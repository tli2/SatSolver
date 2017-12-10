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

    @Parameter(names = "-nounitprop", description = "do not use unit propagation")
    private boolean nounitpop = false;

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public boolean useUnitProp() {
        return !nounitpop;
    }

    public boolean stepByStep() {
        return stepbystep;
    }
}
