package org.dynjs.spec.shims;

import org.dynjs.api.Function;
import org.dynjs.exception.DynJSException;
import org.dynjs.runtime.DynThreadContext;

public class FailShim implements Function {

    @Override
    public Object call(Object self, DynThreadContext context, Object... args) {
        throw new DynJSException(args.length == 1 ? args[0].toString() : "failed");
    }

    @Override
    public String[] getArguments() {
        return new String[]{"message"};
    }
}
