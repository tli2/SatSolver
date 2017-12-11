package edu.cmu.cs.cdm.sat.solver.cdcl;

import com.google.common.graph.MutableValueGraph;
import edu.cmu.cs.cdm.sat.solver.Annotations.*;

import java.util.Map;

public class ConflictGraph {
    private MutableValueGraph<@Literal Integer, /* clause id */ Integer> conflictGraph;
    private Map<@Literal Integer, @Prop Integer> decisionLevel;
}
