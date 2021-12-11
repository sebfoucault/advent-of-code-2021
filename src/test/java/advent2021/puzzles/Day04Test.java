package advent2021.puzzles;

import static advent2021.misc.Utils.readValuesFromResources;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;

public class Day04Test {

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

         return Optional.of(winningBoardInfo.winningBoard).map(Board::getUnmarkedValues)
               .map(v -> v.stream().reduce(0, Integer::sum))
               .map(sum -> sum * winningBoardInfo.lastMarkedNumber).orElseThrow();
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

      boolean isNotMarked(int row, int col) {
         return !markedPositions.get(row * size + col);
      }

      boolean isRowFullyMarked(int row) {
         return IntStream.range(0, size).filter(i -> isNotMarked(row, i)).findAny().isEmpty();
      }

      boolean isColFullyMarked(int col) {
         return IntStream.range(0, size).filter(i -> isNotMarked(i, col)).findAny().isEmpty();
      }

      boolean hasColFullyMarked() {
         return IntStream.range(0, size).filter(this::isColFullyMarked).findAny().isPresent();
      }

      boolean hasRowFullyMarked() {
         return IntStream.range(0, size).filter(this::isRowFullyMarked).findAny().isPresent();
      }

      List<Integer> getUnmarkedValues() {

         return IntStream.range(0, values.size())
               .filter(i -> !markedPositions.get(i))
               .mapToObj(values::get)
               .collect(Collectors.toList());
      }

      void markValueIfPresent(int value) {

         IntStream.range(0, values.size())
            .filter(i -> values.get(i) == value)
            .forEach(markedPositions::set);
      }
   }

   record WinningBoardInfo (Board winningBoard, int lastMarkedNumber) {
   }

   @Test
   void part1() throws IOException {

      var lines = readValuesFromResources("/day04.txt");
      var game = Game.parse(5, lines);

      assertThat(game.playWithPart1Rules(), is(63552));
   }

   @Test
   void part2() throws IOException {

      var lines = readValuesFromResources("/day04.txt");
      var game = Game.parse(5, lines);

      assertThat(game.playWithPart2Rules(), is(9020));
   }
}
