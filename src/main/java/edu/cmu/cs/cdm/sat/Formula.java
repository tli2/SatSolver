package edu.cmu.cs.cdm.sat;

import edu.cmu.cs.cdm.sat.Annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tianyu on 12/8/17.
 */
public class Formula {
    public static final @SatValue int SAT = 3;
    public static final @SatValue int CONFLICT = 4;
    public static final @SatValue int UNRESOLVED = 5;
    private List<Clause> clauses = new ArrayList<>();

    Formula add(Clause clause) {
        clauses.add(clause);
        return this;
    }

    @SatValue int eval(@PropValue int[] val, boolean propagate, OutputStrategy out) throws IOException {
        for (Clause c : clauses) {
            switch (c.sat(val, propagate, out)) {
                case SAT:
                    break;
                case CONFLICT:
                    return CONFLICT;
                case UNRESOLVED:
                    return UNRESOLVED;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return SAT;
    }


    List<Clause> getClauses() {
        return clauses;
    }
}
