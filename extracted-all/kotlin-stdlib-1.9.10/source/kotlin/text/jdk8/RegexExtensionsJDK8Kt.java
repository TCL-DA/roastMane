@file:JvmName(name = "RegexExtensionsJDK8Kt")

package kotlin.text.jdk8

@SinceKotlin(version = "1.2")
public operator fun MatchGroupCollection.get(name: String): MatchGroup? {
   var var10000: MatchNamedGroupCollection = `$this$get` as? MatchNamedGroupCollection;
   if ((`$this$get` as? MatchNamedGroupCollection) == null) {
      throw new UnsupportedOperationException("Retrieving groups by name is not supported on this platform.");
   } else {
      return var10000.get(name);
   }
}

