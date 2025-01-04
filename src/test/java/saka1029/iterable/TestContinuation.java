package saka1029.iterable;

import java.util.HashMap;
import java.util.Map;

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

    static class Context {
        int pc = 0;
        Map<String, Integer> vars = new HashMap<>();
    }
    
    interface Statement {
        Result run(Context c);
    }

    static Statement sequencial(Statement... statements) {
        return c -> {
            while (c.pc < statements.length) {
                Result r = statements[c.pc++].run(c);
                if (r.type == ResultType.YIELD)
                    return r;
            }
            return END;
        };
    }
}
