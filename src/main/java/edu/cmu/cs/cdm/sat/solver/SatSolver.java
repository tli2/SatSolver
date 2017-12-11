package edu.cmu.cs.cdm.sat.solver;

import java.io.IOException;

public interface SatSolver {
    boolean step() throws IOException;
}
