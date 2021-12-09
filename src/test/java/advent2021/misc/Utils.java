package advent2021.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;

public class Utils {

   public static List<Integer> digitsToInt(String s) {

      List<Integer> result = new ArrayList<>();
      
      for (int i = 0; i < s.length(); i++) {
         
         char c = s.charAt(i);
         result.add(Integer.parseInt("" + c));
      }
      
      return result;
   }
   
   public static String readSingleValueFromResources(String s) throws IOException {
      
      return readValuesFromResources(s).get(0);
   }

   public static <T> T readSingleValueFromResources(String s, Function<String, T> converter) throws IOException {
      
      String value = readValuesFromResources(s).get(0);
      return converter.apply(value);
   }
   
   public static List<String> readValuesFromResources(String s) throws IOException {

      return readValuesFromResources(s, line -> line);
   }

   public static <T> List<T> readValuesFromResources(String s, Function<String, T> converter) throws IOException {

      List<T> result = new ArrayList<>();

      InputStream is = Utils.class.getResourceAsStream(s);

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

         String line = null;
         while ((line = reader.readLine()) != null) {
            result.add(converter.apply(line));
         }
         return result;
      }
   }

   public static List<String> split(String s, String separators) {

      return split(s, separators, p -> p);
   }

   public static <T> List<T> split(String s, String separators, Function<String, T> converter) {

      StringTokenizer tokenizer = new StringTokenizer(s, separators);
      List<T> result = new ArrayList<>();
      while (tokenizer.hasMoreTokens()) {
         result.add(converter.apply(tokenizer.nextToken()));
      }
      return result;
   }
   
   
   public static class Sets {
      
      public static <T> Set<T> union(Set<T> s1, Set<T> s2) {
         
         Set<T> result = new HashSet<>();
         result.addAll(s1);
         result.addAll(s2);
         return result;
      }
   }
   
}
