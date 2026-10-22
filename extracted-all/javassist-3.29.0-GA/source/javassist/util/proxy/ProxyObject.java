package javassist.util.proxy;

public interface ProxyObject extends Proxy {
   @Override
   void setHandler(MethodHandler var1);

   MethodHandler getHandler();
}
