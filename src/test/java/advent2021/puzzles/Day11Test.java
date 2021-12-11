package advent2021.puzzles;

import advent2021.misc.Table;
import advent2021.misc.Utils;
import advent2021.misc.Point;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Day11Test {

   static Table<Integer> load(String resourceName) throws IOException {

      List<List<Integer>> rows = Utils.readValuesFromResources(resourceName, Utils::digitsToInt);

      Table<Integer> table = new Table<>();
      for (List<Integer> row: rows) {
         table.addRow(row);
      }
      return table;
   }

   @Test
   void part1() throws IOException {

      Table<Integer> table = load("/day11.txt");

      int result = IntStream.range(0, 100)
              .map(i -> playRound(table))
              .reduce(Integer::sum)
              .orElseThrow();

      assertThat(result, is(1588));
   }

   @Test
   void part2() throws IOException {

      Table<Integer> table = load("/day11.txt");

      int stepCount = 1;
      while (playRound(table) != table.getColCount() * table.getRowCount()) {
         stepCount++;
      }

      assertThat(stepCount, is(517));
   }

   private int playRound(Table<Integer> table) {

      List<Point> flashedPoints = incrementAndFlash(table, table.getPoints());

      Set<Point> allFlashedPoints = new HashSet<>(flashedPoints);

      while (flashedPoints.size() > 0) {

         List<Point> newlyFlashedPoints = new ArrayList<>();
         for (Point flashedPoint: flashedPoints) {

            List<Point> neighbors = table.findNeighbors(flashedPoint, true, null);
            newlyFlashedPoints.addAll(incrementAndFlash(table, neighbors));
         }
         allFlashedPoints.addAll(newlyFlashedPoints);
         flashedPoints = newlyFlashedPoints;
      }

      allFlashedPoints.forEach(p -> table.setValueAt(p, 0));

      return allFlashedPoints.size();
   }

   private List<Point> incrementAndFlash(Table<Integer> table, Collection<Point> points) {

      List<Point> flashedPoints = new ArrayList<>();

      for (Point point: points) {
         int value = table.getValueAt(point);
         if (value > 9) {
            continue;
         }
         int newValue = value + 1;
         if (newValue > 9) {
            flashedPoints.add(point);
         }
         table.setValueAt(point, newValue);
      }
      return flashedPoints;
   }
}
