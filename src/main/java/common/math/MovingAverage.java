package common.math;

import java.util.*;

/***
 * https://www.youtube.com/watch?v=-1PlYrjLz5E
 */

public class MovingAverage {

    int lengthWindow;
    List<Double> inList;
    Deque<Double> deque;

    public MovingAverage(int lengthWindow, List<Double> inList) {
        this.lengthWindow = lengthWindow;
        this.inList = inList;
        this.deque = new LinkedList<>();
    }

    public List<Double> getFiltered() {
        List<Double> outList = new ArrayList<>();
        for (double value : inList) {
            outList.add(addValue(value));
        }
        return outList;
    }

    public double addValue(double value) {
        if (filled()) {
            deque.removeFirst();
        }
        deque.addLast(value);
        return average();
    }

    public double average() {
        return deque.isEmpty() ? 0 : sumOfQue(deque) / deque.size();
    }

    public boolean filled() {
        return deque.size() == lengthWindow;
    }


    private double sumOfQue(Deque<Double> deque) {
        double windowSum = 0;
        for (Double aDouble : deque) {
            windowSum = windowSum + aDouble;
        }
        return windowSum;
    }


}
