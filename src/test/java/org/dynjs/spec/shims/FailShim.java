package org.dynjs.spec.shims;

import org.dynjs.exception.DynJSException;
import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.Function;
import sun.org.mozilla.javascript.internal.Scriptable;

public class FailShim implements Function {
    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable1, Object[] objects) {
        throw new DynJSException(objects.length == 1 ? objects[0].toString() : "failed");
    }

    @Override
    public Scriptable construct(Context context, Scriptable scriptable, Object[] objects) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getClassName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object get(String s, Scriptable scriptable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object get(int i, Scriptable scriptable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean has(String s, Scriptable scriptable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean has(int i, Scriptable scriptable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void put(String s, Scriptable scriptable, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void put(int i, Scriptable scriptable, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Scriptable getPrototype() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPrototype(Scriptable scriptable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Scriptable getParentScope() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setParentScope(Scriptable scriptable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object[] getIds() {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getDefaultValue(Class<?> aClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasInstance(Scriptable scriptable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
