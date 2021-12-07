package advent2021.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Function;

public class Utils {

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
}
