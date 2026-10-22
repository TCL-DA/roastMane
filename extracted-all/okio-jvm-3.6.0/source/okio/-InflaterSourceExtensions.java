@file:JvmName(name = "-InflaterSourceExtensions")

package okio

import java.util.zip.Inflater

public inline fun Source.inflate(inflater: Inflater): InflaterSource {
   return new InflaterSource(`$this$inflate`, inflater);
}

@JvmSynthetic
fun Source.`inflate$default`(inflater: Inflater, `$i$f$inflate`: Int, var3: Any): InflaterSource {
   if ((`$i$f$inflate` and 1) != 0) {
      inflater = new Inflater();
   }

   return new InflaterSource(`$this$inflate_u24default`, inflater);
}

