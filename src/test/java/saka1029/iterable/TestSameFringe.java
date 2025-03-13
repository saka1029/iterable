package saka1029.iterable;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import saka1029.iterable.Generator.Context;

public class TestSameFringe {

    interface Tree {
    }

    static class Leaf implements Tree {
        final int value;

        Leaf(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "%d".formatted(value);
        }
    }

    static class Node implements Tree {
        final Tree left, right;

        Node(Tree left, Tree right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "(%s %s)".formatted(left, right);
        }
    }

    static Node node(Tree left, Tree right) {
        return new Node(left, right);
    }

    static List<Integer> list(Tree tree) {
        List<Integer> result = new ArrayList<>();
        new Object() {
            void visit(Tree t) {
                if (t instanceof Leaf leaf)
                    result.add(leaf.value);
                else if (t instanceof Node node) {
                    visit(node.left);
                    visit(node.right);
                }
            }
        }.visit(tree);
        return result;
    }

    static Leaf leaf(int value) {
        return new Leaf(value);
    }

    @Test
    public void testTree() {
        Tree tree = node(node(leaf(1), leaf(2)), leaf(3));
        System.out.println(tree);
        System.out.println(list(tree));
    }

    static void tree_leaves(Tree t, Context<Integer> c) throws InterruptedException {
        tree_leaves_sub(t, c);
        c.yield(null);
    }

    static void tree_leaves_sub(Tree t, Context<Integer> c) throws InterruptedException {
        if (t instanceof Leaf leaf)
            c.yield(leaf.value);
        else if (t instanceof Node node) {
            tree_leaves_sub(node.left, c);
            tree_leaves_sub(node.right, c);
        }
    }

    static boolean same_fringe(Tree tree1, Tree tree2) {
        try (Generator<Integer> g1 = Generator.of(c -> tree_leaves(tree1, c));
            Generator<Integer> g2 = Generator.of(c -> tree_leaves(tree2, c))) {
            Context<Integer> c1 = g1.context(), c2 = g2.context();
            while (true) {
                Integer tmp1 = c1.take(), tmp2 = c2.take();
                System.out.printf("tmp1=%s tmp2=%s", tmp1, tmp2);
                if (tmp1 != tmp2)
                    return false;
                if (tmp1 == null && tmp2 == null)
                    return true;
            }
        }
    }

    @Test
    public void testSameFringeByGenerator() {
        Tree t1 = node(node(leaf(1), leaf(2)), leaf(3));
        Tree t2 = node(leaf(1), node(leaf(2), leaf(3)));
        System.out.println("t1 = " + t1);
        System.out.println("t2 = " + t2);
        assertTrue(same_fringe(t1, t2));
    }
}
