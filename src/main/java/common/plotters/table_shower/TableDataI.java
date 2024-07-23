package common.plotters.table_shower;

/**
 * SOLID: open for extension (more potential future classes can use this interface)
 */
public interface TableDataI {
    String read(int x, int y);
    int nX();
    int nY();
}
