package advent2021.misc;

public record Vector (Point from, Point to) {

    public boolean isVertical() {
        return from.x() == to.x();
    }

    public boolean isHorizontal() {
        return from.y() == to.y();
    }
}
