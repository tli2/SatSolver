package edu.cmu.cs.cdm.sat;

import java.util.*;



/**
 * Created by Tianyu on 12/3/17.
 */
public final class Defs {
    public static final int FALSE = 0, TRUE = 1, UNASSIGNED = 2,
             SAT = 3, CONFLICT = 4, UNRESOLVED = 5;

    public static class Prop {
        private static Map<String, Integer> varMap = new HashMap<>();

        public static int fromName(String name) {
            return varMap.computeIfAbsent(name, k -> varMap.size());
        }

        public static int getNumProps() {
            return varMap.size();
        }

        public static String getName(int var) {
            return varMap.entrySet().stream()
                    .filter(e -> e.getValue() == var).findAny().get().getKey();
        }

        private Prop() {}
    }

    public static class Lit {
        public static int formLiteral(int prop, boolean sign) {
            int flag = sign ? 1 : 0;
            return (prop << 1) + flag;
        }

        public static int getProp(int lit) {
            return lit >> 1;
        }

        public static boolean getSign(int lit) {
            return lit % 2 == 1;
        }

        private Lit() {}
    }

    public static class Clause {
        private int[] lits;

        Clause(Collection<Integer> lits) {
            this.lits = lits.stream().mapToInt(Integer::intValue).toArray();
        }

        int sat(int[] val, boolean propagate) {
            int unassignedCount = 0;
            int unassignedLit = -1;

            for (int lit : lits) {
                int prop = Lit.getProp(lit);
                boolean sign = Lit.getSign(lit);

                switch (val[prop]){
                    case FALSE:
                        if (!sign) return SAT;
                        break;
                    case TRUE:
                        if (sign) return SAT;
                        break;
                    case UNASSIGNED:
                        unassignedCount++;
                        unassignedLit = lit;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            if (unassignedCount == 1 && propagate) {
                val[Lit.getProp(unassignedLit)] = Lit.getSign(unassignedLit) ? TRUE : FALSE;
                System.out.println("Unit propagate " + Prop.getName(Lit.getProp(unassignedLit)) +  " to " + Lit.getSign(unassignedLit));
                return SAT;
            }
            return unassignedCount == 0 ? CONFLICT : UNRESOLVED;
        }
    }



    public static class Formula {
        private List<Clause> clauses = new ArrayList<>();

        Formula add(Clause clause) {
            clauses.add(clause);
            return this;
        }

        int eval(int[] val, boolean propagate) {
            for (Clause c : clauses) {
                switch (c.sat(val, propagate)) {
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

    private Defs() {}
}
