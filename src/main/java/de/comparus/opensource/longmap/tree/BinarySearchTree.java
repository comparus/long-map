package de.comparus.opensource.longmap.tree;

import de.comparus.opensource.longmap.tree.impl.Node;

import java.util.List;

public interface BinarySearchTree<T> extends BinaryTree<T> {

  T searchNode(T key);

  void insertNode(T key);

  T deleteNode(T key);

  List<T> getAll();

  List<List<Node<T>>> getAllBranches();
}
