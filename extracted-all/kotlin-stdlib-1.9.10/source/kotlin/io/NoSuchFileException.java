package kotlin.io

import java.io.File

class NoSuchFileException(file: File, other: File?, reason: String?) : FileSystemException(file, other, reason) {
}
