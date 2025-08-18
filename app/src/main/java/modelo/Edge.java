/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Chris
 */
public class Edge<V, E> {
    E data; 
    Vertex<V, E> source;
    Vertex<V, E> target;
    double weight;

    public Edge(Vertex<V, E> source, Vertex<V, E> target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }
    
    
}
