package de.comparus.opensource.longmap.tree.impl;

import de.comparus.opensource.longmap.tree.BinarySearchTree;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RedBlackTree<T extends Comparable<T>>
        extends BaseBinaryTree<T> implements BinarySearchTree<T> {

    public static final boolean RED = false;
    public static final boolean BLACK = true;
    private final Comparator<T> comparator;

    private RedBlackTree(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    private RedBlackTree() {
        this.comparator = Comparator.naturalOrder();
    }

    public static <T extends Comparable<T>> RedBlackTree<T> of(T value) {
        RedBlackTree<T> redBlackTree = new RedBlackTree<>();
        redBlackTree.root = new Node<>(value);
        return redBlackTree;
    }

    public static <T extends Comparable<T>> RedBlackTree<T> of(T value, Comparator<T> comparator) {
        RedBlackTree<T> redBlackTree = new RedBlackTree<>(comparator);
        redBlackTree.root = new Node<>(value);
        return redBlackTree;
    }

    public static <T extends Comparable<T>> RedBlackTree<T> of(T root, Comparator<T> comparator, List<T> nodes) {
        RedBlackTree<T> redBlackTree = new RedBlackTree<>(comparator);
        redBlackTree.root = new Node<>(root);
        nodes.forEach(redBlackTree::insertNode);
        return redBlackTree;
    }

    @Override
    public T searchNode(T key) {
        Node<T> node = root;
        while (node != null) {
            if (comparator.compare(key, node.data) == 0) {
                return node.getData();
            } else if (comparator.compare(key, node.data) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return null;
    }

    @Override
    public void insertNode(T key) {
        Node<T> node = root;
        Node<T> parent = null;

        while (node != null) {
            parent = node;
            if (comparator.compare(key, node.data) < 0) {
                node = node.left;
            } else if (comparator.compare(key, node.data) > 0) {
                node = node.right;
            } else {
                return;
            }
        }

        Node<T> newNode = new Node<>(key);
        newNode.color = RED;

        if (parent == null) {
            root = newNode;
        } else if (comparator.compare(key, parent.data) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        newNode.parent = parent;

        fixRedBlackPropertiesAfterInsert(newNode);
    }

    @Override
    public T deleteNode(T key) {
        Node<T> node = root;

        while (node != null && comparator.compare(key, node.data) != 0) {
            if (comparator.compare(key, node.data) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        if (node == null) {
            return null;
        }

        T nodeData = node.data;

        Node<T> movedUpNode;
        boolean deletedNodeColor;

        if (node.left == null || node.right == null) {
            movedUpNode = deleteNodeWithZeroOrOneChild(node);
            deletedNodeColor = node.color;
        } else {
            Node<T> inOrderSuccessor = findMinimum(node.right);

            node.data = inOrderSuccessor.data;

            movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
            deletedNodeColor = inOrderSuccessor.color;
        }

        if (deletedNodeColor == BLACK) {
            fixRedBlackPropertiesAfterDelete(movedUpNode);

            if (movedUpNode.getClass() == NilNode.class) {
                replaceParentsChild(movedUpNode.parent, movedUpNode, null);
            }
        }

        return nodeData;
    }

    @Override
    public List<T> getAll() {
        return root.getAllNodes().stream()
                .map(Node::getData)
                .collect(Collectors.toList());
    }

    @Override
    public List<List<Node<T>>> getAllBranches() {
        return root.getAllBranches();
    }

    private void fixRedBlackPropertiesAfterInsert(Node<T> node) {
        Node<T> parent = node.parent;

        if (parent == null) {
            node.color = BLACK;
            return;
        }

        if (parent.color == BLACK) {
            return;
        }

        Node<T> grandParent = parent.parent;

        if (grandParent == null) {
            parent.color = BLACK;
            return;
        }

        Node<T> uncle = getUncle(parent);

        if (uncle != null && uncle.color == RED) {
            parent.color = BLACK;
            grandParent.color = RED;
            uncle.color = BLACK;

            fixRedBlackPropertiesAfterInsert(grandParent);
        }

        else if (parent == grandParent.left) {
            if (node == parent.right) {
                rotateLeft(parent);
                parent = node;
            }

            rotateRight(grandParent);

            parent.color = BLACK;
            grandParent.color = RED;
        }

        else {
            if (node == parent.left) {
                rotateRight(parent);
                parent = node;
            }

            rotateLeft(grandParent);

            parent.color = BLACK;
            grandParent.color = RED;
        }
    }

    private Node<T> getUncle(Node<T> parent) {
        Node<T> grandparent = parent.parent;
        if (grandparent.left == parent) {
            return grandparent.right;
        } else if (grandparent.right == parent) {
            return grandparent.left;
        } else {
            throw new IllegalStateException("Parent is not a child of its grandparent");
        }
    }



    private Node<T> deleteNodeWithZeroOrOneChild(Node<T> node) {
        if (node.left != null) {
            replaceParentsChild(node.parent, node, node.left);
            return node.left;
        } else if (node.right != null) {
            replaceParentsChild(node.parent, node, node.right);
            return node.right;
        } else {
            Node<T> newChild = node.color == BLACK ? new NilNode<>() : null;
            replaceParentsChild(node.parent, node, newChild);
            return newChild;
        }
    }

    private Node<T> findMinimum(Node<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private void fixRedBlackPropertiesAfterDelete(Node<T> node) {
        if (node == root) {
            return;
        }

        Node<T> sibling = getSibling(node);

        if (sibling.color == RED) {
            handleRedSibling(node, sibling);
            sibling = getSibling(node);
        }

        if (isBlack(sibling.left) && isBlack(sibling.right)) {
            sibling.color = RED;

            if (node.parent.color == RED) {
                node.parent.color = BLACK;
            } else {
                fixRedBlackPropertiesAfterDelete(node.parent);
            }

        } else {
            handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
        }
    }

    private void handleRedSibling(Node<T> node, Node<T> sibling) {
        sibling.color = BLACK;
        node.parent.color = RED;

        if (node == node.parent.left) {
            rotateLeft(node.parent);
        } else {
            rotateRight(node.parent);
        }
    }

    private void handleBlackSiblingWithAtLeastOneRedChild(Node<T> node, Node<T> sibling) {
        boolean nodeIsLeftChild = node == node.parent.left;

        if (nodeIsLeftChild && isBlack(sibling.right)) {
            sibling.left.color = BLACK;
            sibling.color = RED;
            rotateRight(sibling);
            sibling = node.parent.right;
        } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
            sibling.right.color = BLACK;
            sibling.color = RED;
            rotateLeft(sibling);
            sibling = node.parent.left;
        }

        sibling.color = node.parent.color;
        node.parent.color = BLACK;
        if (nodeIsLeftChild) {
            sibling.right.color = BLACK;
            rotateLeft(node.parent);
        } else {
            sibling.left.color = BLACK;
            rotateRight(node.parent);
        }
    }

    private Node<T> getSibling(Node<T> node) {
        Node<T> parent = node.parent;
        if (node == parent.left) {
            return parent.right;
        } else if (node == parent.right) {
            return parent.left;
        } else {
            throw new IllegalStateException("Parent is not a child of its grandparent");
        }
    }

    private boolean isBlack(Node<T> node) {
        return node == null || node.color == BLACK;
    }

    private static class NilNode<T> extends Node<T> {
        private NilNode() {
            super(null);
            this.color = BLACK;
        }
    }

    private void rotateRight(Node<T> node) {
        Node<T> parent = node.parent;
        Node<T> leftChild = node.left;

        node.left = leftChild.right;
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        leftChild.right = node;
        node.parent = leftChild;

        replaceParentsChild(parent, node, leftChild);
    }

    private void rotateLeft(Node<T> node) {
        Node<T> parent = node.parent;
        Node<T> rightChild = node.right;

        node.right = rightChild.left;
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        rightChild.left = node;
        node.parent = rightChild;

        replaceParentsChild(parent, node, rightChild);
    }

    private void replaceParentsChild(Node<T> parent, Node<T> oldChild, Node<T> newChild) {
        if (parent == null) {
            root = newChild;
        } else if (parent.left == oldChild) {
            parent.left = newChild;
        } else if (parent.right == oldChild) {
            parent.right = newChild;
        } else {
            throw new IllegalStateException("Node is not a child of its parent");
        }

        if (newChild != null) {
            newChild.parent = parent;
        }
    }

    @Override
    protected void appendNodeToString(Node<T> node, StringBuilder builder) {
        builder.append(node.data).append(node.color == RED ? "[R]" : "[B]");
    }
}
