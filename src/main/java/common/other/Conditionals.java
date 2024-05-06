package common.other;

import lombok.NoArgsConstructor;

public class Conditionals {

    /**
     * ifTrueMethod is executed if condition is true, else is ifFalseMethod executed
     * Handy to avoid ugly branching: if (condition) { trueMethod() } else { falseMethod() }
     * can be replaced by cleaner executeDependantOnCondition(condition,trueMethod,falseMethod)
     */
    public static void executeOneOfTwo(boolean condition, Runnable ifTrueMethod, Runnable ifFalseMethod) {
        (condition ? ifTrueMethod : ifFalseMethod).run();
    }

    /**
     * Following method is used if we want to run ifTrueMethod only if condition is true
     */
    public static void executeIfTrue(boolean condition, Runnable ifTrueMethod) {
        executeOneOfTwo(condition, ifTrueMethod, () -> {});
    }

    public static void executeIfFalse(boolean condition, Runnable ifTrueMethod) {
        executeOneOfTwo(!condition, ifTrueMethod, () -> {});
    }
}
