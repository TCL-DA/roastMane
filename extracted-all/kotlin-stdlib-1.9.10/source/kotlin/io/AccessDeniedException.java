package kotlin.io

import java.io.File

class AccessDeniedException(file: File, other: File?, reason: String?) : FileSystemException(file, other, reason) {
}
