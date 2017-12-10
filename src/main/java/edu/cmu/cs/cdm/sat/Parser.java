package edu.cmu.cs.cdm.sat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Tianyu on 12/5/17.
 */
public final class Parser {
    public static Formula parseCnf(String filename) throws IOException{
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            Formula result = new Formula();
            int id = 0;
            for (String line = in.readLine(); line != null; line = in.readLine(), id++) {
                result.add(parseClause(line, id));
            }
            return result;
        }
    }

    private static Clause parseClause(String clause, int id) {
        try (Scanner scanner = new Scanner(clause)) {
            List<Integer> result = new ArrayList<>();
            while (scanner.hasNext()) {
                String lit = scanner.next();
                result.add(Props.formLiteral(Props.fromName(lit.replaceAll("!", "")),
                        !lit.startsWith("!")));
            }
            return new Clause(result, id);
        }
    }

    // TODO add more parsing functions
    private Parser() {}
}
