package kotlin.io

import java.io.File

class FileAlreadyExistsException(file: File, other: File?, reason: String?) : FileSystemException(file, other, reason) {
}
