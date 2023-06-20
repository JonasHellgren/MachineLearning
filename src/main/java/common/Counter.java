package common;

public class Counter {

    private int count;
    private final int minCount;
    private final int maxCount;

    public Counter(int minCount, int maxCount) {

        if (maxCount<minCount) {
            throw new IllegalArgumentException("maxCount<minCount");
        }

        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public int getCount() {
        return count;
    }

    public void increase() {
        count++;
    }

    public boolean isExceeded() {
        return count>=maxCount;
    }

    public boolean isBelowMinCount() {
        return count<minCount;
    }

    public void reset() {
        count=0;
    }

    @Override
    public String toString() {
        return Integer.toString(count);
    }


}
