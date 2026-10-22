package nonapi.io.github.classgraph.fastzipfilereader;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import nonapi.io.github.classgraph.fileslice.Slice;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessReader;
import nonapi.io.github.classgraph.utils.VersionFinder;

public class FastZipEntry implements Comparable<FastZipEntry> {
   final LogicalZipFile parentLogicalZipFile;
   private final long locHeaderPos;
   public final String entryName;
   final boolean isDeflated;
   public final long compressedSize;
   public final long uncompressedSize;
   private long lastModifiedTimeMillis;
   private final int lastModifiedTimeMSDOS;
   private final int lastModifiedDateMSDOS;
   public final int fileAttributes;
   private Slice slice;
   final int version;
   public final String entryNameUnversioned;

   FastZipEntry(
      LogicalZipFile parentLogicalZipFile,
      long locHeaderPos,
      String entryName,
      boolean isDeflated,
      long compressedSize,
      long uncompressedSize,
      long lastModifiedTimeMillis,
      int lastModifiedTimeMSDOS,
      int lastModifiedDateMSDOS,
      int fileAttributes
   ) {
      this.parentLogicalZipFile = parentLogicalZipFile;
      this.locHeaderPos = locHeaderPos;
      this.entryName = entryName;
      this.isDeflated = isDeflated;
      this.compressedSize = compressedSize;
      this.uncompressedSize = !isDeflated && uncompressedSize < 0L ? compressedSize : uncompressedSize;
      this.lastModifiedTimeMillis = lastModifiedTimeMillis;
      this.lastModifiedTimeMSDOS = lastModifiedTimeMSDOS;
      this.lastModifiedDateMSDOS = lastModifiedDateMSDOS;
      this.fileAttributes = fileAttributes;
      int entryVersion = 8;
      String entryNameWithoutVersionPrefix = entryName;
      if (entryName.startsWith("META-INF/versions/") && entryName.length() > "META-INF/versions/".length() + 1) {
         int nextSlashIdx = entryName.indexOf(47, "META-INF/versions/".length());
         if (nextSlashIdx > 0) {
            String versionStr = entryName.substring("META-INF/versions/".length(), nextSlashIdx);
            int versionInt = 0;
            if (versionStr.length() < 6 && !versionStr.isEmpty()) {
               for (int i = 0; i < versionStr.length(); i++) {
                  char c = versionStr.charAt(i);
                  if (c < '0' || c > '9') {
                     versionInt = 0;
                     break;
                  }

                  if (versionInt == 0) {
                     versionInt = c - '0';
                  } else {
                     versionInt = versionInt * 10 + c - 48;
                  }
               }
            }

            if (versionInt != 0) {
               entryVersion = versionInt;
            }

            if (entryVersion < 9 || entryVersion > VersionFinder.JAVA_MAJOR_VERSION) {
               entryVersion = 8;
            }

            if (entryVersion > 8) {
               entryNameWithoutVersionPrefix = entryName.substring(nextSlashIdx + 1);
               if (entryNameWithoutVersionPrefix.startsWith("META-INF/")) {
                  entryVersion = 8;
                  entryNameWithoutVersionPrefix = entryName;
               }
            }
         }
      }

      this.version = entryVersion;
      this.entryNameUnversioned = entryNameWithoutVersionPrefix;
   }

   public Slice getSlice() throws IOException {
      if (this.slice == null) {
         RandomAccessReader randomAccessReader = this.parentLogicalZipFile.slice.randomAccessReader();
         if (randomAccessReader.readInt(this.locHeaderPos) != 67324752) {
            throw new IOException("Zip entry has bad LOC header: " + this.entryName);
         }

         long dataStartPos = this.locHeaderPos
            + 30L
            + (long)randomAccessReader.readShort(this.locHeaderPos + 26L)
            + (long)randomAccessReader.readShort(this.locHeaderPos + 28L);
         if (dataStartPos > this.parentLogicalZipFile.slice.sliceLength) {
            throw new IOException("Unexpected EOF when trying to read zip entry data: " + this.entryName);
         }

         this.slice = this.parentLogicalZipFile.slice.slice(dataStartPos, this.compressedSize, this.isDeflated, this.uncompressedSize);
      }

      return this.slice;
   }

   public String getPath() {
      return this.parentLogicalZipFile.getPath() + "!/" + this.entryName;
   }

   public long getLastModifiedTimeMillis() {
      if (this.lastModifiedTimeMillis == 0L && (this.lastModifiedDateMSDOS != 0 || this.lastModifiedTimeMSDOS != 0)) {
         int lastModifiedSecond = (this.lastModifiedTimeMSDOS & 31) * 2;
         int lastModifiedMinute = this.lastModifiedTimeMSDOS >> 5 & 63;
         int lastModifiedHour = this.lastModifiedTimeMSDOS >> 11;
         int lastModifiedDay = this.lastModifiedDateMSDOS & 31;
         int lastModifiedMonth = (this.lastModifiedDateMSDOS >> 5 & 7) - 1;
         int lastModifiedYear = (this.lastModifiedDateMSDOS >> 9) + 1980;
         Calendar lastModifiedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
         lastModifiedCalendar.set(lastModifiedYear, lastModifiedMonth, lastModifiedDay, lastModifiedHour, lastModifiedMinute, lastModifiedSecond);
         lastModifiedCalendar.set(14, 0);
         this.lastModifiedTimeMillis = lastModifiedCalendar.getTimeInMillis();
      }

      return this.lastModifiedTimeMillis;
   }

   public int compareTo(FastZipEntry o) {
      int diff0 = o.version - this.version;
      if (diff0 != 0) {
         return diff0;
      } else {
         int diff1 = this.entryNameUnversioned.compareTo(o.entryNameUnversioned);
         if (diff1 != 0) {
            return diff1;
         } else {
            int diff2 = this.entryName.compareTo(o.entryName);
            if (diff2 != 0) {
               return diff2;
            } else {
               long diff3 = this.locHeaderPos - o.locHeaderPos;
               return diff3 < 0L ? -1 : (diff3 > 0L ? 1 : 0);
            }
         }
      }
   }

   @Override
   public int hashCode() {
      return this.parentLogicalZipFile.hashCode() ^ this.version ^ this.entryName.hashCode() ^ (int)this.locHeaderPos;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof FastZipEntry)) {
         return false;
      } else {
         FastZipEntry other = (FastZipEntry)obj;
         return this.parentLogicalZipFile.equals(other.parentLogicalZipFile) && this.compareTo(other) == 0;
      }
   }

   @Override
   public String toString() {
      return "jar:file:" + this.getPath();
   }
}
