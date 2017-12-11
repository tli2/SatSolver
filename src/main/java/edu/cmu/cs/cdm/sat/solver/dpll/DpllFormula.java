package edu.cmu.cs.cdm.sat.solver.dpll;

import edu.cmu.cs.cdm.sat.OutputStrategy;
import edu.cmu.cs.cdm.sat.solver.Annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tianyu on 12/8/17.
 */
public class DpllFormula {
    public static final @SatValue int SAT = 3;
    public static final @SatValue int CONFLICT = 4;
    public static final @SatValue int UNRESOLVED = 5;
    private List<DpllClause> clauses = new ArrayList<>();

    public DpllFormula(List<List<@Literal Integer>> clauses) {
        for (int i = 0; i < clauses.size(); i++) {
            this.clauses.add(new DpllClause(clauses.get(i), i));
        }
    }

    public @SatValue int eval(@PropValue int[] val, boolean propagate, OutputStrategy out) throws IOException {
        @SatValue int result = SAT;
        for (DpllClause c : clauses) {
            switch (c.sat(val, propagate, out)) {
                case SAT:
                    break;
                case CONFLICT:
                    return CONFLICT;
                case UNRESOLVED:
                    result = UNRESOLVED;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return result;
    }


    List<DpllClause> getClauses() {
        return clauses;
    }
}
