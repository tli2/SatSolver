package edu.cmu.cs.cdm.sat.solver;

import edu.cmu.cs.cdm.sat.solver.Annotations.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tianyu on 12/8/17.
 */
public final class Props {
    public static final @PropValue int FALSE = 0;
    public static final @PropValue int TRUE = 1;
    public static final @PropValue int UNASSIGNED = 2;
    private static Map<String, @Prop Integer> varMap = new HashMap<>();
    private static Map<@Prop Integer, String> propMap = new HashMap<>();

    public static @Prop int fromName(String name) {
        @Prop int prop = varMap.computeIfAbsent(name, k -> varMap.size());
        propMap.put(prop, name);
        return prop;
    }

    public static int getNumProps() {
        return varMap.size();
    }

    public static String getName(@Prop int var) {
        return propMap.get(var);
    }

    private Props() {}

    public static @Literal int formLiteral(@Prop int prop, boolean sign) {
        int flag = sign ? 1 : 0;
        return (prop << 1) + flag;
    }

    public static @Prop int getProp(@Literal int lit) {
        return lit >> 1;
    }

    public static boolean getSign(@Literal int lit) {
        return lit % 2 == 1;
    }
}
