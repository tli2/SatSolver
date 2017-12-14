package edu.cmu.cs.cdm.sat.solver.dpll;

import edu.cmu.cs.cdm.sat.OutputStrategy;
import edu.cmu.cs.cdm.sat.solver.Annotations.*;
import edu.cmu.cs.cdm.sat.solver.Props;

import java.io.IOException;
import java.util.Collection;

import static edu.cmu.cs.cdm.sat.solver.dpll.DpllFormula.*;

/**
 * Created by Tianyu on 12/8/17.
 */
public final class DpllClause {
    private @Literal int[] lits;
    private int id;

    DpllClause(Collection<@Literal Integer> lits, int id) {
        this.lits = lits.stream().mapToInt(Integer::intValue).toArray();
        this.id = id;
    }

    @SatValue int sat(@PropValue int[] val, boolean propagate, OutputStrategy out) throws IOException {
        int unassignedCount = 0;
        @Literal int unassignedLit = -1;

        for (@Literal int lit : lits) {
            @Prop int prop = Props.getProp(lit);
            boolean sign = Props.getSign(lit);

            switch (val[prop]) {
                case Props.FALSE:
                    if (!sign) return SAT;
                    break;
                case Props.TRUE:
                    if (sign) return SAT;
                    break;
                case Props.UNASSIGNED:
                    unassignedCount++;
                    unassignedLit = lit;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        if (unassignedCount == 1 && propagate) {
            if (val[Props.getProp(unassignedLit)] != Props.UNASSIGNED) {
                out.log(String.format("Conflict on clause %d%n", id));
                return CONFLICT;
            }
            val[Props.getProp(unassignedLit)] = Props.getSign(unassignedLit) ? Props.TRUE : Props.FALSE;
            out.log(String.format("Unit propagate %s to %b on clause %d%n",
                    Props.getName(Props.getProp(unassignedLit)),
                    Props.getSign(unassignedLit), id));
            return SAT;
        }
        if (unassignedCount == 0) {
            out.log(String.format("Conflict on clause %d%n", id + 1));
            return CONFLICT;
        }
        return UNRESOLVED;
    }
}
