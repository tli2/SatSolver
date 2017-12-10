package edu.cmu.cs.cdm.sat;

import edu.cmu.cs.cdm.sat.Annotations.*;

public class SatState {
    private Formula formula;
    private @Prop int[] mapping;
    private @PropValue int[] assignment;
    private @Prop int curr = -1;

    public SatState(Formula formula, @Prop int[] mapping) {
        this.formula = formula;
        this.mapping = mapping;
        assignment = new int[mapping.length];
        for (int i = 0; i < assignment.length; i++) {
            assignment[i] = Props.UNASSIGNED;
        }
    }

    public Formula getFormula() {
        return formula;
    }

    public boolean tryNextVar() {
        curr++;
        if (curr >= mapping.length)
            return false;
        System.out.printf("Trying true for %s%n", Props.getName(mapping[curr]));
        if (assignment[curr] != Props.UNASSIGNED)
            tryNextVar();
        assignment[mapping[curr]] = Props.TRUE;
        return true;
    }

    public boolean tryNextBranch() {
        if (assignment[mapping[curr]] == Props.FALSE) {
            return false;
        }
        System.out.printf("Trying false for %s%n", Props.getName(mapping[curr]));
        assignment[mapping[curr]] = Props.FALSE;
        for (int i = curr + 1; i < mapping.length; i++) {
            assignment[mapping[i]] = Props.UNASSIGNED;
        }
        return true;
    }

    public boolean backtrack() {
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
