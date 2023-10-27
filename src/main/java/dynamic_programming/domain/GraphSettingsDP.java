package dynamic_programming.domain;

import lombok.Builder;

@Builder
public record GraphSettingsDP(int xMax, int yMax, double gamma) {

    public Integer getNofActions() {
        return yMax+1;
    }
}
