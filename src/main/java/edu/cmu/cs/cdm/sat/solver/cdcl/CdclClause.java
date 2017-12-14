package edu.cmu.cs.cdm.sat.solver.cdcl;


import edu.cmu.cs.cdm.sat.solver.Annotations;

import java.util.Collection;

import static edu.cmu.cs.cdm.sat.solver.Annotations.*;


public class CdclClause {
    static final @SatValue int SAT = 3;
    static final @SatValue int CONFLICT = 4;
    static final @SatValue int UNRESOLVED = 5;

    @Literal int[] lits;
    int id;

    CdclClause(Collection<@Annotations.Literal Integer> lits, int id) {
        this.lits = lits.stream().mapToInt(Integer::intValue).toArray();
        this.id = id;
    }
}
