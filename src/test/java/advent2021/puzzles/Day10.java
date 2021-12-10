package advent2021.puzzles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;

public class Day10 {

   static abstract class ParseResult {
   }
   
   static class SuccessParseResult extends ParseResult {
   }
   
   static abstract class ParseError extends ParseResult {      
   }

   static class EmptyStackError extends ParseError {
   }
   
   static class CorruptionError extends ParseError {

      final char readChar;
      
      CorruptionError(char readChar) {
         this.readChar = readChar;
      }
      
      long getScore() {
         switch (this.readChar) {
         case ')':
            return 3;
         case ']':
            return 57;
         case '}':
            return 1197;
         case '>':
            return 25137;
         default:
            throw new IllegalStateException();
         }
      }
   }

   static class IncompleteError extends ParseError {

      final List<Character> missingChars;
      
      public IncompleteError(List<Character> missingChars) {
         this.missingChars = missingChars;
      }
      
      long getScore() {

         Function<Character, Integer> charScore = c -> {
            switch (c) {
            case ')':
               return 1;
            case ']':
               return 2;
            case '}':
               return 3;
            case '>':
               return 4;
            default:
               throw new IllegalStateException();
            }
         };
         long total = 0;
         for (char c : this.missingChars) {
            total = total * 5 + charScore.apply(c);
         }
         return total;
      }
   }
   
   @Test
   void part1() throws IOException {

      List<String> values = Utils.readValuesFromResources("/day10.txt");
      
      List<ParseResult> parseErrors = parseValues(values);
      
      long total = parseErrors.stream()
         .filter(r -> r instanceof CorruptionError)
         .map(r -> (CorruptionError) r)
         .map(CorruptionError::getScore)
         .reduce(Long::sum)
         .orElseThrow();
      
      System.out.println(total);
   }

   @Test
   void part2() throws IOException {
      
      List<String> values = Utils.readValuesFromResources("/day10.txt");
      
      List<ParseResult> parseErrors = parseValues(values);
      
      List<Long> incompleteErrors = parseErrors.stream()
         .filter(r -> r instanceof IncompleteError)
         .map(r -> (IncompleteError) r)
         .map(IncompleteError::getScore)
         .sorted()
         .collect(Collectors.toList());
      
      System.out.println(incompleteErrors.get(incompleteErrors.size() / 2));
   }
   
   private List<ParseResult> parseValues(List<String> values) {
      
      return values.stream()
            .map(Day10::parseValue)
            .collect(Collectors.toList());
   }

   private static ParseResult parseValue(String value) {
      
      Stack<Character> chunkStack = new Stack<>();
      
      for (int i = 0 ; i < value.length() ; i++) {
         
         char c = value.charAt(i);
         
         if (isOpeningChar(c)) {
            chunkStack.push(c);
            
         } else if (isClosingChar(c)) {
            
            if (chunkStack.isEmpty()) {
               return new EmptyStackError();
            }

            char e = getExpectedClosingChar(chunkStack.pop());
            if (e != c) {
               return new CorruptionError(c);
            }
         }
      }
      
      if (!chunkStack.isEmpty()) {
         List<Character> missingChars = new ArrayList<>();
         while (!chunkStack.isEmpty()) {
            missingChars.add(getExpectedClosingChar(chunkStack.pop()));
         }
         return new IncompleteError(missingChars);
      }
      
      return new SuccessParseResult();
   }

   private static boolean isOpeningChar(char c) {      
      return c == '(' || c == '[' || c == '{' || c == '<';
   }
   
   private static boolean isClosingChar(char c) {
      return c == ')' || c == ']' || c == '}' || c == '>';
   }
   
   private static char getExpectedClosingChar(char c) {

      switch (c) {
      case '(':
         return ')';
      case '[':
         return ']';
      case '{':
         return '}';
      case '<':
         return '>';
      default:
         throw new IllegalStateException();
      }
   }
}
