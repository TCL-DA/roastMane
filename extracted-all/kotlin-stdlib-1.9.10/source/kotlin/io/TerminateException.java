package kotlin.io

import java.io.File

private class TerminateException(file: File) : FileSystemException(file, null, null, 6) {
}
