package de.comparus.opensource.longmap.tree;

import de.comparus.opensource.longmap.tree.impl.Node;
import de.comparus.opensource.longmap.tree.impl.RedBlackTree;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static de.comparus.opensource.longmap.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class RedBlackTreeTest {
    private BinarySearchTree<Integer> tree;

    @Test
    void insertNodeRight() {
        tree = RedBlackTree.of(0);

        tree.insertNode(5);

        Node<Integer> root = tree.getRoot();

        assertNotNull(root.getRight());
        assertEquals(5, root.getRight().getData());
    }

    @Test
    void insertNodeLeft() {
        tree = RedBlackTree.of(0);

        tree.insertNode(-3);

        Node<Integer> root = tree.getRoot();

        assertNotNull(root.getLeft());
        assertEquals(-3, root.getLeft().getData());
    }

    @Test
    void searchNode() {
        tree = RedBlackTree.of(0);

        tree.insertNode(5);
        Integer node = tree.searchNode(5);

        assertNotNull(node);
        assertEquals(5, node);
    }

    @RepeatedTest(5)
    void fillTreeAndCheckViolations() {
        tree = RedBlackTree.of(0);

        fillTree(tree, 20);

        assertTrue(checkIfRootIsBlack(tree));

        assertTrue(checkIfNumberOfBlackNodesForEveryBranchIsTheSame(tree));

        assertTrue(checkIfTreeDoesNotHaveAdjacentRedNodes(tree));
    }

    @RepeatedTest(5)
    void removeRandomElementsFromTreeAndCheckViolations() {
        tree = RedBlackTree.of(0);
        List<Integer> list = new ArrayList<>();

        fillTree(tree, list,20);

        removeRandomElements(tree, list, 5);

        assertTrue(checkIfRootIsBlack(tree));

        assertTrue(checkIfNumberOfBlackNodesForEveryBranchIsTheSame(tree));

        assertTrue(checkIfTreeDoesNotHaveAdjacentRedNodes(tree));
    }

    private boolean checkIfRootIsBlack(BinarySearchTree<Integer> tree) {
        return tree.getRoot().getColor() == RedBlackTree.BLACK;
    }

    private boolean checkIfNumberOfBlackNodesForEveryBranchIsTheSame(BinarySearchTree<Integer> tree) {
        return tree.getAllBranches().stream()
                .map(branch -> branch.stream()
                        .filter(n -> n.getColor() == RedBlackTree.BLACK)
                        .count())
                .distinct()
                .count() == 1;
    }

    private boolean checkIfTreeDoesNotHaveAdjacentRedNodes(BinarySearchTree<Integer> tree) {
        return checkNodesOnCondition(tree, n -> {
            if (n.getColor() == RedBlackTree.RED) {
                Node<Integer> left = n.getLeft();
                Node<Integer> right = n.getRight();

                if (left == null && right == null) {
                    return false;
                }
                if (left == null) {
                    return right.getColor() == RedBlackTree.RED;
                }
                if (right == null) {
                    return left.getColor() == RedBlackTree.RED;
                }
                return left.getColor() == RedBlackTree.RED || right.getColor() == RedBlackTree.RED;
            }
            return false;
        });
    }

    private void fillTree(BinarySearchTree<Integer> tree, List<Integer> values, Integer amount) {
        apply(amount, BOUND, value -> {
            tree.insertNode(value);
            values.add(value);
        });
    }

    private void fillTree(BinarySearchTree<Integer> tree, Integer amount) {
        apply(amount, BOUND, tree::insertNode);
    }

    private void removeRandomElements(BinarySearchTree<Integer> tree, List<Integer> values, Integer amount) {
        apply(amount, values.size(), index -> tree.deleteNode(values.get(index)));
    }
}