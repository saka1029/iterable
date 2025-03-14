package saka1029.iterable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Test;
import saka1029.iterable.Generator.Context;

/**
 * same fringe test
 * https://wiki.c2.com/?SameFringeProblem
 * 以下のツリーが同一であるかことを
 * 判別する。
 * (1 (2 3)), ((1 2) 3)
 */
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
                System.out.printf("tmp1=%s tmp2=%s%n", tmp1, tmp2);
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
        Tree t3 = node(leaf(1), node(leaf(2), node(leaf(3), leaf(4))));
        assertFalse(same_fringe(t1, t3));
    }

    static void traverse(Context<Integer> c, Tree tree) throws InterruptedException {
        if (tree instanceof Leaf leaf)
            c.yield(leaf.value);
        else if (tree instanceof Node node) {
            traverse(c, node.left);
            traverse(c, node.right);
        }
    }

    static boolean same_fringe_by_iterator(Tree tree1, Tree tree2) {
        try (Generator<Integer> g1 = Generator.of(c -> traverse(c, tree1));
            Generator<Integer> g2 = Generator.of(c -> traverse(c, tree2))) {
            Iterator<Integer> i1 = g1.iterator(), i2 = g2.iterator();
            while (i1.hasNext() && i2.hasNext())
                if (!i1.next().equals(i2.next()))
                    return false;
            return !(i1.hasNext() || i2.hasNext());
        }
    }

    @Test
    public void testSameFringeByIterator() {
        Tree t1 = node(node(leaf(1), leaf(2)), leaf(3));
        Tree t2 = node(leaf(1), node(leaf(2), leaf(3)));
        assertTrue(same_fringe_by_iterator(t1, t2));
        Tree t3 = node(leaf(1), node(leaf(2), node(leaf(3), leaf(4))));
        assertFalse(same_fringe_by_iterator(t1, t3));
        Tree t4 = node(leaf(1), node(leaf(2), node(leaf(3), leaf(4))));
        Tree t5 = node(node(node(leaf(1), leaf(2)), leaf(3)), leaf(4));
        assertTrue(same_fringe_by_iterator(t4, t5));
    }

    /**
     * parse tree string
     * SYNTAX
     * <pre>
     * tree = node | leaf
     * node = '(' tree tree ')'
     * leaf = NUMBER
     * </pre>
     */
    static Tree parse(String input) {
        return new Object() {
            int index = 0, ch = get();

            int get() {
                return ch = index < input.length() ? input.charAt(index++) : -1;
            }

            void spaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            Node node() {
                get(); // skip '('
                Tree left = tree(), right = tree();
                spaces();
                if (ch != ')')
                    throw new RuntimeException("')' expected");
                get(); // skip ')'
                return new Node(left, right);
            }

            Leaf number() {
                int value = 0;
                while (Character.isDigit(ch)) {
                    value = value * 10 + Character.digit(ch, 10);
                    get();
                }
                return new Leaf(value);
            }

            Tree tree() {
                spaces();
                if (ch == -1)
                    throw new RuntimeException("Unexpected end");
                else if (ch == '(')
                    return node();
                else if (ch == ')')
                    throw new RuntimeException("Unexpected ')'");
                else if (Character.isDigit(ch))
                    return number();
                else
                    throw new RuntimeException("Unexpected '%c'".formatted((char)ch));
            }
        }.tree();
    }

    @Test
    public void testParse() {
        assertEquals("(1 (2 (3 4)))", parse("(  1 (  2 (  3   4  )  )  )").toString());
        assertEquals("(((1 2) 3) 4)", parse("(((1 2) 3) 4)").toString());
    }

    @Test
    public void testSameFringeForParsedTree() {
        assertTrue(same_fringe_by_iterator(parse("(1 (2 (3 4)))"), parse("(((1 2) 3) 4)")));
        assertFalse(same_fringe_by_iterator(parse("(1 (2 (3 (4 5))))"), parse("(((1 2) 3) 4)")));
    }

    static class Serializer implements Iterable<Integer> {
        final Tree root;

        public Serializer(Tree root) {
            this.root = root;
        }

        @Override
        public Iterator<Integer> iterator() {
            final Deque<Tree> que = new LinkedList<>();
            que.push(root);
            return new Iterator<>() {

                @Override
                public boolean hasNext() {
                    return !que.isEmpty();
                }

                @Override
                public Integer next() {
                    if (que.isEmpty())
                        throw new NoSuchElementException();
                    while (que.peek() instanceof Node) {
                        Node node = (Node)que.pop();
                        que.push(node.right);
                        que.push(node.left);
                    }
                    return ((Leaf)que.pop()).value;
                }
            };
        }
    }

    @Test
    public void testSerializer() {
        Tree root1 = parse("(((1 2) 3) 4)");
        Iterator<Integer> i1 = new Serializer(root1).iterator();
        assertEquals(1, (int)i1.next());
        assertEquals(2, (int)i1.next());
        assertEquals(3, (int)i1.next());
        assertEquals(4, (int)i1.next());
        assertFalse(i1.hasNext());
        Tree root2 = parse("(1 (2 (3 4)))");
        Iterator<Integer> i2 = new Serializer(root2).iterator();
        assertEquals(1, (int)i2.next());
        assertEquals(2, (int)i2.next());
        assertEquals(3, (int)i2.next());
        assertEquals(4, (int)i2.next());
        assertFalse(i2.hasNext());
    }
}
