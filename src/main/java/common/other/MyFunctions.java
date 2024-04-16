package common.other;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MyFunctions {

    public static BiFunction<String,String,String > defaultIfNullString=(v, d) -> Objects.isNull(v)?d:v;
    public static BiFunction<Integer,Integer,Integer> defaultIfNullInteger=(v, d) -> Objects.isNull(v)?d:v;
    public static BiFunction<Double,Double,Double> defaultIfNullDouble=(v, d) -> Objects.isNull(v)?d:v;
    public static BiFunction<Object,Object,Object> defaultIfNullObject=(v, d) -> Objects.isNull(v)?d:v;

    public static Function<Double,Double> sqr2 =(n) -> Math.pow(n,2);
    public static Function<Double,Double> sqr3=(n) -> Math.pow(n,3);
    public static BiFunction<Boolean,Double,Double> zeroIfTrueElseNum=(cond,num) -> (cond) ? 0 : num;
    public static BiFunction<Double,Double,Double> secondArgIfSmaller =(num, elseNum) ->
            (Math.abs(num)< elseNum) ? elseNum :num;


}
