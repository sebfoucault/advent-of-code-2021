package advent2021.puzzles;

import static advent2021.misc.Utils.readValuesFromResources;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class Day05Test {

   record Point (int x, int y) {}

   record Vector (Point from, Point to) {

      boolean isVertical() {
         return from.x == to.x;
      }

      boolean isHorizontal() {
         return from.y == to.y;
      }
   }

   static Vector parseVector(String s) {

      Pattern pattern = Pattern.compile("([0-9]+),([0-9]+) -> ([0-9]+),([0-9]+)");
      Matcher matcher = pattern.matcher(s);
      matcher.matches();

      int x0 = Integer.parseInt(matcher.group(1));
      int y0 = Integer.parseInt(matcher.group(2));
      int x1 = Integer.parseInt(matcher.group(3));
      int y1 = Integer.parseInt(matcher.group(4));

      return new Vector(new Point(x0, y0), new Point(x1, y1));
   }

   static class Grid {

      Map<Point, Integer> computeIntersection(List<Vector> vectors, boolean followDiagonals) {

         Map<Point, Integer> values = new HashMap<>();

         for (Vector v : vectors) {

            if (v.isVertical()) {
               for (int y = min(v.from.y, v.to.y); y <= max(v.from.y, v.to.y); y++) {
                  incrementValueAt(values, v.from.x, y);
               }
            } else if (v.isHorizontal()) {
               for (int x = min(v.from.x, v.to.x); x <= max(v.from.x, v.to.x); x++) {
                  incrementValueAt(values, x, v.from.y);
               }
            } else if (followDiagonals) {

               int incX = getMoveInX(v);
               int incY = getMoveInY(v);

               for (int x = v.from.x, y = v.from.y;; x += incX, y += incY) {
                  incrementValueAt(values, x, y);
                  if (x == v.to.x && y == v.to.y) {
                     break;
                  }
               }
            }
         }

         return values;
      }

      int getMoveInX(Vector v) {

         int delta = v.to.x - v.from.x;
         return delta / Math.abs(delta);
      }

      int getMoveInY(Vector v) {

         int delta = v.to.y - v.from.y;
         return delta / Math.abs(delta);
      }

      static void incrementValueAt(Map<Point, Integer> values, int x, int y) {
         values.merge(new Point(x, y), 1, Integer::sum);
      }
   }

   @Test
   void part1() throws IOException {

      var vectors = readValuesFromResources("/day05.txt", Day05Test::parseVector);

      long count = getNumberOfPointsWithTwoOrMoreIntersections(vectors, false);

      assertThat(count, is(7318L));
   }

   @Test
   void part2() throws IOException {

      var vectors = readValuesFromResources("/day05.txt", Day05Test::parseVector);

      long count = getNumberOfPointsWithTwoOrMoreIntersections(vectors, true);

      assertThat(count, is(19939L));
   }

   private long getNumberOfPointsWithTwoOrMoreIntersections(List<Vector> vectors, boolean followDiagonals) {

      Grid g = new Grid();
      Map<Point, Integer> intersections = g.computeIntersection(vectors, followDiagonals);

      return intersections.entrySet().stream().filter(e -> e.getValue() >= 2).count();
   }
}
