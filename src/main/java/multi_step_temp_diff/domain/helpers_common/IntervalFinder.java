package multi_step_temp_diff.domain.helpers_common;


import common.Counter;
import common.MathUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/***
 *    This class finds the index of an interval. A vector yVec with elements y is one input.
 *    An example yVec can be
 *    0     0.2     0.4             0.8     1
 *    There are 4 intervals in this example vector, the first is between 0 and 0.2. The second between 0.2 and 0.4.
 *    And so on.
 *    The objective is to identify what interval a number x belongs to.
 *    Following is required:  1) x>=min(yVec), 2) x=<max(yVec)
 *
 * Thanks to interval halving the method is fast, it converges with log2(nog intervals)
 * For example 2M intervals gives approx 20 iterations.

 Pseudocode:

  left=0, right=yVec.size-1
  while (abs(left-right)<=1)  {
        mid=ceil(left+right)/2)
        if (isMidRightLargerThanX(mid,x) ) {
            right=mid
         else
            left=mid;
 }

 if (abs(left-right)=1)  {
    interval=getIntervalLeftOrRight(left,right)
 } else {
    interval=mid;
 }

 }

 * Example execution for x=0.75 and y as above
 *  left=0;  right=3; -> mid=2  -> isMidRightLargerThanX = (0.8>0.75) = true, isMidLeftSmallerThanX = (0.4<0.75) = true
 *    => STOP!  x=0.75 is in interval 2
 *
 * Example execution for x=0.1 and y as above
 *  left=0;  right=3; -> mid=2  -> isMidRightLargerThanX = (0.8>0.1) = true, isMidLeftSmallerThanX = (0.4<0.1) = false
 *     => left=0, right = mid = 2
 *  left=0;  right=2; -> mid=1 -> isMidRightLargerThanX = (0.4>0.1) = true, isMidLeftSmallerThanX = (0.2<0.1) = false
 *    => left=0, right = mid = 1
 *    abs(left-right)<=1  => STOP!!  isMidRightLargerThanX and isMidLeftSmallerThanX is true for interval = left = 0
 *    => x=0.0 is in interval 0
 */

public class IntervalFinder {

    List<Double> yVec;
    Integer nofIterations;


    BiPredicate<Integer, Double> isRightSideOfIntervalLargerThanX = (m, x) -> yVec.get(m + 1) > x;
    BiPredicate<Integer, Double> isLeftSideOfIntervalSmallerThanX = (m, x) -> yVec.get(m) < x;
    BiPredicate<Integer, Integer> isLeftAndRightClose = (l, r) -> Math.abs(l - r) <= 1;
    BiPredicate<Integer, Integer> isOneBetweenLeftAndRight = (l, r) -> MathUtils.isZero(Math.abs(l - r) - 1);
    BiFunction<Integer, Integer, Integer> calcMid = (l, r) -> (int) Math.ceil((l + r) / 2d);

    public IntervalFinder(List<Double> yVec) {
        this.yVec = yVec;
        this.nofIterations = 0;
    }

    public int find(double x) {

        int left = 0, right = yVec.size() - 1;
        Counter counter = new Counter();

        int mid = calcMid.apply(left, right);
        while (!isLeftAndRightClose.test(left,right)) {
            mid = calcMid.apply(left, right);
            if (isRightSideOfIntervalLargerThanX.test(mid, x)) {
                right = mid;
            } else {
                left = mid;
            }
            counter.increase();
        }

        nofIterations = counter.getCount();

        return isOneBetweenLeftAndRight.test(left, right)
                ? getIntervalLeftOrRight(left, right,x)
                : mid;

    }

        private int getIntervalLeftOrRight(int left, int right, double x) {
            return isLeftSideOfIntervalSmallerThanX.test(left, x) &&  isRightSideOfIntervalLargerThanX.test(left, x)
                    ? left
                    : right;
        }


}


