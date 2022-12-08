package de.comparus.opensource.longmap.tree.impl;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
  T data;



  Node<T> left;
  Node<T> right;
  Node<T> parent;

  boolean color;

  public Node(T data) {
    this.data = data;
  }

  public T getData() {
    return data;
  }

  public Node<T> getLeft() {
    return left;
  }

  public Node<T> getRight() {
    return right;
  }

  public Node<T> getParent() {
    return parent;
  }

  public boolean getColor() {
    return color;
  }

  public List<Node<T>> getAllNodes() {
    List<Node<T>> subnodes = new ArrayList<>();

    if (parent == null) {
      subnodes.add(this);
    }

    if (left != null) {
      subnodes.add(left);
      subnodes.addAll(left.getAllNodes());
    }

    if (right != null) {
      subnodes.add(right);
      subnodes.addAll(right.getAllNodes());
    }

    return subnodes;
  }

  public List<List<Node<T>>> getAllBranches() {
    return getAllBranches(this);
  }

  private List<List<Node<T>>> getAllBranches(Node<T> node){
    List<List<Node<T>>> paths = new ArrayList<>();
    List<List<Node<T>>> leftSubtree = new ArrayList<>();
    List<List<Node<T>>> rightSubtree = new ArrayList<>();

    if (node.left != null) {
      leftSubtree = getAllBranches(node.left);
    }

    if (node.right != null) {
      rightSubtree = getAllBranches(node.right);
    }

    extractBranches(node, paths, leftSubtree);

    extractBranches(node, paths, rightSubtree);

    if (paths.size() == 0){
      paths.add(new ArrayList<>());
      paths.get(0).add(node);
    }

    return paths;
  }

  private void extractBranches(Node<T> node, List<List<Node<T>>> paths, List<List<Node<T>>> subtree) {
    for (List<Node<T>> branch : subtree) {
      List<Node<T>> path = new ArrayList<>();
      path.add(node);
      path.addAll(branch);
      paths.add(path);
    }
  }
}