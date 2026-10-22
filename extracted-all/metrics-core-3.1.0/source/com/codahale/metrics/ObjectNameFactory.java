package com.codahale.metrics;

import javax.management.ObjectName;

public interface ObjectNameFactory {
   ObjectName createName(String var1, String var2, String var3);
}
