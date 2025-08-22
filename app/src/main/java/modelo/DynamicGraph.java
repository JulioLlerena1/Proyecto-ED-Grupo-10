/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 *
 * @author Chris
 */
public class DynamicGraph<V, E> {
    private List<Vertex<V, E>> vertices;
    private boolean directed;

    public DynamicGraph(boolean directed) {
        this.vertices = new ArrayList<>();
        this.directed = directed;
    }

    public List<Vertex<V, E>> getVertices() {
        return vertices;
    }

    // 1. Find vertex by content
    public Vertex<V, E> findVertex(V content) {
        if (content == null) return null;
        for (Vertex<V, E> v : vertices) {
            if (v.getValue().equals(content)) {
                return v;
            }
        }
        return null;
    }

    // 2. Add vertex
    public boolean addVertex(V content) {
        if (content == null || findVertex(content) != null) {
            return false;
        }
        Vertex<V, E> newVertex = new Vertex<>(content);
        vertices.add(newVertex);
        return true;
    }

    // 3. Connect two vertices (add edge)
    public boolean connect(V from, V to, double weight) {
        if (from == null || to == null) return false;

        Vertex<V, E> v1 = findVertex(from);
        Vertex<V, E> v2 = findVertex(to);

        if (v1 == null || v2 == null) return false;

        // Add edge v1 -> v2
        Edge<V, E> edge = new Edge<>(v1, v2, weight);
        v1.getEdges().add(edge);

        // If not directed, also add v2 -> v1
        if (!directed) {
            Edge<V, E> reverseEdge = new Edge<>(v2, v1, weight);
            v2.getEdges().add(reverseEdge);
        }
        return true;
    }



    public List<Vertex<V, E>> dijkstra(V startContent, V endContent) {
        Vertex<V, E> start = findVertex(startContent);
        Vertex<V, E> end = findVertex(endContent);
        if (start == null || end == null) return Collections.emptyList();

        // Reiniciar valores antes de ejecutar
        for (Vertex<V, E> v : vertices) {
            v.setDistance(Double.POSITIVE_INFINITY);
            v.setPredecessor(null);
            v.isVisited = false;
        }

        start.setDistance(0);

        // PriorityQueue by distance
        PriorityQueue<Vertex<V, E>> queue = new PriorityQueue<>(
                (a, b) -> Double.compare(a.getDistance(), b.getDistance())
        );
        queue.add(start);

        while (!queue.isEmpty()) {
            Vertex<V, E> u = queue.poll();

            if (u.isVisited) continue;
            u.isVisited = true;

            // Si llegamos al destino, salimos
            if (u.equals(end)) break;

            for (Edge<V, E> edge : u.getEdges()) {
                Vertex<V, E> v = edge.target;
                double newDist = u.getDistance() + edge.weight;

                if (newDist < v.getDistance()) {
                    v.setDistance(newDist);
                    v.setPredecessor(u);
                    queue.add(v);
                }
            }
        }

        // Reconstruir camino del destino al inicio
        List<Vertex<V, E>> path = new ArrayList<>();
        Vertex<V, E> current = end;
        while (current != null) {
            path.add(current);
            current = current.getPredecessor();
        }

        Collections.reverse(path);

        // Si no hay camino válido, devolver lista vacía
        if (path.isEmpty() || !path.get(0).equals(start)) {
            return Collections.emptyList();
        }

        return path;
    }


    public void printShortestPathsFrom(V startContent) {
        Vertex<V, E> start = findVertex(startContent);
        if (start == null) {
            System.out.println("Start vertex not found.");
            return;
        }

        System.out.println("Shortest paths from vertex: " + startContent);
        for (Vertex<V, E> v : vertices) {
            
            if (v != start) { // solo procesar si no es el vértice inicial
                System.out.print("To " + v.getValue() + ": ");

                if (v.getDistance() == Double.POSITIVE_INFINITY) {
                    System.out.println("No path");
                } else {
                    List<V> path = new ArrayList<>();
                    Vertex<V, E> current = v;
                    while (current != null) {
                        path.add(0, current.getValue());
                        current = current.getPredecessor();
                    }

                    System.out.print("Path: ");
                    for (int i = 0; i < path.size(); i++) {
                        System.out.print(path.get(i));
                        if (i < path.size() - 1) System.out.print(" -> ");
                    }
                    System.out.println(" | Distance: " + v.getDistance());
                }
            }
        }
    }


    // 4. Print graph
    public void printGraph() {
        for (Vertex<V, E> v : vertices) {
            System.out.print(v.getValue() + " -> ");
            for (Edge<V, E> e : v.getEdges()) {
                System.out.print(e.target.getValue() + "(" + e.weight + ") ");
            }
            System.out.println();
        }
    }
    
    
    public void bfs(V startContent) {
        Vertex<V, E> start = findVertex(startContent);
        for (Vertex<V, E> v : vertices) {
            v.isVisited = false;
        }
        Queue<Vertex<V, E>> queue = new LinkedList<>();
        start.isVisited = true;
        queue.add(start);
        while (!queue.isEmpty()) {
            Vertex<V, E> u = queue.poll(); // desencolar
            System.out.print(u.value + " ");

            // Revisar vecinos
            for (Edge<V, E> edge : u.edges) {
                Vertex<V, E> v = edge.target;
                if (!v.isVisited) {
                    v.isVisited = true;
                    queue.add(v);       // encolar
                }
            }
        }
        System.out.println();
    }

    public void dfs(V startContent) {
        Vertex<V, E> start = findVertex(startContent);

        for (Vertex<V, E> v : vertices) {
            v.isVisited = false;
        }

        Stack<Vertex<V, E>> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Vertex<V, E> u = stack.pop();

            if (!u.isVisited) {
                u.isVisited = true;
                System.out.print(u.value + " ");

                for (Edge<V, E> edge : u.edges) {
                    Vertex<V, E> v = edge.target;
                    if (!v.isVisited) {
                        stack.push(v);
                    }
                }
            }
        }
        System.out.println();
    }
    
    public DynamicGraph<V, E> bfsTreeGraph(V startContent) {
        Vertex<V, E> start = findVertex(startContent);

        // Nuevo grafo que será el árbol de expansión
        DynamicGraph<V, E> tree = new DynamicGraph<>(false);

        for (Vertex<V, E> v : vertices) {
            v.isVisited = false;
        }
        Queue<Vertex<V, E>> queue = new LinkedList<>();
        start.isVisited = true;
        queue.add(start);
        tree.addVertex(start.value); // agregar la raíz al árbol

        while (!queue.isEmpty()) {
            Vertex<V, E> u = queue.poll();

            for (Edge<V, E> edge : u.edges) {
                Vertex<V, E> v = edge.target;
                if (!v.isVisited) {
                    v.isVisited = true;
                    queue.add(v);

                    // Agregamos el nodo y la arista al árbol
                    tree.addVertex(v.value);
                    tree.connect(u.value, v.value, edge.weight); // suponiendo que tienes peso
                }
            }
        }
        return tree;
    }
    
    
        public DynamicGraph<V,E> primMST(V start) {
        DynamicGraph<V, E> mst = new DynamicGraph<>(false);  
        Vertex<V, E> inicio = findVertex(start);
        
        for (Vertex<V, E> v : vertices) {
            v.isVisited = false;
        }
        
        PriorityQueue<Edge<V,E>> pq = new PriorityQueue<>(
                (a,b) -> {return Double.compare(a.weight, b.weight);});
        
        inicio.isVisited = true;
        mst.addVertex(start);
        pq.addAll(inicio.getEdges());

        while (!pq.isEmpty()) {
            Edge<V,E> edge = pq.poll();
            if ((!edge.target.isVisited)) {
                
                // Agregar arista al árbol MST
                mst.addVertex(edge.target.value);
                mst.connect(edge.source.value, edge.target.value, edge.weight);
               
                edge.target.isVisited = true;
                // Agregar las aristas del nuevo nodo
                for (Edge<V,E> next : edge.target.getEdges()) {
                    if (!next.target.isVisited) {
                        pq.offer(next);
                    }
                }
            }
        }
        return mst;
    }
}
