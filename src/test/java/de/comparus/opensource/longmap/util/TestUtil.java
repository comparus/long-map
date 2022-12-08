package de.comparus.opensource.longmap.util;

import de.comparus.opensource.longmap.tree.BinarySearchTree;
import de.comparus.opensource.longmap.tree.impl.Node;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TestUtil {
    public static final int BOUND = 32000;
    private static final Random RANDOM = new Random();
    public static <T> void apply(int amount, int bound, BiFunction<Long, Integer, T> operator) {
        for (int i = 0; i < amount; i++) {
            long key = RANDOM.nextLong();
            int value = RANDOM.nextInt(bound);

            operator.apply(key, value);
        }
    }

    public static void apply(int amount, int bound, Consumer<Integer> operator) {
        for (int i = 0; i < amount; i++) {
            int value = RANDOM.nextInt(bound);

            operator.accept(value);
        }
    }

    public static boolean checkNodesOnCondition(BinarySearchTree<Integer> tree, Predicate<Node<Integer>> predicate) {
        return tree.getRoot().getAllNodes().stream()
                .filter(predicate)
                .findFirst()
                .isEmpty();
    }
}
