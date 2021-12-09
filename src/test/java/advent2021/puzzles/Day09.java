package advent2021.puzzles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;

public class Day09 {

   static class Table {
      
      final List<Integer> values = new ArrayList<>();
      
      int rowCount;
      int colCount;
      
      void addRow(List<Integer> values) {
         
         this.values.addAll(values);
         rowCount++;
         colCount = values.size();
      }
      
      List<Point> getPoints() {
         
         List<Point> result = new ArrayList<>();
         
         for (int row = 0 ; row < rowCount ; row++) {
            for (int col = 0; col < colCount; col++) {
               
               int value = this.values.get(row * colCount + col);
               result.add(new Point(value, row, col));
            }
         }
         
         return result;
      }
      
      List<Point> findNeighbors(Point p, Predicate<Point> predicate) {
         
         List<Point> result = new ArrayList<>();
         
         for (Pair<Integer, Integer> move: Arrays.asList(Pair.with(0,  -1), Pair.with(0,  +1), Pair.with(-1, 0), Pair.with(+1,  0))) {
            
            int newRow = p.row + move.getValue0();
            int newCol = p.col + move.getValue1();
            
            if (newRow < 0 || newRow >= rowCount || newCol < 0 || newCol >= colCount) {
               continue;
            }
            
            int newValue = this.values.get(newRow * colCount + newCol);
            
            Point neighbor = new Point(newValue, newRow, newCol);

            if (null == predicate || predicate.test(neighbor)) {
               result.add(neighbor);
            }
         }
         
         return result;
      }
   }
   
   static class Point {
      
      final int value;
      final int row; 
      final int col;
      
      public Point(int value, int row, int col) {
         this.value = value;
         this.row = row;
         this.col = col;
      }

      @Override
      public int hashCode() {
         return Objects.hash(col, row, value);
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
         return col == other.col && row == other.row && value == other.value;
      }
   }
   
   @Test
   void part1() throws IOException {

      Table table = loadTable("/day09.txt");
      
      int total = findLowPoints(table).stream()
            .map(p -> p.value)
            .map(n -> n + 1)
            .reduce(Integer::sum)
            .orElseThrow();
      
      System.out.println(total);
   }

   @Test
   void part2() throws IOException {

      Table table = loadTable("/day09.txt");
      
      int product = findLowPoints(table).stream()
         .map(p -> findBasin(table, p, Collections.emptySet()))
         .map(b -> b.size())
         .sorted((b1, b2) -> Integer.compare(b2, b1))
         .limit(3)
         .reduce(1, (a,b) -> a*b);
      
      System.out.println(product);
   }
      
   private List<Point> findLowPoints(Table table) {
      
      Predicate<Point> hasNoSmallerNeighbor = point -> {
            return table.findNeighbors(point, neighbor -> neighbor.value <= point.value).isEmpty();
      };
      
      return table.getPoints().stream()
            .filter(hasNoSmallerNeighbor)
            .collect(Collectors.toList());
   }
     
   private Set<Point> findBasin(Table table, Point point, Set<Point> excludedPoints) {

      Predicate<Point> isNeighborInBasin = neighbor -> {
            return neighbor.value != 9 && !excludedPoints.contains(neighbor) && neighbor.value > point.value;
      };
      
      Set<Point> result = new HashSet<>();
      for (Point neighbor : table.findNeighbors(point, isNeighborInBasin)) {
         result.addAll(findBasin(table, neighbor, Utils.Sets.union(excludedPoints, Collections.singleton(point))));
      }
      
      result.add(point);

      return result;
   }
   
   private Table loadTable(String resourceName) throws IOException {
      
      List<List<Integer>> rows = Utils.readValuesFromResources(resourceName, Utils::digitsToInt);
      
      Table table = new Table();
      for (List<Integer> row: rows) {
         table.addRow(row);
      }
      return table;
   }
}
