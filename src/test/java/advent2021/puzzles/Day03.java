package advent2021.puzzles;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import advent2021.misc.Utils;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Predicate;

public class Day03 {

   static class Aggregator {
      
      private final int width;
      private final int[] countOfBitSetTo1;
      private final int[] countOfBitSetTo0;
      
      Aggregator() {
         
         this.width = 0;
         this.countOfBitSetTo1 = null;
         this.countOfBitSetTo0 = null;
      }
      
      public Aggregator(int width, int[] countOfBitSetTo1, int[] countOfBitSetTo0) {
         
         this.width = width;
         this.countOfBitSetTo1 = countOfBitSetTo1;
         this.countOfBitSetTo0 = countOfBitSetTo0;
      }

      Aggregator aggregate(String record) {
         
         int[] x = this.countOfBitSetTo1 == null ? new int[record.length()] : this.countOfBitSetTo1.clone();
         int[] y = this.countOfBitSetTo0 == null ? new int[record.length()] : this.countOfBitSetTo0.clone();
         
         for (int i =0 ; i < record.length() ;i++) {
            if (record.charAt(i) == '1') {
               x[i]++;
            } else {
               y[i]++;
            }
         }
         return new Aggregator(record.length(), x, y);
      }
   }

   @Test
   void part1() throws IOException {
      
      var records = Utils.readValuesFromResources("/day03.txt", s -> s);
      
      var powerConsumption = Observable
            .fromIterable(records)
            .reduce(new Aggregator(), Aggregator::aggregate)
            .map(a-> gammaRate(a) * epsilonRate(a))
            .blockingGet();
      
      System.out.println(powerConsumption);
   }
   
   @Test
   void part2() throws Exception {
      
      var records = Utils.readValuesFromResources("/day03.txt", s -> s);

      var oxygenRecord = findRecord(records, this::keepForOxygen);
      var co2Record = findRecord(records, this::keepForCO2);
      
      System.out.println(binaryStringToInt(oxygenRecord) * binaryStringToInt(co2Record));
   }

   private String findRecord(List<String> records, BiFunction<Aggregator, Integer, Predicate<String>> predicateProvider) throws Exception {
      
      var filteredRecords = Collections.unmodifiableList(records);
      
      int bitIndex = 0;
      while (filteredRecords.size() > 1) {
         
         var resultAggr = Observable
            .fromIterable(filteredRecords)
            .reduce(new Aggregator(), Aggregator::aggregate)
            .blockingGet();
         
         Predicate<String> keepForOxygen = predicateProvider.apply(resultAggr, bitIndex++);
         
         filteredRecords = Observable
            .fromIterable(filteredRecords)
            .filter(keepForOxygen)
            .toList()
            .blockingGet();	
      }
      return filteredRecords.get(0);
   }
   
   private Predicate<String> keepForOxygen(Aggregator aggr, int bitIndex) {
      
      if (aggr.countOfBitSetTo0[bitIndex] == aggr.countOfBitSetTo1[bitIndex]) {
         return r -> hasBitMatching(bitIndex, 1, r);
      } else {
         return r -> hasBitMatching(bitIndex, mostCommonBit(aggr, bitIndex), r);
      }
   }

   private Predicate<String> keepForCO2(Aggregator aggr, int bitIndex) {
      
      if (aggr.countOfBitSetTo0[bitIndex] == aggr.countOfBitSetTo1[bitIndex]) {
         return r -> hasBitMatching(bitIndex, 0, r);
      } else {
         return r -> hasBitMatching(bitIndex, leastCommonBit(aggr, bitIndex), r);
      }
   }
   
   private boolean hasBitMatching(int bitIndex, int bitValue, String record) {
      return record.charAt(bitIndex) == (bitValue == 1 ? '1' : '0');
   }

   private int gammaRate(Aggregator aggr) {
      
      int value = 0;
      for (int i = 0 ; i < aggr.width ; i++) {
         value += mostCommonBit(aggr, i) << (aggr.width - 1 - i);
      }
      return value;
   }
   
   private int epsilonRate(Aggregator aggr) {
      
      int value = 0;
      for (int i = 0 ; i < aggr.width ; i++) {
         value += leastCommonBit(aggr, i) << (aggr.width - 1 - i);
      }
      return value;		
   }
   
   private int mostCommonBit(Aggregator aggr, int bitIdx) {
      return aggr.countOfBitSetTo1[bitIdx] > aggr.countOfBitSetTo0[bitIdx] ? 1 : 0;
   }

   private int leastCommonBit(Aggregator aggr, int bitIdx) {
      return mostCommonBit(aggr, bitIdx) == 0 ? 1 : 0;
   }
   
   private int binaryStringToInt(String s) {
      
      int value = 0;
      for (int i = 0 ; i < s.length() ; i++) {
         value += (hasBitMatching(i, 1, s) ? 1 : 0) << (s.length() - 1 - i);
      }
      return value;		
   }
}
