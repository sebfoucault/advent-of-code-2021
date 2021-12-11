package advent2021.puzzles;

import static advent2021.misc.Utils.readValuesFromResources;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.reactivex.Observable;

public class Day02Test {

   enum Direction {

      Forward, Down, Up;

      public static Direction fromString(String s) {

         return switch (s) {
            case "forward" -> Forward;
            case "down" -> Down;
            case "up" -> Up;
            default -> throw new IllegalArgumentException();
         };
      }
   }

   record Command (Direction direction, int magnitude){

      public static Command parse(String s) {
         
         String[] parts = s.split(" ");
         return new Command(Direction.fromString(parts[0]), Integer.parseInt(parts[1]));
      }
   }

   record Position(int x, int depth, int aim) {

      Position applySimple(Command command) {

         return switch (command.direction) {
            case Down -> new Position(x, depth + command.magnitude, aim);
            case Up -> new Position(x, depth - command.magnitude, aim);
            case Forward -> new Position(x + command.magnitude, depth, aim);
         };
      }

      Position applyWithAim(Command command) {

         return switch (command.direction) {
            case Down -> new Position(x, depth, aim + command.magnitude);
            case Up -> new Position(x, depth, aim - command.magnitude);
            case Forward -> new Position(x + command.magnitude, depth + (aim * command.magnitude), aim);
         };
      }
   }

   @Test
   void part1() throws IOException {

      var commands = readValuesFromResources("/day02.txt", Command::parse);
      
      var finalPos = Observable
         .fromIterable(commands)
         .reduce(new Position(0, 0, 0), Position::applySimple)
         .blockingGet();

      assertThat(finalPos.x * finalPos.depth, is(1694130));
   }

   @Test
   void part2() throws IOException {

      var commands = readValuesFromResources("/day02.txt", Command::parse);
      
      var finalPos = Observable
         .fromIterable(commands)
         .reduce(new Position(0, 0, 0), Position::applyWithAim)
         .blockingGet();
      
      assertThat(finalPos.x * finalPos.depth, is(1698850445));
   }
}
