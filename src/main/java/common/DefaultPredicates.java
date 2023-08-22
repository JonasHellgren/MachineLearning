package common;

import java.util.Objects;
import java.util.function.BiFunction;

public class DefaultPredicates {

    public static  BiFunction<String,String,String > defaultIfNullString=(v, d) -> Objects.isNull(v)?d:v;
    public static BiFunction<Integer,Integer,Integer> defaultIfNullInteger=(v, d) -> Objects.isNull(v)?d:v;
    public static BiFunction<Double,Double,Double> defaultIfNullDouble=(v, d) -> Objects.isNull(v)?d:v;
    public static BiFunction<Object,Object,Object> defaultIfNullObject=(v, d) -> Objects.isNull(v)?d:v;

}
