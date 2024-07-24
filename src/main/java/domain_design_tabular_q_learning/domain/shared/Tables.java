package domain_design_tabular_q_learning.domain.shared;

public record Tables(
        String[][] values,
        String[][] stateActionValues,
        String[][] actions
) {
    public Tables(GridSizeExtractor e) {
        this(new String[e.nX()][e.nY()], new String[e.nX()][e.nY()], new String[e.nX()][e.nY()]);
    }
}