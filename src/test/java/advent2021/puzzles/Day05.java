package advent2021.puzzles;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;

public class Day05 {

   static class Point {

      final int x;
      final int y;

      Point(int x, int y) {
         this.x = x;
         this.y = y;
      }

      @Override
      public String toString() {
         return "Point [x=" + x + ", y=" + y + "]";
      }

      @Override
      public int hashCode() {
         return Objects.hash(x, y);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         Point other = (Point) obj;
         return x == other.x && y == other.y;
      }
   }

   static class Vector {

      final Point from;
      final Point to;

      Vector(Point from, Point to) {
         this.from = from;
         this.to = to;
      }

      boolean isVertical() {
         return from.x == to.x;
      }

      boolean isHorizontal() {
         return from.y == to.y;
      }

      @Override
      public String toString() {
         return "Vector [from=" + from + ", to=" + to + "]";
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

      var vectors = Utils.readValuesFromResources("/day05.txt", Day05::parseVector);

      long count = getNumberOfPointsWithTwoOrMoreIntersections(vectors, false);

      System.out.println(count);
   }

   @Test
   void part2() throws IOException {

      var vectors = Utils.readValuesFromResources("/day05.txt", Day05::parseVector);

      long count = getNumberOfPointsWithTwoOrMoreIntersections(vectors, true);

      System.out.println(count);
   }

   private long getNumberOfPointsWithTwoOrMoreIntersections(List<Vector> vectors, boolean followDiagonals) {

      Grid g = new Grid();
      Map<Point, Integer> intersections = g.computeIntersection(vectors, followDiagonals);

      long count = intersections.entrySet().stream().filter(e -> e.getValue() >= 2).count();
      return count;
   }
}
