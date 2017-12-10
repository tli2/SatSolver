package edu.cmu.cs.cdm.sat;

public class SatSolver {


    public void solve(Formula formula) {
        System.out.printf("solving %d clauses with %d variables%n", formula.getClauses().size(), Props.getNumProps());
        int[] assignment = new int[Props.getNumProps()];
        for (int i = 0; i < assignment.length; i++)
            assignment[i] = i;
        SatState state = new SatState(formula, assignment);
        state.tryNextVar();
        while (true) {
            switch (formula.eval(state.getAssignment(), true)) {
                case Formula.SAT:
                    printSolution(state.getAssignment());
                    return;
                case Formula.CONFLICT:
                    System.out.println("Conflict");
                    if (!state.tryNextBranch()) {
                        while (!state.tryNextBranch()) {
                            if (!state.backtrack()) {
                                System.out.println("No solution");
                                return;
                            }
                        }
                    }
                    break;
                case Formula.UNRESOLVED:
                    if (!state.tryNextVar()) {
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

    private static void printSolution(int[] assignment) {
        System.out.println("solution found: ");
        for (int i = 0 ; i < assignment.length; i++) {
            System.out.printf("%s: %s%n", Props.getName(i), printAssignment(assignment[i]));
        }
    }
}
