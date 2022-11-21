package common;

public class ConditionalUtils {

    /**
     * ifTrueMethod is executed if condition is true, else is ifFalseMethod executed
     * Handy to avoid ugly branching: if (condition) { trueMethod() } else { falseMethod() }
     * can be replaced by cleaner executeDependantOnCondition(condition,trueMethod,falseMethod)
     */
    public static void executeDependantOnCondition(boolean condition, Runnable ifTrueMethod, Runnable ifFalseMethod) {
        (condition ? ifTrueMethod : ifFalseMethod).run();
    }

    /**
     * Following method is used if we want to run ifTrueMethod only if condition is true
     */
    public static void executeOnlyIfConditionIsTrue(boolean condition, Runnable ifTrueMethod) {
        executeDependantOnCondition(condition, ifTrueMethod, () -> {});
    }
}
