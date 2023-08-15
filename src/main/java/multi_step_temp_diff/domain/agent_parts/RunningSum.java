package multi_step_temp_diff.domain.agent_parts;

import java.util.ArrayList;
import java.util.List;

/***
 * Thanks to generics (T) both integers and double list can be managed

 * Example:
 * numberList=[5,10,15,20]  -> resultList = [5,15,30,50]
 */

public class RunningSum<T extends Number> {

    List<T> numberList;

    public RunningSum(List<T> numberList) {
        this.numberList = numberList;
    }

    public List<Double> calculate() {
        List<Double> resultList=new ArrayList<>();
        double partSum=0d;
        for (T num:numberList) {
            partSum+=num.doubleValue();
            resultList.add(partSum);
        }
        return resultList;
    }

}
