package monte_carlo_tree_search.classes;

public class Counter {

    private int count;
    private final int maxCount;

    public Counter(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getCount() {
        return count;
    }

    public void increase() {
        count++;
    }

    public boolean isExceeded() {
        return count==maxCount;
    }

    public void reset() {
        count=0;
    }

}
