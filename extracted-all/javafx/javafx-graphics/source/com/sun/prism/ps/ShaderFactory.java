package com.sun.prism.ps;

import com.sun.prism.ResourceFactory;
import java.io.InputStream;
import java.util.Map;

public interface ShaderFactory extends ResourceFactory {
   Shader createShader(InputStream var1, Map<String, Integer> var2, Map<String, Integer> var3, int var4, boolean var5, boolean var6);

   Shader createStockShader(String var1);
}
