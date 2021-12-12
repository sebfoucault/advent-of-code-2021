package advent2021.puzzles;

import advent2021.misc.Utils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Day12Test {

   static class Graph {

      private final Set<Edge> edges = new HashSet<>();

      public Graph addEdge(Vertex from, Vertex to) {

         this.edges.add(new Edge(from, to));
         return this;
      }

      public Graph merge(Graph g) {

         this.edges.addAll(g.edges);
         return this;
      }

      public Set<Vertex> getReachableVertices(Vertex vertex) {

         return this.edges.stream()
                 .filter( e -> e.from().equals(vertex) || e.to().equals(vertex))
                 .map(e -> e.from().equals(vertex) ? e.to() : e.from())
                 .filter(v -> !v.equals(vertex))
                 .collect(Collectors.toSet());
      }
   }

   record Vertex(String id, boolean small) {

      public Vertex(String id) {
         this(id, id.equals(id.toLowerCase()));
      }
   }

   record Edge(Vertex from, Vertex to) {}

   @Test
   void part1() throws IOException {

      Graph g = parse("/day12.txt");

      VertexVisitController controller = (gg, vertex, visitedVertices) -> {
         return !vertex.small() || visitedVertices.getOrDefault(vertex,0) == 0;
      };

      List<List<Edge>> allPaths = findAllPath(g, new Vertex("start"), new Vertex("end"), controller);
      assertThat(allPaths.size(), is(3292));
   }

   @Test
   void part2() throws IOException {

      Graph g = parse("/day12.txt");

      VertexVisitController controller = (graph, vertex, visitedVertices) -> {

         if (vertex.id().equals("start")) {
            return false;
         } else if (vertex.small()) {
            int count = visitedVertices.getOrDefault(vertex, 0);
            return hasSmallVertexVisitedTwice(visitedVertices) ? count == 0 : count < 2;
         } else {
            return true;
         }
      };

      List<List<Edge>> allPaths = findAllPath(g, new Vertex("start"), new Vertex("end"), controller);
      assertThat(allPaths.size(), is(89592));
   }

   private boolean hasSmallVertexVisitedTwice(Map<Vertex, Integer> visitedVertices) {
      return visitedVertices.entrySet().stream()
              .anyMatch(e -> e.getKey().small() && e.getValue() >= 2);
   }

   interface VertexVisitController {
      boolean acceptVisit(Graph g, Vertex v, Map<Vertex, Integer> visitedVertices );
   }

   private List<List<Edge>> findAllPath(Graph g, Vertex from, Vertex to, VertexVisitController controller) {
      return findAllPath(g, from, to, Collections.emptyMap(), controller);
   }

   private List<List<Edge>> findAllPath(Graph g, Vertex from, Vertex to, Map<Vertex,Integer> visitedVertices, VertexVisitController controller) {

      Map<Vertex, Integer> newVisitedVertices = Utils.Maps.add(visitedVertices, from, 1, Integer::sum);

      List<List<Edge>> result = new ArrayList<>();

      for (Vertex vertex : g.getReachableVertices(from)) {

         Edge edge = new Edge(from, vertex);

         if (vertex.equals(to)) {
            result.add(new LinkedList<>(Collections.singletonList(edge)));
         } else {

            if (!controller.acceptVisit(g, vertex, newVisitedVertices)) {
               continue;
            }

            for (List<Edge> partialPath : findAllPath(g, vertex, to, newVisitedVertices, controller)) {

               List<Edge> completePath = new ArrayList<>();
               completePath.add(edge);
               completePath.addAll(partialPath);
               result.add(completePath);
            }
         }
      }
      return result;
   }

   private Graph parse(String resourceName) throws IOException {

      List<Edge> vertices = Utils.readValuesFromResources(resourceName, Day12Test::parseVertex);
      return vertices.stream()
              .reduce(new Graph(), (g,e) -> g.addEdge(e.from(), e.to()), Graph::merge);
   }

   private static Edge parseVertex(String s) {
      List<String> splits = Utils.split(s, "-");
      return new Edge(new Vertex(splits.get(0)), new Vertex(splits.get(1)));
   }
}
