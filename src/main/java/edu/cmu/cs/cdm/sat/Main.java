package edu.cmu.cs.cdm.sat;

import com.beust.jcommander.JCommander;

import java.io.IOException;

public final class Main {
    public static void main(String[] args) throws IOException {
        Args parsedArgs = new Args();
        new JCommander(parsedArgs, args);
        SatSolver solver = new SatSolver();
        solver.solve(Parser.parseCnf(parsedArgs.getInput()));
    }

    private Main() {}
}
