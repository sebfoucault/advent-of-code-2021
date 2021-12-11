package advent2021.puzzles;

import static advent2021.misc.Utils.readValuesFromResources;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.reactivex.Observable;

public class Day01Test {

   @Test
   void part1() throws IOException {

      var values = readValuesFromResources("/day01.txt", Integer::parseInt);

      assertThat(countIncreasedValues(values), is(1482L));
   }

   @Test
   void part2() throws IOException {

      var values = readValuesFromResources("/day01.txt", Integer::parseInt);

      assertThat(countIncreasedValues(sumWithSlidingWindowOf3(values)), is(1518L));
   }

   private Long countIncreasedValues(List<Integer> l) {

      return Observable.fromIterable(l)
         .buffer(2, 1)
         .filter(b -> b.size() > 1)
         .map(b -> b.get(1) > b.get(0))
         .filter(b -> b)
         .count()
         .blockingGet();
   }

   private List<Integer> sumWithSlidingWindowOf3(List<Integer> l) {

      return Observable.fromIterable(l)
         .buffer(3, 1)
         .filter(b -> b.size() == 3)
         .map(b -> b.get(0) + b.get(1) + b.get(2))
         .toList()
         .blockingGet();
   }
}
