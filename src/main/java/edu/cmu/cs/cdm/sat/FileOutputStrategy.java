package edu.cmu.cs.cdm.sat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileOutputStrategy implements OutputStrategy {
    private BufferedWriter outFile;

    public FileOutputStrategy(String filename) throws IOException {
        outFile = new BufferedWriter(new FileWriter(filename));
    }

    @Override
    public void log(String message) throws IOException {
        outFile.write(message);
        outFile.newLine();
    }

    @Override
    public void close() throws IOException {
        outFile.close();
    }
}
