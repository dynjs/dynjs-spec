package org.dynjs.spec.shims;

import org.dynjs.exception.ThrowException;
import org.dynjs.runtime.AbstractNativeFunction;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.GlobalContext;

public class FailShim extends AbstractNativeFunction {

    public FailShim(GlobalContext globalContext) {
        super(globalContext, "message");
    }

    @Override
    public Object call(ExecutionContext context, Object self, Object... args) {
        throw new ThrowException(context, context.createError("Error", args.length == 1 ? args[0].toString() : "test failure"));
    }
}
