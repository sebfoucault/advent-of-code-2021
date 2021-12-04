package advent2021.puzzles;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;
import io.reactivex.Observable;

public class Day01 {

	@Test
	void part1() throws IOException {
		
		var values = Utils.readFromResources("/day01.txt", Integer::parseInt);
		
		System.out.println(countIncreasedValues(values));
	}

	@Test
	void part2() throws IOException {

		var values = Utils.readFromResources("/day01.txt", Integer::parseInt);
		
		System.out.println(countIncreasedValues(sumWithSlidingWindowOf3(values)));
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
