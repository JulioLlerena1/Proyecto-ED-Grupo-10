/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chris
 */
class Vertex<V, E> {
    V value; // vertex content
    List<Edge<V, E>> edges; // outgoing edges
    double distance; // accumulated distance
    boolean isVisited;
    Vertex<V, E> predecessor; // previous vertex in shortest path

    public Vertex(V value) {
        this.value = value;
        this.edges = new ArrayList<>();
        this.distance = Double.POSITIVE_INFINITY; // initially infinity
        this.predecessor = null;
        this.isVisited = false;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void setEdges(List<Edge<V, E>> edges) {
        this.edges = edges;
    }

    public void setPredecessor(Vertex<V, E> predecessor) {
        this.predecessor = predecessor;
    }
    
    

    public Vertex<V, E> getPredecessor() {
        return predecessor;
    }
    
    
    
    public V getValue() {
        return value;
    }

    public List<Edge<V, E>> getEdges() {
        return edges;
    }
}
