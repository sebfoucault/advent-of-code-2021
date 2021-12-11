package advent2021.misc;

import org.javatuples.Pair;

import java.util.*;
import java.util.function.Predicate;

public final class Table<T> {

    public static final Collection<Pair<Integer, Integer>> ALL_MOVES =
            Utils.crossProductBut(Arrays.asList(-1, 0, +1), Arrays.asList(-1, 0, +1), Pair.with(0,0));

    public static final Collection<Pair<Integer, Integer>> ALL_BUT_DIAGONAL_MOVES =
            Arrays.asList(Pair.with(0,  -1), Pair.with(0,  +1), Pair.with(-1, 0), Pair.with(+1,  0));

    private final List<T> values = new ArrayList<>();

    private int rowCount;
    private int colCount;

    public int getRowCount() {
        return rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    public void addRow(List<T> values) {

        this.values.addAll(values);
        rowCount++;
        colCount = values.size();
    }

    public List<Point> getPoints() {
        return getPoints(null);
    }

    List<Point> getPoints(Predicate<Point> p) {

        List<Point> result = new ArrayList<>();

        for (int y = 0 ; y < rowCount ; y++) {
            for (int x = 0; x < colCount; x++) {

                Point point = new Point(x, y);
                if (null == p || p.test(point)) {
                    result.add(point);
                }
            }
        }

        return result;
    }

    public void setValueAt(Point point, T value) {
        setValueAt(point.x(), point.y(), value);
    }

    public void setValueAt(int col, int row, T value) {
        this.values.set(row * colCount + col, value);
    }

    public T getValueAt(Point point) {
        return getValueAt(point.x(), point.y());
    }

    T getValueAt(int col, int row) {
        return this.values.get(row * colCount + col);
    }


    public List<Point> findNeighbors(Point p, Collection<Pair<Integer, Integer>> moves, Predicate<Point> predicate) {

        List<Point> result = new ArrayList<>();

        for (Pair<Integer, Integer> move: ALL_MOVES) {

            int x = p.x() + move.getValue0();
            int y = p.y() + move.getValue1();

            if (y < 0 || y >= rowCount || x < 0 || x >= colCount) {
                continue;
            }

            Point neighbor = new Point(x, y);
            if (null == predicate || predicate.test(neighbor)) {
                result.add(neighbor);
            }
        }
        return result;
    }
}
