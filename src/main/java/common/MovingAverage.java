package common;

import java.util.*;

/***
 * https://www.youtube.com/watch?v=-1PlYrjLz5E
 */

public class MovingAverage {

    int lengthWindow;
    List<Double> inList;
    Deque<Double> deque;

    public MovingAverage(int lengthWindow,List<Double> inList) {
        this.lengthWindow = lengthWindow;
        this.inList=inList;
        this.deque=new LinkedList<>();
    }

    public List<Double> getFiltered() {
        List<Double> outList=new ArrayList<>();
        for (double value:inList) {
            outList.add(next(value));
        }
        return outList;
    }

    private double next(double value) {
        if (deque.size() == lengthWindow) {
            deque.removeFirst();
        }
        deque.addLast(value);
        return sumOfQue(deque)/deque.size();
    }


    private double sumOfQue(Deque<Double> deque) {
        double windowSum = 0;
        for (Double aDouble : deque) {
            windowSum = windowSum + aDouble;
        }
        return windowSum;
    }


}
