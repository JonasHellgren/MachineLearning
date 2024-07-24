package domain_design_tabular_q_learning.persistance;

import java.util.List;

public  interface DataBaseI<T> {

    void create(T rec);
    T read(Integer id);
    void delete(Integer id);
    List<T> getAll();
    Integer largestId();
    void clear();
    int size();

}
