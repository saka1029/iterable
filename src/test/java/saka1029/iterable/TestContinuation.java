package saka1029.iterable;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestContinuation {

    enum ResultType {
        CONTINUE,
        YIELD,
        BREAK,
        END;
    }

    record Result(ResultType type, int value) {}
    static final Result CONTINUE = new Result(ResultType.CONTINUE, -1);
    static final Result BREAK = new Result(ResultType.CONTINUE, -1);
    static final Result END = new Result(ResultType.END, -1);
    static Result returnValue(int value) { return new Result(ResultType.YIELD, value); }

    static class Context {
        int pc = 0;
        Map<String, Integer> vars = new HashMap<>();
    }
    
    interface Statement {
        Result run(Context c);
    }

    static Statement sequential(Statement... statements) {
        return c -> {
            while (c.pc < statements.length) {
                Result r = statements[c.pc++].run(c);
                if (r.type == ResultType.YIELD)
                    return r;
            }
            return END;
        };
    }

    @Test
    public void testStatement() {
        Statement statements = sequential(
            c -> { c.vars.put("a", 100); return CONTINUE; },
            c -> { c.vars.put("b", c.vars.get("a") + 10); return CONTINUE; },
            c -> returnValue(c.vars.get("b")),
            c -> returnValue(999)
        );
        Context context = new Context();
        assertEquals(returnValue(110), statements.run(context));
        assertEquals(returnValue(999), statements.run(context));
        assertEquals(END, statements.run(context));
    }
}
