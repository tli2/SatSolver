package edu.cmu.cs.cdm.sat;

import com.beust.jcommander.JCommander;

import java.io.IOException;
import java.util.*;

/**
 * Created by Tianyu on 11/28/17.
 */
public class SatSolver {
    public static void main(String[] args) throws IOException {
        Args parsedArgs = new Args();
        new JCommander(parsedArgs, args);
        solve(Parser.parseCnf(parsedArgs.getInput()));
    }

    private static void solve(Defs.Formula formula) {
        System.out.printf("solving %d clauses with %d variables%n", formula.getClauses().size(), Defs.Prop.getNumProps());
        int[] assignment = new int[Defs.Prop.getNumProps()];
        for (int i = 0; i < assignment.length; i++)
            assignment[i] = Defs.UNASSIGNED;
        if (resolve(formula, assignment, 0, Defs.TRUE, true)) {
            System.out.println("Sat:");
            printSolution(assignment);
        }
    }

    private static void printSolution(int[] assignment) {
        System.out.println("solution found: ");
        for (int i = 0 ; i < assignment.length; i++) {
            System.out.printf("%s: %s%n", Defs.Prop.getName(i), printAssignment(assignment[i]));
        }
    }

    private static String printAssignment(int assn) {
        switch (assn) {
            case Defs.FALSE:
                return "false";
            case Defs.TRUE:
                return "true";
            case Defs.UNASSIGNED:
                return "either";
            default:
                throw new IllegalArgumentException();
        }
    }

    private static boolean resolve(Defs.Formula formula,
                                   int[] assignment,
                                   int prop,
                                   int val,
                                   boolean propagate) {
        if (assignment[prop] != Defs.UNASSIGNED)
            return resolve(formula, assignment, prop + 1, Defs.TRUE, propagate);
        System.out.printf("Trying %s for %s%n", printAssignment(val), Defs.Prop.getName(prop));
        assignment[prop] = val;
        switch (formula.eval(assignment, propagate)) {
            case Defs.SAT:
                return true;
            case Defs.CONFLICT:
                System.out.println("Conflict, back tracking");
                for (int i = prop; i < assignment.length; i++)
                    assignment[i] = Defs.UNASSIGNED;
                return val == Defs.TRUE && resolve(formula, assignment, prop, Defs.FALSE, propagate);
            case Defs.UNRESOLVED:
                boolean rec = resolve(formula, assignment, prop + 1, Defs.TRUE, propagate);
                if (rec)
                    return true;
                else {
                    System.out.println("Conflict, back tracking");
                    for (int i = prop; i < assignment.length; i++)
                        assignment[i] = Defs.UNASSIGNED;
                    return val == Defs.TRUE && resolve(formula, assignment, prop, Defs.FALSE, propagate);
                }
            default:
                throw new IllegalArgumentException();
        }
    }
}
