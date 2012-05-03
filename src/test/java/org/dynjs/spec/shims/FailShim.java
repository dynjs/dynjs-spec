package org.dynjs.spec.shims;

import org.dynjs.api.Function;
import org.dynjs.api.Scope;
import org.dynjs.exception.DynJSException;
import org.dynjs.runtime.DynThreadContext;

public class FailShim implements Function {
    @Override
    public void setContext(DynThreadContext context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object call(DynThreadContext context, Object[] arguments) {
        throw new DynJSException(arguments.length == 1 ? arguments[0].toString() : "failed");
    }

    @Override
    public Scope getEnclosingScope() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object resolve(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void define(String property, Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
