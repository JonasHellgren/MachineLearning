package common.math;

import lombok.Builder;

@Builder
public record Discrete2DPos(
        int x,
        int y
) {

    public boolean equals(Discrete2DPos other) {
        return x==other.x && y==other.y;
    }

    public Discrete2DPos move(Discrete2DVector vector) {
        return  Discrete2DPos.builder()
                .x(x+ vector.dx())
                .y(y+ vector.dy())
                .build();
    }

}
