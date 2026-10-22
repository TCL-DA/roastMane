package nonapi.io.github.classgraph.fastzipfilereader;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import nonapi.io.github.classgraph.fileslice.ArraySlice;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessReader;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.StringUtils;
import nonapi.io.github.classgraph.utils.VersionFinder;

public class LogicalZipFile extends ZipFileSlice {
   public List<FastZipEntry> entries;
   private boolean isMultiReleaseJar;
   Set<String> classpathRoots = Collections.newSetFromMap(new ConcurrentHashMap<>());
   public String classPathManifestEntryValue;
   public String bundleClassPathManifestEntryValue;
   public String addExportsManifestEntryValue;
   public String addOpensManifestEntryValue;
   public String automaticModuleNameManifestEntryValue;
   public boolean isJREJar;
   static final String META_INF_PATH_PREFIX = "META-INF/";
   private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
   public static final String MULTI_RELEASE_PATH_PREFIX = "META-INF/versions/";
   private static final byte[] IMPLEMENTATION_TITLE_KEY = manifestKeyToBytes("Implementation-Title");
   private static final byte[] SPECIFICATION_TITLE_KEY = manifestKeyToBytes("Specification-Title");
   private static final byte[] CLASS_PATH_KEY = manifestKeyToBytes("Class-Path");
   private static final byte[] BUNDLE_CLASSPATH_KEY = manifestKeyToBytes("Bundle-ClassPath");
   private static final byte[] SPRING_BOOT_CLASSES_KEY = manifestKeyToBytes("Spring-Boot-Classes");
   private static final byte[] SPRING_BOOT_LIB_KEY = manifestKeyToBytes("Spring-Boot-Lib");
   private static final byte[] MULTI_RELEASE_KEY = manifestKeyToBytes("Multi-Release");
   private static final byte[] ADD_EXPORTS_KEY = manifestKeyToBytes("Add-Exports");
   private static final byte[] ADD_OPENS_KEY = manifestKeyToBytes("Add-Opens");
   private static final byte[] AUTOMATIC_MODULE_NAME_KEY = manifestKeyToBytes("Automatic-Module-Name");
   private static byte[] toLowerCase = new byte[256];

   LogicalZipFile(ZipFileSlice zipFileSlice, NestedJarHandler nestedJarHandler, LogNode log) throws IOException, InterruptedException {
      super(zipFileSlice);
      this.readCentralDirectory(nestedJarHandler, log);
   }

   private static Entry<String, Integer> getManifestValue(byte[] manifest, int startIdx) {
      int curr = startIdx;
      int len = manifest.length;

      while (curr < len && manifest[curr] == 32) {
         curr++;
      }

      int firstNonSpaceIdx = curr;

      boolean isMultiLine;
      for (isMultiLine = false; curr < len && !isMultiLine; curr++) {
         byte b = manifest[curr];
         if (b == 13 && curr < len - 1 && manifest[curr + 1] == 10) {
            if (curr < len - 2 && manifest[curr + 2] == 32) {
               isMultiLine = true;
            }
            break;
         }

         if (b == 13 || b == 10) {
            if (curr < len - 1 && manifest[curr + 1] == 32) {
               isMultiLine = true;
            }
            break;
         }
      }

      String val;
      if (!isMultiLine) {
         val = new String(manifest, firstNonSpaceIdx, curr - firstNonSpaceIdx, StandardCharsets.UTF_8);
      } else {
         ByteArrayOutputStream buf = new ByteArrayOutputStream();

         for (curr = firstNonSpaceIdx; curr < len; curr++) {
            byte bx = manifest[curr];
            boolean isLineEnd;
            if (bx == 13 && curr < len - 1 && manifest[curr + 1] == 10) {
               curr += 2;
               isLineEnd = true;
            } else if (bx != 13 && bx != 10) {
               buf.write(bx);
               isLineEnd = false;
            } else {
               curr++;
               isLineEnd = true;
            }

            if (isLineEnd && curr < len && manifest[curr] != 32) {
               break;
            }
         }

         try {
            val = buf.toString("UTF-8");
         } catch (UnsupportedEncodingException var10) {
            throw new RuntimeException("UTF-8 encoding is not supported in your JRE", var10);
         }
      }

      return new SimpleEntry<>(val.endsWith(" ") ? val.trim() : val, curr);
   }

   private static byte[] manifestKeyToBytes(String key) {
      byte[] bytes = new byte[key.length()];

      for (int i = 0; i < key.length(); i++) {
         bytes[i] = (byte)Character.toLowerCase(key.charAt(i));
      }

      return bytes;
   }

   private static boolean keyMatchesAtPosition(byte[] manifest, byte[] key, int pos) {
      if (pos + key.length + 1 <= manifest.length && manifest[pos + key.length] == 58) {
         for (int i = 0; i < key.length; i++) {
            if (toLowerCase[manifest[i + pos]] != key[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void parseManifest(FastZipEntry manifestZipEntry, LogNode log) throws IOException, InterruptedException {
      byte[] manifest = manifestZipEntry.getSlice().load();
      int i = 0;

      while (i < manifest.length) {
         boolean skip = false;
         if (manifest[i] == 10 || manifest[i] == 13) {
            skip = true;
         } else if (keyMatchesAtPosition(manifest, IMPLEMENTATION_TITLE_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + IMPLEMENTATION_TITLE_KEY.length + 1);
            if (manifestValueAndEndIdx.getKey().equalsIgnoreCase("Java Runtime Environment")) {
               this.isJREJar = true;
            }

            i = manifestValueAndEndIdx.getValue();
         } else if (keyMatchesAtPosition(manifest, SPECIFICATION_TITLE_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + SPECIFICATION_TITLE_KEY.length + 1);
            if (manifestValueAndEndIdx.getKey().equalsIgnoreCase("Java Platform API Specification")) {
               this.isJREJar = true;
            }

            i = manifestValueAndEndIdx.getValue();
         } else if (keyMatchesAtPosition(manifest, CLASS_PATH_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + CLASS_PATH_KEY.length + 1);
            this.classPathManifestEntryValue = manifestValueAndEndIdx.getKey();
            if (log != null) {
               log.log("Found Class-Path entry in manifest file: " + this.classPathManifestEntryValue);
            }

            i = manifestValueAndEndIdx.getValue();
         } else if (keyMatchesAtPosition(manifest, BUNDLE_CLASSPATH_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + BUNDLE_CLASSPATH_KEY.length + 1);
            this.bundleClassPathManifestEntryValue = manifestValueAndEndIdx.getKey();
            if (log != null) {
               log.log("Found Bundle-ClassPath entry in manifest file: " + this.bundleClassPathManifestEntryValue);
            }

            i = manifestValueAndEndIdx.getValue();
         } else if (keyMatchesAtPosition(manifest, SPRING_BOOT_CLASSES_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + SPRING_BOOT_CLASSES_KEY.length + 1);
            String springBootClassesFieldVal = manifestValueAndEndIdx.getKey();
            if (!springBootClassesFieldVal.equals("BOOT-INF/classes")
               && !springBootClassesFieldVal.equals("BOOT-INF/classes/")
               && !springBootClassesFieldVal.equals("WEB-INF/classes")
               && !springBootClassesFieldVal.equals("WEB-INF/classes/")) {
               throw new IOException(
                  "Spring boot classes are at \""
                     + springBootClassesFieldVal
                     + "\" rather than the standard location \"BOOT-INF/classes/\" or \"WEB-INF/classes/\" -- please report this at https://github.com/classgraph/classgraph/issues"
               );
            }

            i = manifestValueAndEndIdx.getValue();
         } else if (keyMatchesAtPosition(manifest, SPRING_BOOT_LIB_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + SPRING_BOOT_LIB_KEY.length + 1);
            String springBootLibFieldVal = manifestValueAndEndIdx.getKey();
            if (!springBootLibFieldVal.equals("BOOT-INF/lib")
               && !springBootLibFieldVal.equals("BOOT-INF/lib/")
               && !springBootLibFieldVal.equals("WEB-INF/lib")
               && !springBootLibFieldVal.equals("WEB-INF/lib/")) {
               throw new IOException(
                  "Spring boot lib jars are at \""
                     + springBootLibFieldVal
                     + "\" rather than the standard location \"BOOT-INF/lib/\" or \"WEB-INF/lib/\" -- please report this at https://github.com/classgraph/classgraph/issues"
               );
            }

            i = manifestValueAndEndIdx.getValue();
         } else if (keyMatchesAtPosition(manifest, MULTI_RELEASE_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + MULTI_RELEASE_KEY.length + 1);
            if (manifestValueAndEndIdx.getKey().equalsIgnoreCase("true")) {
               this.isMultiReleaseJar = true;
            }

            i = manifestValueAndEndIdx.getValue();
         } else if (keyMatchesAtPosition(manifest, ADD_EXPORTS_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + ADD_EXPORTS_KEY.length + 1);
            this.addExportsManifestEntryValue = manifestValueAndEndIdx.getKey();
            if (log != null) {
               log.log("Found Add-Exports entry in manifest file: " + this.addExportsManifestEntryValue);
            }

            i = manifestValueAndEndIdx.getValue();
         } else if (keyMatchesAtPosition(manifest, ADD_OPENS_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + ADD_OPENS_KEY.length + 1);
            this.addExportsManifestEntryValue = manifestValueAndEndIdx.getKey();
            if (log != null) {
               log.log("Found Add-Opens entry in manifest file: " + this.addOpensManifestEntryValue);
            }

            i = manifestValueAndEndIdx.getValue();
         } else if (keyMatchesAtPosition(manifest, AUTOMATIC_MODULE_NAME_KEY, i)) {
            Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + AUTOMATIC_MODULE_NAME_KEY.length + 1);
            this.automaticModuleNameManifestEntryValue = manifestValueAndEndIdx.getKey();
            if (log != null) {
               log.log("Found Automatic-Module-Name entry in manifest file: " + this.automaticModuleNameManifestEntryValue);
            }

            i = manifestValueAndEndIdx.getValue();
         } else {
            skip = true;
         }

         if (skip) {
            while (true) {
               if (i < manifest.length - 2) {
                  if (manifest[i] == 13 && manifest[i + 1] == 10 && manifest[i + 2] != 32) {
                     i += 2;
                  } else {
                     if (manifest[i] != 13 && manifest[i] != 10 || manifest[i + 1] == 32) {
                        i++;
                        continue;
                     }

                     i++;
                  }
               }

               if (i >= manifest.length - 2) {
                  return;
               }
               break;
            }
         }
      }
   }

   private void readCentralDirectory(NestedJarHandler nestedJarHandler, LogNode log) throws IOException, InterruptedException {
      if (this.slice.sliceLength < 22L) {
         throw new IOException("Zipfile too short to have a central directory");
      } else {
         RandomAccessReader reader = this.slice.randomAccessReader();
         long eocdPos = -1L;
         long i = this.slice.sliceLength - 22L;

         for (long iMin = this.slice.sliceLength - 22L - 32L; i >= iMin && i >= 0L; i--) {
            if (reader.readUnsignedInt(i) == 101010256L) {
               eocdPos = i;
               break;
            }
         }

         if (eocdPos < 0L && this.slice.sliceLength > 54L) {
            int bytesToRead = (int)Math.min(this.slice.sliceLength, 65536L);
            byte[] eocdBytes = new byte[bytesToRead];
            long readStartOff = this.slice.sliceLength - (long)bytesToRead;
            if (reader.read(readStartOff, eocdBytes, 0, bytesToRead) < bytesToRead) {
               throw new IOException("Zipfile is truncated");
            }

            try (ArraySlice arraySlice = new ArraySlice(eocdBytes, false, 0L, nestedJarHandler)) {
               RandomAccessReader eocdReader = arraySlice.randomAccessReader();

               for (long ix = (long)eocdBytes.length - 22L; ix >= 0L; ix--) {
                  if (eocdReader.readUnsignedInt(ix) == 101010256L) {
                     eocdPos = ix + readStartOff;
                     break;
                  }
               }
            }
         }

         if (eocdPos < 0L) {
            throw new IOException("Jarfile central directory signature not found: " + this.getPath());
         } else {
            i = (long)reader.readUnsignedShort(eocdPos + 8L);
            if (reader.readUnsignedShort(eocdPos + 4L) <= 0
               && reader.readUnsignedShort(eocdPos + 6L) <= 0
               && i == (long)reader.readUnsignedShort(eocdPos + 10L)) {
               long cenSize = reader.readUnsignedInt(eocdPos + 12L);
               if (cenSize > eocdPos) {
                  throw new IOException("Central directory size out of range: " + cenSize + " vs. " + eocdPos + ": " + this.getPath());
               } else {
                  long cenOff = reader.readUnsignedInt(eocdPos + 16L);
                  long cenPos = eocdPos - cenSize;
                  long zip64cdLocIdx = eocdPos - 20L;
                  if (zip64cdLocIdx >= 0L && reader.readUnsignedInt(zip64cdLocIdx) == 117853008L) {
                     if (reader.readUnsignedInt(zip64cdLocIdx + 4L) > 0L || reader.readUnsignedInt(zip64cdLocIdx + 16L) > 1L) {
                        throw new IOException("Multi-disk jarfiles not supported: " + this.getPath());
                     }

                     long eocdPos64 = reader.readLong(zip64cdLocIdx + 8L);
                     if (reader.readUnsignedInt(eocdPos64) != 101075792L) {
                        throw new IOException(
                           "Zip64 central directory at location " + eocdPos64 + " does not have Zip64 central directory header: " + this.getPath()
                        );
                     }

                     long numEnt64 = reader.readLong(eocdPos64 + 24L);
                     if (reader.readUnsignedInt(eocdPos64 + 16L) > 0L
                        || reader.readUnsignedInt(eocdPos64 + 20L) > 0L
                        || numEnt64 != reader.readLong(eocdPos64 + 32L)) {
                        throw new IOException("Multi-disk jarfiles not supported: " + this.getPath());
                     }

                     if (i == 65535L) {
                        i = numEnt64;
                     } else if (i != numEnt64) {
                        i = -1L;
                     }

                     long cenSize64 = reader.readLong(eocdPos64 + 40L);
                     if (cenSize == 4294967295L) {
                        cenSize = cenSize64;
                     } else if (cenSize != cenSize64) {
                        throw new IOException("Mismatch in central directory size: " + cenSize + " vs. " + cenSize64 + ": " + this.getPath());
                     }

                     cenPos = eocdPos64 - cenSize;
                     long cenOff64 = reader.readLong(eocdPos64 + 48L);
                     if (cenOff == 4294967295L) {
                        cenOff = cenOff64;
                     } else if (cenOff != cenOff64) {
                        throw new IOException("Mismatch in central directory offset: " + cenOff + " vs. " + cenOff64 + ": " + this.getPath());
                     }
                  }

                  long locPos = cenPos - cenOff;
                  if (locPos < 0L) {
                     throw new IOException("Local file header offset out of range: " + locPos + ": " + this.getPath());
                  } else {
                     RandomAccessReader cenReader;
                     if (cenSize > 2147483639L) {
                        cenReader = this.slice.slice(cenPos, cenSize, false, 0L).randomAccessReader();
                     } else {
                        byte[] entryBytes = new byte[(int)cenSize];
                        if ((long)reader.read(cenPos, entryBytes, 0, (int)cenSize) < cenSize) {
                           throw new IOException("Zipfile is truncated");
                        }

                        cenReader = new ArraySlice(entryBytes, false, 0L, nestedJarHandler).randomAccessReader();
                     }

                     if (i == -1L) {
                        i = 0L;

                        for (long entOff = 0L; entOff + 46L <= cenSize; i++) {
                           long sig = cenReader.readUnsignedInt(entOff);
                           if (sig != 33639248L) {
                              throw new IOException("Invalid central directory signature: 0x" + Integer.toString((int)sig, 16) + ": " + this.getPath());
                           }

                           int filenameLen = cenReader.readUnsignedShort(entOff + 28L);
                           int extraFieldLen = cenReader.readUnsignedShort(entOff + 30L);
                           int commentLen = cenReader.readUnsignedShort(entOff + 32L);
                           entOff += (long)(46 + filenameLen + extraFieldLen + commentLen);
                        }
                     }

                     if (i > 2147483639L) {
                        throw new IOException("Too many zipfile entries: " + i);
                     } else if (i > cenSize / 46L) {
                        throw new IOException("Too many zipfile entries: " + i + " (expected a max of " + cenSize / 46L + " based on central directory size)");
                     } else {
                        this.entries = new ArrayList<>((int)i);
                        FastZipEntry manifestZipEntry = null;

                        try {
                           int entSize = 0;
                           long entOff = 0L;

                           while (entOff + 46L <= cenSize) {
                              long sig = cenReader.readUnsignedInt(entOff);
                              if (sig != 33639248L) {
                                 throw new IOException("Invalid central directory signature: 0x" + Integer.toString((int)sig, 16) + ": " + this.getPath());
                              }

                              int filenameLen = cenReader.readUnsignedShort(entOff + 28L);
                              int extraFieldLen = cenReader.readUnsignedShort(entOff + 30L);
                              int commentLen = cenReader.readUnsignedShort(entOff + 32L);
                              entSize = 46 + filenameLen + extraFieldLen + commentLen;
                              long filenameStartOff = entOff + 46L;
                              long filenameEndOff = filenameStartOff + (long)filenameLen;
                              if (filenameEndOff > cenSize) {
                                 if (log != null) {
                                    log.log("Filename extends past end of entry -- skipping entry at offset " + entOff);
                                 }
                                 break;
                              }

                              String entryName = cenReader.readString(filenameStartOff, filenameLen);
                              String entryNameSanitized = FileUtils.sanitizeEntryPath(entryName, true, false);
                              if (!entryNameSanitized.isEmpty() && !entryName.endsWith("/")) {
                                 int flags = cenReader.readUnsignedShort(entOff + 8L);
                                 if ((flags & 1) != 0) {
                                    if (log != null) {
                                       log.log("Skipping encrypted zip entry: " + entryNameSanitized);
                                    }
                                 } else {
                                    int compressionMethod = cenReader.readUnsignedShort(entOff + 10L);
                                    if (compressionMethod == 0 || compressionMethod == 8) {
                                       boolean isDeflated = compressionMethod == 8;
                                       long compressedSize = cenReader.readUnsignedInt(entOff + 20L);
                                       long uncompressedSize = cenReader.readUnsignedInt(entOff + 24L);
                                       int fileAttributes = cenReader.readUnsignedShort(entOff + 40L);
                                       long pos = cenReader.readUnsignedInt(entOff + 42L);
                                       long lastModifiedMillis = 0L;
                                       if (extraFieldLen > 0) {
                                          int extraFieldOff = 0;

                                          while (extraFieldOff + 4 < extraFieldLen) {
                                             long tagOff = filenameEndOff + (long)extraFieldOff;
                                             int tag = cenReader.readUnsignedShort(tagOff);
                                             int size = cenReader.readUnsignedShort(tagOff + 2L);
                                             if (extraFieldOff + 4 + size > extraFieldLen) {
                                                if (log != null) {
                                                   log.log("Skipping zip entry with invalid extra field size: " + entryNameSanitized);
                                                }
                                                break;
                                             }

                                             if (tag == 1 && size >= 20) {
                                                long uncompressedSize64 = cenReader.readLong(tagOff + 4L + 0L);
                                                if (uncompressedSize == 4294967295L) {
                                                   uncompressedSize = uncompressedSize64;
                                                } else if (uncompressedSize != uncompressedSize64) {
                                                   throw new IOException(
                                                      "Mismatch in uncompressed size: "
                                                         + uncompressedSize
                                                         + " vs. "
                                                         + uncompressedSize64
                                                         + ": "
                                                         + entryNameSanitized
                                                   );
                                                }

                                                long compressedSize64 = cenReader.readLong(tagOff + 4L + 8L);
                                                if (compressedSize == 4294967295L) {
                                                   compressedSize = compressedSize64;
                                                } else if (compressedSize != compressedSize64) {
                                                   throw new IOException(
                                                      "Mismatch in compressed size: " + compressedSize + " vs. " + compressedSize64 + ": " + entryNameSanitized
                                                   );
                                                }

                                                if (size >= 28) {
                                                   long pos64 = cenReader.readLong(tagOff + 4L + 16L);
                                                   if (pos == 4294967295L) {
                                                      pos = pos64;
                                                   } else if (pos != pos64) {
                                                      throw new IOException("Mismatch in entry pos: " + pos + " vs. " + pos64 + ": " + entryNameSanitized);
                                                   }
                                                }
                                                break;
                                             }

                                             if (tag == 21589 && size >= 5) {
                                                int bits = cenReader.readUnsignedByte(tagOff + 4L + 0L);
                                                if ((bits & 1) == 1 && size >= 13) {
                                                   lastModifiedMillis = cenReader.readLong(tagOff + 4L + 1L) * 1000L;
                                                }
                                             } else if (tag == 22613 && size >= 20) {
                                                lastModifiedMillis = cenReader.readLong(tagOff + 4L + 8L) * 1000L;
                                             } else if (tag != 30805 && tag == 28789) {
                                                int version = cenReader.readUnsignedByte(tagOff + 4L + 0L);
                                                if (version != 1) {
                                                   throw new IOException(
                                                      "Unknown Unicode entry name format " + version + " in extra field: " + entryNameSanitized
                                                   );
                                                }

                                                if (size > 9) {
                                                   try {
                                                      entryNameSanitized = cenReader.readString(tagOff + 9L, size - 9);
                                                   } catch (IllegalArgumentException var64) {
                                                      throw new IOException("Malformed extended Unicode entry name for entry: " + entryNameSanitized);
                                                   }
                                                }
                                             }

                                             extraFieldOff += 4 + size;
                                          }
                                       }

                                       int lastModifiedTimeMSDOS = 0;
                                       int lastModifiedDateMSDOS = 0;
                                       if (lastModifiedMillis == 0L) {
                                          lastModifiedTimeMSDOS = cenReader.readUnsignedShort(entOff + 12L);
                                          lastModifiedDateMSDOS = cenReader.readUnsignedShort(entOff + 14L);
                                       }

                                       if (compressedSize < 0L) {
                                          if (log != null) {
                                             log.log("Skipping zip entry with invalid compressed size (" + compressedSize + "): " + entryNameSanitized);
                                          }
                                       } else if (uncompressedSize < 0L) {
                                          if (log != null) {
                                             log.log("Skipping zip entry with invalid uncompressed size (" + uncompressedSize + "): " + entryNameSanitized);
                                          }
                                       } else if (pos < 0L) {
                                          if (log != null) {
                                             log.log("Skipping zip entry with invalid pos (" + pos + "): " + entryNameSanitized);
                                          }
                                       } else {
                                          long locHeaderPos = locPos + pos;
                                          if (locHeaderPos < 0L) {
                                             if (log != null) {
                                                log.log("Skipping zip entry with invalid loc header position (" + locHeaderPos + "): " + entryNameSanitized);
                                             }
                                          } else if (locHeaderPos + 4L >= this.slice.sliceLength) {
                                             if (log != null) {
                                                log.log("Unexpected EOF when trying to read LOC header: " + entryNameSanitized);
                                             }
                                          } else {
                                             FastZipEntry entry = new FastZipEntry(
                                                this,
                                                locHeaderPos,
                                                entryNameSanitized,
                                                isDeflated,
                                                compressedSize,
                                                uncompressedSize,
                                                lastModifiedMillis,
                                                lastModifiedTimeMSDOS,
                                                lastModifiedDateMSDOS,
                                                fileAttributes
                                             );
                                             this.entries.add(entry);
                                             if (entry.entryName.equals("META-INF/MANIFEST.MF")) {
                                                manifestZipEntry = entry;
                                             }
                                          }
                                       }
                                    } else if (log != null) {
                                       log.log("Skipping zip entry with invalid compression method " + compressionMethod + ": " + entryNameSanitized);
                                    }
                                 }
                              }

                              entOff += (long)entSize;
                           }
                        } catch (IndexOutOfBoundsException | EOFException var65) {
                           if (log != null) {
                              log.log(
                                 "Reached premature EOF"
                                    + (this.entries.isEmpty() ? "" : " after reading zip entry " + this.entries.get(this.entries.size() - 1))
                              );
                           }
                        }

                        if (manifestZipEntry != null) {
                           this.parseManifest(manifestZipEntry, log);
                        }

                        if (this.isMultiReleaseJar) {
                           if (VersionFinder.JAVA_MAJOR_VERSION < 9) {
                              if (log != null) {
                                 log.log(
                                    "This is a multi-release jar, but JRE version " + VersionFinder.JAVA_MAJOR_VERSION + " does not support multi-release jars"
                                 );
                              }
                           } else {
                              if (log != null) {
                                 Set<Integer> versionsFound = new HashSet<>();

                                 for (FastZipEntry entry : this.entries) {
                                    if (entry.version > 8) {
                                       versionsFound.add(entry.version);
                                    }
                                 }

                                 List<Integer> versionsFoundSorted = new ArrayList<>(versionsFound);
                                 CollectionUtils.sortIfNotEmpty(versionsFoundSorted);
                                 log.log("This is a multi-release jar, with versions: " + StringUtils.join(", ", versionsFoundSorted));
                              }

                              CollectionUtils.sortIfNotEmpty(this.entries);
                              List<FastZipEntry> unversionedZipEntriesMasked = new ArrayList<>(this.entries.size());
                              Map<String, String> unversionedPathToVersionedPath = new HashMap<>();

                              for (FastZipEntry versionedZipEntry : this.entries) {
                                 if (!unversionedPathToVersionedPath.containsKey(versionedZipEntry.entryNameUnversioned)) {
                                    unversionedPathToVersionedPath.put(versionedZipEntry.entryNameUnversioned, versionedZipEntry.entryName);
                                    unversionedZipEntriesMasked.add(versionedZipEntry);
                                 } else if (log != null) {
                                    log.log(
                                       unversionedPathToVersionedPath.get(versionedZipEntry.entryNameUnversioned) + " masks " + versionedZipEntry.entryName
                                    );
                                 }
                              }

                              this.entries = unversionedZipEntriesMasked;
                           }
                        }
                     }
                  }
               }
            } else {
               throw new IOException("Multi-disk jarfiles not supported: " + this.getPath());
            }
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      return super.equals(o);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public String toString() {
      return this.getPath();
   }

   static {
      for (int i = 32; i < 127; i++) {
         toLowerCase[i] = (byte)Character.toLowerCase((char)i);
      }
   }
}
