package okhttp3.internal.ws

import okio.ByteString

private final val EMPTY_DEFLATE_BLOCK: ByteString = ByteString.Companion.decodeHex("000000ffff")
private const val LAST_OCTETS_COUNT_TO_REMOVE_AFTER_DEFLATION: Int = 4

@JvmSynthetic
fun `access$getEMPTY_DEFLATE_BLOCK$p`(): ByteString {
   return EMPTY_DEFLATE_BLOCK;
}

