package edu.cmu.cs.cdm.sat;

import java.io.IOException;

public interface OutputStrategy {
    void log(String message) throws IOException;
    void close() throws IOException;
}
