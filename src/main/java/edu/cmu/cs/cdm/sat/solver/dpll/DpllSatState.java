package edu.cmu.cs.cdm.sat.solver.dpll;

import edu.cmu.cs.cdm.sat.OutputStrategy;
import edu.cmu.cs.cdm.sat.solver.Annotations.*;
import edu.cmu.cs.cdm.sat.solver.Props;

import java.io.IOException;

public class DpllSatState {
    private DpllFormula formula;
    private @Prop int[] mapping;
    private @PropValue int[] assignment;
    private @Prop int curr = -1;

    public DpllSatState(DpllFormula formula, @Prop int[] mapping) {
        this.formula = formula;
        this.mapping = mapping;
        assignment = new int[mapping.length];
        for (int i = 0; i < assignment.length; i++) {
            assignment[i] = Props.UNASSIGNED;
        }
    }

    public DpllFormula getFormula() {
        return formula;
    }

    public boolean tryNextVar(OutputStrategy output) throws IOException {
        curr++;
        if (curr >= mapping.length)
            return false;
        if (assignment[curr] != Props.UNASSIGNED)
            return tryNextVar(output);
        else {
            output.log(String.format("Trying true for %s%n", Props.getName(mapping[curr])));
            assignment[mapping[curr]] = Props.TRUE;
            return true;
        }
    }

    public boolean tryNextBranch(OutputStrategy output) throws IOException {
        if (curr < 0 || assignment[mapping[curr]] == Props.FALSE) {
            return false;
        }
        output.log(String.format("Trying false for %s%n", Props.getName(mapping[curr])));
        assignment[mapping[curr]] = Props.FALSE;
        for (int i = curr + 1; i < mapping.length; i++) {
            assignment[mapping[i]] = Props.UNASSIGNED;
        }
        return true;
    }

    public boolean backtrack() {
        if (curr < 0) {
            return false;
        }
        for (int i = curr; i < mapping.length; i++) {
            assignment[mapping[i]] = Props.UNASSIGNED;
        }
        curr--;
        return curr >= 0;
    }

    public @PropValue int[] getAssignment() {
        return assignment;
    }

    public @PropValue int getAssignmentFor(@Prop int prop) {
        return assignment[prop];
    }

    public void assign(@Prop int prop, @PropValue int value) {
        assignment[prop] = value;
    }


}
