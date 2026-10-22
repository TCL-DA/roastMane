package javafx.util;

@FunctionalInterface
public interface Callback<P, R> {
   R call(P var1);
}
