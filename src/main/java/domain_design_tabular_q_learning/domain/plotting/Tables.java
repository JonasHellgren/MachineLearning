package domain_design_tabular_q_learning.domain.plotting;

public record Tables(
        String[][] values,
        String[][] stateActionValues,
        String[][] actions
) {
    public Tables(GridSizeInformer e) {
        this(new String[e.nX()][e.nY()], new String[e.nX()][e.nY()], new String[e.nX()][e.nY()]);
    }
}