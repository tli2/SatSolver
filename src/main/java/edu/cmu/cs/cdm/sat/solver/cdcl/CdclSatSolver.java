package edu.cmu.cs.cdm.sat.solver.cdcl;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import edu.cmu.cs.cdm.sat.OutputStrategy;
import edu.cmu.cs.cdm.sat.solver.Annotations.Literal;
import edu.cmu.cs.cdm.sat.solver.Props;
import edu.cmu.cs.cdm.sat.solver.SatSolver;

import javax.print.attribute.IntegerSyntax;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static edu.cmu.cs.cdm.sat.solver.Annotations.*;
import static edu.cmu.cs.cdm.sat.solver.cdcl.CdclClause.SAT;
import static edu.cmu.cs.cdm.sat.solver.cdcl.CdclClause.CONFLICT;
import static edu.cmu.cs.cdm.sat.solver.cdcl.CdclClause.UNRESOLVED;

public class CdclSatSolver implements SatSolver {
    private OutputStrategy out;

    /* Sat solving state*/
    private List<CdclClause> clauses;
    private @PropValue int[] assignment;
    private @Prop int curr = -1;

    /* Conflict graph */
    private MutableValueGraph<@Literal Integer, /* clause id */ Integer> conflictGraph =
            ValueGraphBuilder.directed().build();
    private Map<@Literal Integer, @Prop Integer> decisionLevels = new HashMap<>();
    private boolean backjumped = false;
    private static final @Literal Integer CONFLICT_NODE = Props.formLiteral(-1, false);

    public CdclSatSolver(List<List<@Literal Integer>> formula, OutputStrategy out) throws IOException {
        this.out = out;
        clauses = new ArrayList<>();
        for (int i = 0; i < formula.size(); i++) {
            clauses.add(new CdclClause(formula.get(i), i));
        }
        assignment = new int[Props.getNumProps()];
        for (int i = 0; i < assignment.length; i++) {
            assignment[i] = Props.UNASSIGNED;
        }
        out.log(String.format("solving %d clauses with %d variables%n",
                formula.size(), Props.getNumProps()));
    }

    @Override
    public boolean step() throws IOException {
        switch (formulaSat()) {
            case SAT:
                out.log("solution found:\n");
                printSolution(assignment, out);
                return false;
            case CONFLICT:
                if (backjumped) {
                    backjumped = false;
                    if (curr == 0 && assignment[0] == Props.FALSE) {
                        return false;
                    }
                } else {
                    while (!tryNextBranch()) {
                        if (!backtrack()) {
                            out.log("No solution\n");
                            return false;
                        }
                    }
                }
                break;
            case UNRESOLVED:
                if (!tryNextVar()) {
                    out.log("No solution\n");
                    return false;
                }
                break;
        }
        return true;
    }

    public boolean tryNextVar() throws IOException {
        curr++;
        if (curr >= assignment.length)
            return false;
        if (assignment[curr] != Props.UNASSIGNED)
            return tryNextVar();
        else {
            out.log(String.format("Trying true for %s%n", Props.getName(curr)));
            assignment[curr] = Props.TRUE;
            decisionLevels.put(Props.formLiteral(curr, true), curr);
            conflictGraph.addNode(Props.formLiteral(curr, true));
            return true;
        }
    }

    public boolean tryNextBranch() throws IOException {
        if (curr < 0 || assignment[curr] == Props.FALSE) {
            return false;
        }
        out.log(String.format("Trying false for %s%n", Props.getName(curr)));
        conflictGraph.addNode(Props.formLiteral(curr, false));
        assignment[curr] = Props.FALSE;
        for (int i = curr + 1; i < assignment.length; i++) {
            assignment[i] = Props.UNASSIGNED;
        }
        return true;
    }

    public boolean backtrack() throws IOException {
        out.log(String.format("backtracking from %s%n", Props.getName(curr)));
        if (curr < 0) {
            return false;
        }
        for (int i = curr; i < assignment.length; i++) {
            switch (assignment[i]) {
                case Props.FALSE:
                    conflictGraph.removeNode(Props.formLiteral(i, false));
                    decisionLevels.remove(Props.formLiteral(i, false));
                    break;
                case Props.TRUE:
                    conflictGraph.removeNode(Props.formLiteral(i, true));
                    decisionLevels.remove(Props.formLiteral(i, true));
                    break;
                case Props.UNASSIGNED:
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            assignment[i] = Props.UNASSIGNED;
        }
        curr--;
        return curr >= 0;
    }

    @SatValue int formulaSat() throws IOException {
        @SatValue int result = SAT;
        for (CdclClause clause : clauses) {
            switch (clauseSat(clause)) {
                case SAT:
                    break;
                case CONFLICT:
                    return CONFLICT;
                case UNRESOLVED:
                    result = UNRESOLVED;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return result;
    }

    @SatValue int clauseSat(CdclClause clause) throws IOException {
        int unassignedCount = 0;
        @Literal int unassignedLit = -1;
        for (@Literal int lit : clause.lits) {
            @Prop int prop = Props.getProp(lit);
            boolean sign = Props.getSign(lit);

            switch (assignment[prop]) {
                case Props.FALSE:
                    if (!sign) return SAT;
                    break;
                case Props.TRUE:
                    if (sign) return SAT;
                    break;
                case Props.UNASSIGNED:
                    unassignedCount++;
                    unassignedLit = lit;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        if (unassignedCount > 1) {
            return UNRESOLVED;
        }
        if (unassignedCount == 0) {
            int level = -1;
            Set<@Literal Integer> propagated = new HashSet<>();
            for (@Literal int lit : clause.lits) {
                int currLevel = decisionLevels.get(Props.opposite(lit));
                if (currLevel > level) {
                    level = currLevel;
                    propagated = new HashSet<>();
                }
                if (currLevel == level && currLevel != Props.getProp(lit)) {
                    propagated.add(lit);
                }
            }

            if (propagated.isEmpty()) {
                out.log(String.format("Conflict on clause %d%n", clause.id + 1));
                return CONFLICT;
            } else {
                unassignedLit = propagated.stream().findAny().get();
            }
        }

        conflictGraph.addNode(unassignedLit);

        @Prop int maxDecision = -1;
        for (@Literal int prev_lit : clause.lits) {
            if (prev_lit != unassignedLit) {
                conflictGraph.putEdgeValue(unassignedLit, Props.opposite(prev_lit), clause.id);
                if (maxDecision < decisionLevels.get(Props.opposite(prev_lit)))
                    maxDecision = decisionLevels.get(Props.opposite(prev_lit));
            }
        }

        decisionLevels.put(unassignedLit, maxDecision);

        // unit propagation
        out.log(String.format("Unit propagate %s to %b on clause %d%n",
                Props.getName(Props.getProp(unassignedLit)),
                Props.getSign(unassignedLit), clause.id + 1));
        assignment[Props.getProp(unassignedLit)] = Props.getSign(unassignedLit) ? Props.TRUE : Props.FALSE;

        // check for conflict
        if (conflictGraph.nodes().contains(Props.opposite(unassignedLit))) {
            // Add conflict edge
            conflictGraph.putEdgeValue(CONFLICT_NODE, unassignedLit, clause.id);
            @Literal int otherDecision = conflictGraph.successors(Props.opposite(unassignedLit)).stream().findAny().get();
            int otherClause = conflictGraph.edgeValue(Props.opposite(unassignedLit), otherDecision);
            conflictGraph.putEdgeValue(CONFLICT_NODE, Props.opposite(unassignedLit), otherClause);

            out.log(String.format("Conflict due to prop %s, propagated on %d and %d%n",
                    Props.getName(Props.getProp(unassignedLit)), clause.id + 1, otherClause + 1));

            // obtain graph cut
            Set<@Literal Integer> subgraph = new HashSet<>(Graphs.reachableNodes(conflictGraph, CONFLICT_NODE));
            subgraph.remove(CONFLICT_NODE);
            Set<@Literal Integer> decisions = subgraph.stream()
                    .filter(l -> Props.getProp(l) == decisionLevels.get(l))
                    .collect(Collectors.toSet());

            // Learn clause
            CdclClause newClause = new CdclClause(decisions.stream().map(Props::opposite).collect(Collectors.toSet()), clauses.size());
            clauses.add(newClause);
            out.log(String.format("Learned clause %s, assigning clause id %d%n", printClause(newClause.lits), newClause.id + 1));

            // backjump
            @Prop int backjumpLevel = decisions.stream().mapToInt(Props::getProp).max().getAsInt();
            Set<@Literal Integer> toRemove = new HashSet<>();
            for (@Literal int lit : subgraph) {
                if (decisionLevels.get(lit) >= backjumpLevel) {
                    assignment[Props.getProp(lit)] = Props.UNASSIGNED;
                    toRemove.add(lit);
                    decisionLevels.remove(lit);
                }
            }
            toRemove.forEach(conflictGraph::removeNode);
            backjumped = true;
            curr = backjumpLevel;
            out.log(String.format("Backjumping to decision of prop %s%n", Props.getName(Props.getProp(backjumpLevel))));
            return CONFLICT;
        }
        return SAT;
    }

    private static String printAssignment(int assn) {
        switch (assn) {
            case Props.FALSE:
                return "false";
            case Props.TRUE:
                return "true";
            case Props.UNASSIGNED:
                return "none";
            default:
                throw new IllegalArgumentException();
        }
    }

    private static String printClause(@Literal int[] clause) {
        StringBuilder builder = new StringBuilder();
        for (@Literal int lit : clause) {
            builder.append(Props.getSign(lit) ? "" : "!");
            builder.append(Props.getName(Props.getProp(lit)));
            builder.append(" ");
        }
        return builder.toString();
    }

    private static void printSolution(int[] assignment, OutputStrategy out) throws IOException {
        for (int i = 0; i < assignment.length; i++) {
            out.log(String.format("%s: %s%n", Props.getName(i), printAssignment(assignment[i])));
        }
    }

    @Override
    public void print() throws IOException {
        printSolution(assignment, out);
    }
}
