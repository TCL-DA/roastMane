@file:JvmName(name = "CharsetsKt")

package kotlin.text

import java.nio.charset.Charset
import kotlin.internal.InlineOnly

@InlineOnly
public inline fun charset(charsetName: String): Charset {
   var var10000: Charset = Charset.forName(charsetName);
   return var10000;
}

