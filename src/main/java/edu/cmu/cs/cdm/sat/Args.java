package edu.cmu.cs.cdm.sat;

import com.beust.jcommander.Parameter;

/**
 * Command line arguments for the SAT solver
 */
public final class Args {

    @Parameter(names = "-input", description = "input SAT formula in CNF form", required = true)
    private String input;

    @Parameter(names = "-output", description = "output file for execution trace")
    private String output;

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }
}
