package advent2021.puzzles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;

public class Day04 {

   static class Game {

      List<Integer> numbers;
      List<Board> boards = new ArrayList<>();

      static Game parse(int size, List<String> lines) {

         Game game = new Game();
         game.numbers = Utils.split(lines.get(0), ",", Integer::parseInt);
         
         Board board = null;

         for (String line : lines.subList(1, lines.size())) {

            if (!line.isBlank()) {
      
               if (board == null) {
                  board = game.addBoard(size);
               }		
               board.addRow(Utils.split(line, " ", Integer::parseInt));
               
            } else {
               board = null;
            }
         }

         return game;
      }
      
      Board addBoard(int size) {
         
         Board b = new Board(size);
         boards.add(b);		
         return b;
      }
      
      int playWithPart1Rules() {

         List<WinningBoardInfo> winningBoardInfos = play();			
         return getResultForWinningBoard(winningBoardInfos.get(0));
      }

      int playWithPart2Rules() {

         List<WinningBoardInfo> winningBoardInfos = play();			
         return getResultForWinningBoard(winningBoardInfos.get(winningBoardInfos.size() - 1));
      }

      List<WinningBoardInfo> play() {
                  
         List<Board> candidateBoards = new ArrayList<>(this.boards);

         List<WinningBoardInfo> result = new ArrayList<>();
         for (int i = 0; i < numbers.size() && candidateBoards.size() > 0; i++) {

            int number = numbers.get(i);

            candidateBoards.stream().forEach(b -> b.markValueIfPresent(number));

            List<Board> winningBoards = findWinningBoards(candidateBoards);
            for (Board winningBoard: winningBoards) {
               candidateBoards.remove(winningBoard);
               result.add(new WinningBoardInfo(winningBoard, number));
            }
         }
         
         return result;
      }
      
      private List<Board> findWinningBoards(List<Board> candidateBoards) {

         return candidateBoards.stream()
               .filter(b -> b.hasColFullyMarked() || b.hasRowFullyMarked())
               .collect(Collectors.toList());
      }

      private int getResultForWinningBoard(WinningBoardInfo winningBoardInfo) {

         return Optional.of(winningBoardInfo.winningBoard).map(b -> b.getUnmarkedValues())
               .map(v -> v.stream().reduce(0, Integer::sum))
               .map(sum -> sum * winningBoardInfo.lastMarkedNumber).get();
      }
   }

   static class Board {

      private final ArrayList<Integer> values = new ArrayList<>();
      private final BitSet markedPositions = new BitSet();

      private final int size;

      public Board(int size) {
         this.size = size;
      }

      void addRow(List<Integer> values) {
         this.values.addAll(values);
      }

      boolean isMarked(int row, int col) {
         return markedPositions.get(row * size + col);
      }

      boolean isRowFullyMarked(int row) {
         return !IntStream.range(0, size).filter(i -> !isMarked(row, i)).findAny().isPresent();
      }

      boolean isColFullyMarked(int col) {
         return !IntStream.range(0, size).filter(i -> !isMarked(i, col)).findAny().isPresent();
      }

      boolean hasColFullyMarked() {
         return IntStream.range(0, size).filter(i -> isColFullyMarked(i)).findAny().isPresent();
      }

      boolean hasRowFullyMarked() {			
         return IntStream.range(0, size).filter(i -> isRowFullyMarked(i)).findAny().isPresent();
      }

      List<Integer> getUnmarkedValues() {
         
         return IntStream.range(0, values.size())
               .filter(i -> !markedPositions.get(i))
               .mapToObj(i -> values.get(i))
               .collect(Collectors.toList());
      }
            
      void markValueIfPresent(int value) {
         
         IntStream.range(0, values.size())
            .filter(i -> values.get(i) == value)
            .forEach(i -> markedPositions.set(i));
      }
   }
   
   static class WinningBoardInfo {
      
      private final Board winningBoard;
      private final int lastMarkedNumber;
      
      public WinningBoardInfo(Board winningBoard, int lastMarkedNumber) {
         this.winningBoard = winningBoard;
         this.lastMarkedNumber = lastMarkedNumber;
      }
   }

   @Test
   void part1() throws IOException {

      var lines = Utils.readValuesFromResources("/day04.txt");
      var game = Game.parse(5, lines);
      
      System.out.println(game.playWithPart1Rules());
   }

   @Test
   void part2() throws IOException {

      var lines = Utils.readValuesFromResources("/day04.txt");
      var game = Game.parse(5, lines);
      
      System.out.println(game.playWithPart2Rules());
   }
}
