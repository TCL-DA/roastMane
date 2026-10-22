package javafx.animation;

@FunctionalInterface
public interface Interpolatable<T> {
   T interpolate(T var1, double var2);
}
