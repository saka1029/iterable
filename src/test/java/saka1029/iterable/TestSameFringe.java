package saka1029.iterable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
                for ( ; Character.isDigit(ch); get())
                    value = value * 10 + Character.digit(ch, 10);
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

    @Test
    public void testTree() {
        Tree tree = parse("((1 2) 3)");
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
        Tree t1 = parse("((1 2) 3)");
        Tree t2 = parse("(1 (2 3))");
        System.out.println("t1 = " + t1);
        System.out.println("t2 = " + t2);
        assertTrue(same_fringe(t1, t2));
        Tree t3 = parse("(1 (2 (3 4)))");
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
        Tree t1 = parse("((1 2) 3)");
        Tree t2 = parse("(1 (2 3))");
        assertTrue(same_fringe_by_iterator(t1, t2));
        Tree t3 = parse("(1 (2 (3 4)))");
        assertFalse(same_fringe_by_iterator(t1, t3));
        Tree t4 = parse("(1 (2 (3 4)))");
        Tree t5 = parse("(((1 2) 3) 4)");
        assertTrue(same_fringe_by_iterator(t4, t5));
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

    /**
     * Treeの各要素を左から右に向かって順に取り出します。
     * @param tree
     * @return
     */
    static Iterator<Integer> iterator(Tree tree) {
        Deque<Tree> stack = new LinkedList<>();
        stack.push(tree);
        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public Integer next() {
                while (!stack.isEmpty()) {
                    Tree tree = stack.pop();
                    if (tree instanceof Leaf leaf) {
                        return leaf.value;
                    } else if (tree instanceof Node node) {
                        // Nodeの場合はその子要素をスタックにプッシュします。
                        stack.push(node.right); // 後で処理するrightを先にプッシュします。
                        stack.push(node.left);  // 先に処理するleftを後にプッシュします。
                    } else
                        throw new RuntimeException("Unknown object '%s'".formatted(tree));
                }
                throw new NoSuchElementException();
            }

        };
    }

    static <T> boolean equal(Iterator<T> left, Iterator<T> right) {
        while (left.hasNext() && right.hasNext())
            if (!left.next().equals(right.next()))
                return false;
        return !left.hasNext() && !right.hasNext();
    }

    static Iterable<Integer> iterable(Tree tree) {
        return () -> iterator(tree);
    }

    @Test
    public void testSerializer() {
        Tree root1 = parse("(((1 2) 3) 4)");
        Iterator<Integer> i1 = iterator(root1);
        assertEquals(1, (int)i1.next());
        assertEquals(2, (int)i1.next());
        assertEquals(3, (int)i1.next());
        assertEquals(4, (int)i1.next());
        assertFalse(i1.hasNext());
        Tree root2 = parse("(1 (2 (3 4)))");
        Iterator<Integer> i2 = iterator(root2);
        assertEquals(1, (int)i2.next());
        assertEquals(2, (int)i2.next());
        assertEquals(3, (int)i2.next());
        assertEquals(4, (int)i2.next());
        assertFalse(i2.hasNext());
    }

    @Test
    public void testCompare() {
        Tree root1 = parse("(((1 2) 3) 4)");
        Tree root2 = parse("(1 (2 (3 4)))");
        assertTrue(equal(iterator(root1), iterator(root2)));
    }

    static Tree n(Tree left, Tree right) { return new Node(left, right); }
    static Tree n(int left, Tree right) { return new Node(new Leaf(left), right); }
    static Tree n(Tree left, int right) { return new Node(left, new Leaf(right)); }
    static Tree n(int left, int right) { return new Node(new Leaf(left), new Leaf(right)); }

    @Test
    public void testCompare2() {
        Tree root1 = n(n(n(1, 2), 3), 4);
        Tree root2 = n(1, n(2, n(3, 4)));
        assertTrue(equal(iterator(root1), iterator(root2)));
    }
}
