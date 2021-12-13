package advent2021.puzzles;

import advent2021.misc.Point;
import advent2021.misc.Size;
import advent2021.misc.Utils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Day13Test {

   enum Axis {
      X,Y
   }

   record Folding(Axis axis, int value) {}

   record Problem(List<Point> points, List<Folding> foldings) {}

   static class Table {

      private final Size size;
      private final ArrayList<Boolean> values;

      Table(Size size) {
         this(size.width(), size.height());
      }

      Table(int width, int height) {
         this.size = new Size(width, height);
         this.values = new ArrayList<>(Collections.nCopies(width * height, false));
      }

      public int getHeight() {
         return size.height();
      }

      public int getWidth() {
         return size.width();
      }

      public void setValueAt(int x, int y, Boolean b) {
         this.values.set(y * getWidth() + x, b);
      }

      public void setValueAtOr(int x, int y, boolean newValue) {
         boolean oldValue = getValueAt(x, y);
         boolean actualValue = oldValue ? oldValue : newValue;
         setValueAt(x, y, actualValue);
      }

      public Boolean getValueAt(int x, int y) {
         return this.values.get(y * getWidth() + x);
      }
   }

   private static Table fold(Table t, Folding folding) {

      if (folding.axis == Axis.X) {
         return foldX(t, folding.value());
      } else {
         return foldY(t, folding.value());
      }
   }

   private static Table foldX(Table t, int foldPoint) {

      Table result = new Table(foldPoint, t.getHeight());

      for (int x = 0 ; x < t.getWidth() ; x++) {
         if (x==foldPoint) continue;
         for (int y = 0; y < t.getHeight(); y++) {
            result.setValueAtOr(norm(x, foldPoint), y, t.getValueAt(x,y));
         }
      }
      return result;
   }

   private static Table foldY(Table t, int foldPoint) {

      Table result = new Table(t.getWidth(), foldPoint);

      for (int x = 0 ; x < t.getWidth() ; x++) {
         for (int y = 0; y < t.getHeight(); y++) {
            if (y == foldPoint) continue;
            result.setValueAtOr(x, norm(y, foldPoint), t.getValueAt(x,y));
         }
      }
      return result;
   }

   private static int norm(int value, int cap) {
      if (value <= cap) {
         return value;
      } else {
         return cap - (value - cap);
      }
   }

   private String drawTable(Table t) {

      StringBuilder sb = new StringBuilder();

      for (int y = 0; y < t.size.height(); y++) {
         for (int x = 0 ; x < t.size.width() ; x++) {
            Boolean valueAt = t.getValueAt(x, y);
            sb.append(valueAt ? "#" : ".");
         }
         sb.append("\n");
      }
      return sb.toString();
   }

   @Test
   void part1() throws IOException {

      Problem pb = parseProblem("/day13.txt");

      Table table = fillTable(pb.points);
      Folding folding = pb.foldings.get(0);

      Table foldResult = fold(table, folding);

      long count = foldResult.values.stream().filter(b -> b).count();
      assertThat(count, is(770L));
   }

   @Test
   void part2() throws IOException {

      Problem pb = parseProblem("/day13.txt");
      Table table = fillTable(pb.points);

      for (Folding folding: pb.foldings) {
         table = fold(table, folding);
      }

      String drawnTable = drawTable(table);

      assertThat(drawnTable,
              is(
                     """
                     ####.###..#..#.####.#....###..###..###..
                     #....#..#.#..#.#....#....#..#.#..#.#..#.
                     ###..#..#.#..#.###..#....#..#.###..#..#.
                     #....###..#..#.#....#....###..#..#.###..
                     #....#....#..#.#....#....#....#..#.#.#..
                     ####.#.....##..####.####.#....###..#..#.
                     """));
   }

   Problem parseProblem(String resourceName) throws IOException {

      List<String> values = Utils.readValuesFromResources(resourceName);

      List<Point> points = new ArrayList<>();
      List<Folding> foldings = new ArrayList<>();

      for (String value: values) {

         int indexOfX = value.indexOf("x=");
         int indexOfY = value.indexOf("y=");

         if (value.isBlank()) {
            continue;
         } else if (indexOfX != -1) {
            foldings.add(new Folding(Axis.X, Integer.parseInt(value.substring(indexOfX + "x=".length()))));
         } else if (indexOfY != -1) {
            foldings.add(new Folding(Axis.Y, Integer.parseInt(value.substring(indexOfY + "y=".length()))));
         } else {
            List<String> splits = Utils.split(value, ", ");
            points.add(new Point(Integer.parseInt(splits.get(0)), Integer.parseInt(splits.get(1))));
         }
      }
      return new Problem(points, foldings);
   }

   private static Table fillTable(List<Point> points) {

      Size tableSize = points.stream().reduce(new Size(0, 0), (Size::fit), Size::union);

      Table t = new Table(tableSize);
      for (Point point: points) {
         t.setValueAt(point.x(), point.y(), true);
      }
      return t;
   }
}
