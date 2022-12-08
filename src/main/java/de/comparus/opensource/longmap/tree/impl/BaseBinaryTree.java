package de.comparus.opensource.longmap.tree.impl;

import de.comparus.opensource.longmap.tree.BinaryTree;

public class BaseBinaryTree<T> implements BinaryTree<T> {

  protected Node<T> root;

  @Override
  public Node<T> getRoot() {
    return root;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    appendNodeToStringRecursive(getRoot(), builder);
    return builder.toString();
  }

  private void appendNodeToStringRecursive(Node<T> node, StringBuilder builder) {
    appendNodeToString(node, builder);
    if (node.left != null) {
      builder.append(" L{");
      appendNodeToStringRecursive(node.left, builder);
      builder.append('}');
    }
    if (node.right != null) {
      builder.append(" R{");
      appendNodeToStringRecursive(node.right, builder);
      builder.append('}');
    }
  }

  protected void appendNodeToString(Node<T> node, StringBuilder builder) {
    builder.append(node.data);
  }
}