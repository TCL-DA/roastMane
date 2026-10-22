package org.aopalliance.instrument;

import org.aopalliance.reflect.ClassLocator;
import org.aopalliance.reflect.Code;
import org.aopalliance.reflect.CodeLocator;

public interface Instrumentor {
   ClassLocator createClass(String var1) throws InstrumentationError;

   Instrumentation addInterface(ClassLocator var1, String var2) throws InstrumentationError;

   Instrumentation setSuperClass(ClassLocator var1, String var2) throws InstrumentationError;

   Instrumentation addClass(ClassLocator var1, String var2) throws InstrumentationError;

   Instrumentation addMethod(ClassLocator var1, String var2, String[] var3, String[] var4, Code var5) throws InstrumentationError;

   Instrumentation addField(ClassLocator var1, String var2, String var3, Code var4) throws InstrumentationError;

   Instrumentation addBeforeCode(CodeLocator var1, Code var2, Instrumentation var3, Instrumentation var4) throws InstrumentationError;

   Instrumentation addAfterCode(CodeLocator var1, Code var2, Instrumentation var3, Instrumentation var4) throws InstrumentationError;

   Instrumentation addAroundCode(CodeLocator var1, Code var2, String var3, Instrumentation var4, Instrumentation var5) throws InstrumentationError;

   void undo(Instrumentation var1) throws UndoNotSupportedException;
}
