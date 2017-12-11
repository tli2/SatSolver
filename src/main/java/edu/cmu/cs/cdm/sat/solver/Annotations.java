package edu.cmu.cs.cdm.sat.solver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotations {

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, ElementType.METHOD, ElementType.PARAMETER})
    public @interface Prop {}

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, ElementType.METHOD, ElementType.PARAMETER})
    public @interface Literal {}

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, ElementType.METHOD, ElementType.PARAMETER})
    public @interface PropValue {}

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, ElementType.METHOD, ElementType.PARAMETER})
    public @interface SatValue {}
}
