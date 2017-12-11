package edu.cmu.cs.cdm.sat;

import com.beust.jcommander.Parameter;
import org.junit.runners.Parameterized;

/**
 * Command line arguments for the SAT solver
 */
public final class Args {

    @Parameter(names = "-input", description = "input SAT formula in CNF form", required = true)
    private String input;

    @Parameter(names = "-output", description = "output file for execution trace")
    private String output;

    @Parameter(names = "-stepbystep", description = "whether to watch the sat solver run step by step")
    private boolean stepbystep = false;

    @Parameter(names = "-solver", description = "either simple (recursive backtracking with no unit propagation), " +
            "                                   dpll or cdcl")
    private String solver = "dpll";

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public boolean stepByStep() {
        return stepbystep;
    }

    public String getSolver() {
        return solver;
    }
}
