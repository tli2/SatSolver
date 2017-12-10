package edu.cmu.cs.cdm.sat;

import java.io.IOException;

public class CommandlineOutputStrategy implements OutputStrategy {
    @Override
    public void log(String message) throws IOException {
        System.out.print(message);
    }

    @Override
    public void close() throws IOException {}
}
