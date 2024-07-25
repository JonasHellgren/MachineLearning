package domain_design_tabular_q_learning.domain.plotting;

public record FileDirName(
        String dir, String fileName, String fileEnd
) {

    public static FileDirName of(String dir, String fileName, String fileEnd) {
        return new FileDirName(dir, fileName, fileEnd);
    }
}
