package kotlin.text

import kotlin.internal.InlineOnly
import kotlin.text.HexFormat.Builder

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@InlineOnly
public inline fun HexFormat(builderAction: (Builder) -> Unit): HexFormat {
   var var1: HexFormat.Builder = new HexFormat.Builder();
   builderAction.invoke(var1);
   return var1.build();
}

