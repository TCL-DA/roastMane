package org.jvnet.hk2.internal;

import java.lang.reflect.Constructor;

public interface ConstructorAction {
   Object makeMe(Constructor<?> var1, Object[] var2, boolean var3) throws Throwable;
}
