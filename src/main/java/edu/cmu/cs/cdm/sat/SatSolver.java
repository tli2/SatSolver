package edu.cmu.cs.cdm.sat;

import java.io.IOException;

public class SatSolver {


    public void solve(Formula formula, OutputStrategy out, boolean propagate) throws IOException {
        out.log(String.format("solving %d clauses with %d variables%n", formula.getClauses().size(), Props.getNumProps()));
        int[] assignment = new int[Props.getNumProps()];
        for (int i = 0; i < assignment.length; i++)
            assignment[i] = i;
        SatState state = new SatState(formula, assignment);
        state.tryNextVar(out);
        while (true) {
            switch (formula.eval(state.getAssignment(), propagate, out)) {
                case Formula.SAT:
                    printSolution(state.getAssignment(), out);
                    return;
                case Formula.CONFLICT:
                    System.out.println("Conflict");
                    if (!state.tryNextBranch(out)) {
                        while (!state.tryNextBranch(out)) {
                            if (!state.backtrack()) {
                                System.out.println("No solution");
                                return;
                            }
                        }
                    }
                    break;
                case Formula.UNRESOLVED:
                    if (!state.tryNextVar(out)) {
                        System.out.println("No solution");
                        return;
                    }
                    break;
            }
        }
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
        out.log("solution found: ");
        for (int i = 0 ; i < assignment.length; i++) {
            out.log(String.format("%s: %s%n", Props.getName(i), printAssignment(assignment[i])));
        }
    }
}
