package okio.internal

private class EocdRecord(entryCount: Long, centralDirectoryOffset: Long, commentByteCount: Int) {
   public final val centralDirectoryOffset: Long
   public final val commentByteCount: Int
   public final val entryCount: Long

   init {
      this.entryCount = entryCount;
      this.centralDirectoryOffset = centralDirectoryOffset;
      this.commentByteCount = commentByteCount;
   }
}
