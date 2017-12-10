package edu.cmu.cs.cdm.sat;

import com.beust.jcommander.JCommander;

import java.io.IOException;
import java.util.Scanner;

public final class Main {
    public static void main(String[] args) throws IOException {
        Args parsedArgs = new Args();
        new JCommander(parsedArgs, args);
        OutputStrategy out = parsedArgs.getOutput() == null ?
                new CommandlineOutputStrategy() : new FileOutputStrategy(parsedArgs.getOutput());
        SatSolver solver = new SatSolver(
                Parser.parseCnf(parsedArgs.getInput()),
                out,
                parsedArgs.useUnitProp());
        if (parsedArgs.stepByStep()) {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    scanner.nextLine();
                    if (!solver.step()) break;
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
