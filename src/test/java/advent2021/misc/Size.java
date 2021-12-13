package advent2021.misc;

public record Size(int width, int height) {

    public Size fit(Point p) {
        int newWidth = Math.max(p.x() + 1, this.width());
        int newHeight = Math.max(p.y() + 1, this.height());
        return new Size(newWidth, newHeight);
    }

    public Size union(Size size) {
        return new Size(
                Math.max(this.width(), size.width()),
                Math.max(this.height(), size.height()));
    }

}
