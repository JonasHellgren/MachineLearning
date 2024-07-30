package safe_rl.persistance.trade_environment;

public record PathAndFile(
        String path,
        String name,
        String fileType
) {

    public static PathAndFile xlsOf(String path, String name) {
        return new PathAndFile(path,name,"xlsx");
    }

    public String fullName() {
        return path+name+fileType;
    }

}
