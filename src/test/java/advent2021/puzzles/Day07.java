package advent2021.puzzles;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;

import static advent2021.misc.Utils.readSingleValueFromResources;

public class Day07 {

   
   @Test
   void part1() throws IOException {
      
      List<Integer> positions = readSingleValueFromResources("/day07.txt",
            s -> Utils.split(s, ",", Integer::parseInt));
      
      Result r = align(positions, (from, to) -> Math.abs(to - from));
        
      System.out.println(r.position + " => " + r.cost);
   }

   @Test
   void part2() throws IOException {
      
      List<Integer> positions = readSingleValueFromResources("/day07.txt",
            s -> Utils.split(s, ",", Integer::parseInt));
      
      Result r = align(positions, (from, to) -> {
         var distance = Math.abs(to - from);
         return distance * (distance + 1) / 2;
      });
        
      System.out.println(r.position + " => " + r.cost);
   }
   
   static class Result {
      int position;
      int cost;
   }
   
   interface CostFunction {
      int computeCostFromTo(int from, int to);
   }
   
   private Result align(List<Integer> values, CostFunction costFunction) {

      Map<Integer, Integer> valuesOccurrences = new HashMap<>();
      
      int minValue = Integer.MAX_VALUE;
      int maxValue = 0;
      for (int value: values) {
         valuesOccurrences.merge(value, 1, Integer::sum);
         minValue = Math.min(minValue, value);
         maxValue = Math.max(maxValue, value);
      }
      
      Result result = null;
      
      for (int position = minValue; position <= maxValue; position++) {
         
         int costToPosition = 0;
         
         for (Entry<Integer, Integer> e: valuesOccurrences.entrySet()) {
            costToPosition += costFunction.computeCostFromTo(e.getKey(), position) * e.getValue();
         }
         
         if (result == null || costToPosition < result.cost) {
            result = new Result();
            result.cost = costToPosition;
            result.position = position;
         }
      }
      
      return result;
   }
}
