package advent2021.puzzles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;

public class Day08Test {

   static record Sample (List<Set<Character>> patterns, List<Set<Character>> outputs) {

      static Sample parse(String s) {

         List<String> splits = Utils.split(s, "|");

         String patternsAsString = splits.get(0);
         String outputsAsString = splits.get(1);

         List<Set<Character>> patterns = Utils.split(patternsAsString, " ").stream().map(Sample::toCharacterSet)
               .collect(Collectors.toList());

         List<Set<Character>> outputs = Utils.split(outputsAsString, " ").stream().map(Sample::toCharacterSet)
               .collect(Collectors.toList());

         return new Sample(patterns, outputs);
      }

      private static Set<Character> toCharacterSet(String s) {

         Set<Character> result = new TreeSet<>();
         for (int i = 0; i < s.length(); i++) {
            result.add(s.charAt(i));
         }
         return result;
      }
   }

   @Test
   void part1() throws IOException {

      List<Sample> samples = Utils.readValuesFromResources("/day08.txt", Sample::parse);

      long unambigousOutputsCount = samples.stream()
              .map(sample -> sample.outputs)
              .flatMap(Collection::stream)
              .filter(output -> Arrays.asList(2,3,4,7).contains(output.size()))
              .count();

      assertThat(unambigousOutputsCount, is(449L));
   }

   @Test
   void part2() throws IOException {

      List<Sample> samples = Utils.readValuesFromResources("/day08.txt", Sample::parse);

      int sum = samples.stream()
            .map(s -> Solver.apply(s, Solver.solve(s)))
            .reduce(0, Integer::sum);

      assertThat(sum, is(968175));
   }

   static class Solver {

      static Map<Set<Character>, Integer> solve(Sample s) {

         Map<Set<Character>, Integer> result = new LinkedHashMap<>();

         List<Set<Character>> candidates = new ArrayList<>(s.patterns);

         Set<Character> one = findAndRemove(candidates, hasSize(2));
         Set<Character> four = findAndRemove(candidates, hasSize(4));
         Set<Character> seven = findAndRemove(candidates, hasSize(3));
         Set<Character> eight = findAndRemove(candidates, hasSize(7));
         Set<Character> six = findAndRemove(candidates, hasSize(6).and(hasIntersectionOfSize(one, 1)));
         Set<Character> nine = findAndRemove(candidates, hasSize(6).and(hasIntersectionOfSize(four, four.size())));
         Set<Character> zero = findAndRemove(candidates, hasSize(6));
         Set<Character> three = findAndRemove(candidates, hasIntersectionOfSize(one, one.size()));
         Set<Character> five = findAndRemove(candidates, hasIntersectionOfSize(nine, 5));
         Set<Character> two = findAndRemove(candidates, set -> true);

         result.put(zero, 0);
         result.put(one, 1);
         result.put(two, 2);
         result.put(three, 3);
         result.put(four, 4);
         result.put(five, 5);
         result.put(six, 6);
         result.put(seven, 7);
         result.put(eight, 8);
         result.put(nine, 9);

         return result;
      }

      private static Predicate<Set<Character>> hasIntersectionOfSize(Set<Character> refSet, int intersectionSize) {
         return set -> intersect(set, refSet).size() == intersectionSize;
      }

      private static <T> Set<T> intersect(Set<T> s1, Set<T> s2) {
         Set<T> s = new HashSet<>(s1);
         s.retainAll(s2);
         return s;
      }

      static Predicate<Set<Character>> hasSize(int size) {
         return set -> set.size() == size;
      }

      static Set<Character> findAndRemove(List<Set<Character>> candidates, Predicate<Set<Character>> predicate) {

         Set<Character> result = candidates.stream().filter(predicate).findFirst().orElseThrow();
         candidates.remove(result);
         return result;
      }

      static int apply(Sample sample, Map<Set<Character>, Integer> map) {

         return sample.outputs
                 .stream()
                 .map(map::get)
                 .reduce(0, (sum, v) -> sum * 10 + v, Integer::sum);
      }
   }
}
