package edu.cmu.cs.cdm.sat.solver.dpll;

import edu.cmu.cs.cdm.sat.OutputStrategy;
import edu.cmu.cs.cdm.sat.solver.Props;
import edu.cmu.cs.cdm.sat.solver.SatSolver;


import java.io.IOException;

public class DpllSatSolver implements SatSolver {
    private OutputStrategy out;
    private boolean propagate;
    private DpllFormula formula;
    private DpllSatState state;

    public DpllSatSolver(DpllFormula formula, OutputStrategy out, boolean propagate) throws IOException {
        this.out = out;
        this.propagate = propagate;
        this.formula = formula;
        out.log(String.format("solving %d clauses with %d variables%n",
                formula.getClauses().size(), Props.getNumProps()));
        int[] assignment = new int[Props.getNumProps()];
        for (int i = 0; i < assignment.length; i++)
            assignment[i] = i;
        state = new DpllSatState(formula, assignment);
    }

    @Override
    public boolean step() throws IOException {
        switch (formula.eval(state.getAssignment(), propagate, out)) {
            case DpllFormula.SAT:
                out.log("solution found:\n");
                printSolution(state.getAssignment(), out);
                return false;
            case DpllFormula.CONFLICT:
                while (!state.tryNextBranch(out)) {
                    if (!state.backtrack(out)) {
                        out.log("No solution\n");
                        return false;
                    }
                }
                break;
            case DpllFormula.UNRESOLVED:
                if (!state.tryNextVar(out)) {
                    out.log("No solution\n");
                    return false;
                }
                break;
        }
        return true;
    }

    private static String printAssignment(int assn) {
        switch (assn) {
            case Props.FALSE:
                return "false";
            case Props.TRUE:
                return "true";
            case Props.UNASSIGNED:
                return "either";
            default:
                throw new IllegalArgumentException();
        }
    }

    private static void printSolution(int[] assignment, OutputStrategy out) throws IOException {
        for (int i = 0 ; i < assignment.length; i++) {
            out.log(String.format("%s: %s%n", Props.getName(i), printAssignment(assignment[i])));
        }
    }

    @Override
    public void print() throws IOException {
        printSolution(state.getAssignment(), out);
    }
}
