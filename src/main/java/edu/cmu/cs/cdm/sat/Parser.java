package edu.cmu.cs.cdm.sat;

import edu.cmu.cs.cdm.sat.solver.Props;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static edu.cmu.cs.cdm.sat.solver.Annotations.*;

/**
 * Created by Tianyu on 12/5/17.
 */
public final class Parser {
    public static List<List<@Literal Integer>> parseCnf(String filename) throws IOException{
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            List<List<@Literal Integer>> result = new ArrayList<>();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                result.add(parseClause(line));
            }
            return result;
        }
    }

    private static List<@Literal Integer> parseClause(String clause) {
        try (Scanner scanner = new Scanner(clause)) {
            List<Integer> result = new ArrayList<>();
            while (scanner.hasNext()) {
                String lit = scanner.next();
                result.add(Props.formLiteral(Props.fromName(lit.replaceAll("!", "")),
                        !lit.startsWith("!")));
            }
            return result;
        }
    }

    private Parser() {}
}
