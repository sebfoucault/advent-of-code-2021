package advent2021.puzzles;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;
import io.reactivex.Observable;

public class Day02 {

   enum Direction {

      Forward, Down, Up;

      public static Direction fromString(String s) {
         switch (s) {
         case "forward":
            return Forward;
         case "down":
            return Down;
         case "up":
            return Up;
         default:
            throw new IllegalArgumentException();
         }
      }
   }

   static class Command {

      final Direction direction;
      final int magnitude;

      Command(Direction direction, int magnitude) {
         this.direction = direction;
         this.magnitude = magnitude;
      }

      public static Command parse(String s) {
         String[] parts = s.split(" ");
         return new Command(Direction.fromString(parts[0]), Integer.parseInt(parts[1]));
      }

      @Override
      public String toString() {
         return "Command [direction=" + direction + ", magnitude=" + magnitude + "]";
      }
   }

   static class Position {

      final int x;
      final int depth;
      final int aim;

      Position(int x, int depth, int aim) {
         this.x = x;
         this.depth = depth;
         this.aim = aim;
      }

      Position applySimple(Command command) {

         switch (command.direction) {
         case Down:
            return new Position(x, depth + command.magnitude, aim);
         case Up:
            return new Position(x, depth - command.magnitude, aim);
         case Forward:
            return new Position(x + command.magnitude, depth, aim);
         default:
            throw new IllegalStateException();
         }
      }

      Position applyWithAim(Command command) {

         switch (command.direction) {
         case Down:
            return new Position(x, depth, aim + command.magnitude);
         case Up:
            return new Position(x, depth, aim - command.magnitude);
         case Forward:
            return new Position(x + command.magnitude, depth + (aim * command.magnitude), aim);
         default:
            throw new IllegalStateException();
         }
      }
      
      @Override
      public String toString() {
         return "Position [x=" + x + ", depth=" + depth + "]";
      }
   }

   @Test
   void part1() throws IOException {

      var commands = Utils.readValuesFromResources("/day02.txt", Command::parse);
      
      var finalPos = Observable
         .fromIterable(commands)
         .reduce(new Position(0, 0, 0), Position::applySimple)
         .blockingGet();
      
      System.out.println(finalPos.x * finalPos.depth);
   }

   @Test
   void part2() throws IOException {

      var commands = Utils.readValuesFromResources("/day02.txt", Command::parse);
      
      var finalPos = Observable
         .fromIterable(commands)
         .reduce(new Position(0, 0, 0), Position::applyWithAim)
         .blockingGet();
      
      System.out.println(finalPos.x * finalPos.depth);
   }
}
