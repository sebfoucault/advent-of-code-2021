package advent2021.puzzles;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;

public class Day06 {

   public class SquidState {
      
      private Map<Integer, Long> countPerAge = new HashMap<>();

      void addSquid(int squidAge) {
         countPerAge.merge(squidAge, 1L, (v1, v2) -> v1 + v2);
      }
            
      void addSquids(int squidAge, long count) {
         countPerAge.merge(squidAge, count, (v1, v2) -> v1 + v2);
      }
            
      Set<Entry<Integer, Long>> getSquidAgeInfo() {
         return countPerAge.entrySet();
      }
      
      long getSquidCount() {
         return countPerAge.values().stream().reduce(Long::sum).orElseGet(() -> 0L);
      }
   }
   
   private SquidState nextState(SquidState state, int ageAfterBirthForParent, int ageAfterBirthForChild) {
      
      SquidState nextState = new SquidState();
      
      for (Entry<Integer, Long> entry: state.getSquidAgeInfo()) {
         
         int age = entry.getKey();
         long count = entry.getValue();
         
         if (age == 0) {
            nextState.addSquids(ageAfterBirthForChild, count);
            nextState.addSquids(ageAfterBirthForParent, count);
         } else {
            nextState.addSquids(age - 1, count);
         }
      }
      return nextState;
   }
   
   private SquidState readState(String s) {
      
      var state = new SquidState();
      
      StringTokenizer tokenizer = new StringTokenizer(s, ", ");
      while (tokenizer.hasMoreTokens()) {
         state.addSquid((byte)Integer.parseInt(tokenizer.nextToken()));
      }
      return state;
   }

   private SquidState countSquidsAfterIteration(int iterationCount) throws IOException {
      
      var state = readState(Utils.readValuesFromResources("/day06.txt").get(0));
      for (int i = 0; i < iterationCount; i++) {
         state = nextState(state, 6, 8);
      }
      return state;
   }
   
   @Test
   void part1() throws IOException {
      
      var state = countSquidsAfterIteration(80);
      System.out.println(state.getSquidCount());
   }

   @Test
   void part2() throws IOException {
      
      var state = countSquidsAfterIteration(80);
      System.out.println(state.getSquidCount());
   }
}
