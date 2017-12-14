package edu.cmu.cs.cdm.sat;

import com.beust.jcommander.JCommander;
import edu.cmu.cs.cdm.sat.solver.SatSolver;
import edu.cmu.cs.cdm.sat.solver.cdcl.CdclSatSolver;
import edu.cmu.cs.cdm.sat.solver.dpll.DpllFormula;
import edu.cmu.cs.cdm.sat.solver.dpll.DpllSatSolver;

import java.io.IOException;
import java.util.Scanner;

public final class Main {
    public static void main(String[] args) throws IOException {
        Args parsedArgs = new Args();
        new JCommander(parsedArgs, args);
        OutputStrategy out = parsedArgs.getOutput() == null ?
                new CommandlineOutputStrategy() : new FileOutputStrategy(parsedArgs.getOutput());
        SatSolver solver;
        switch (parsedArgs.getSolver()) {
            case "simple":
            case "dpll":
                DpllFormula formula = new DpllFormula(Parser.parseCnf(parsedArgs.getInput()));
                solver = new DpllSatSolver(formula, out, parsedArgs.getSolver().equals("dpll"));
                break;
            case "cdcl":
                solver = new CdclSatSolver(Parser.parseCnf(parsedArgs.getInput()), out);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized solver: " + parsedArgs.getSolver());
        }

        if (parsedArgs.stepByStep()) {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    if (scanner.nextLine().equals("p")) {
                        solver.print();
                    } else if (!solver.step()) break;
                }
            }
        } else {
            while (true) {
                if (!solver.step()) break;
            }
        }
        out.close();
    }

    private Main() {}
}
