package advent2021.puzzles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import advent2021.misc.Point;
import advent2021.misc.Table;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;

public class Day09Test {


   @Test
   void part1() throws IOException {

      Table<Integer> table = loadTable("/day09.txt");

      int total = findLowPoints(table).stream()
            .map(p -> table.getValueAt(p))
            .map(n -> n + 1)
            .reduce(Integer::sum)
            .orElseThrow();

      assertThat(total, is(448));
   }

   @Test
   void part2() throws IOException {

      Table<Integer> table = loadTable("/day09.txt");

      int product = findLowPoints(table).stream()
         .map(p -> findBasin(table, p, Collections.emptySet()))
         .map(Set::size)
         .sorted((b1, b2) -> Integer.compare(b2, b1))
         .limit(3)
         .reduce(1, (a,b) -> a*b);

      assertThat(product, is(1417248));
   }

   private List<Point> findLowPoints(Table<Integer> table) {

      Predicate<Point> hasNoSmallerNeighbor = point -> {
         int pointValue = table.getValueAt(point);
         return table.findNeighbors(point, Table.ALL_BUT_DIAGONAL_MOVES, neighbor -> table.getValueAt(neighbor) <= pointValue).isEmpty();
      };

      return table.getPoints().stream()
            .filter(hasNoSmallerNeighbor)
            .collect(Collectors.toList());
   }

   private Set<Point> findBasin(Table<Integer> table, Point point, Set<Point> excludedPoints) {

      Predicate<Point> isNeighborInBasin = neighbor -> {
         int neighborValue = table.getValueAt(neighbor);
         int pointValue = table.getValueAt(point);
         return neighborValue != 9 && !excludedPoints.contains(neighbor) && neighborValue > pointValue;
      };

      Set<Point> result = new HashSet<>();
      for (Point neighbor : table.findNeighbors(point, Table.ALL_BUT_DIAGONAL_MOVES, isNeighborInBasin)) {
         result.addAll(findBasin(table, neighbor, Utils.Sets.union(excludedPoints, Collections.singleton(point))));
      }

      result.add(point);

      return result;
   }

   private Table<Integer> loadTable(String resourceName) throws IOException {

      List<List<Integer>> rows = Utils.readValuesFromResources(resourceName, Utils::digitsToInt);

      Table<Integer> table = new Table<>();
      for (List<Integer> row: rows) {
         table.addRow(row);
      }
      return table;
   }
}
