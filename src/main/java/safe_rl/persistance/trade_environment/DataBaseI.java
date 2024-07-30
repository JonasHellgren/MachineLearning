package safe_rl.persistance.trade_environment;

import java.util.List;

public  interface DataBaseI<I, D> {

    void create(D data);
    D read(I id);
    boolean exists(I id);
    List<I> getIds();
    void clear();
    int size();
}
