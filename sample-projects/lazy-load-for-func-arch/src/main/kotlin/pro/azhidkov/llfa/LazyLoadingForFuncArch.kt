package pro.azhidkov.llffa

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI


class FileNotCompressible : Throwable()

class FileService {

    fun compressFileById(fileId: Long) {
        // ввод
        val fileMeta = findFileMeta(fileId)
        val content = lazy { fetchFile(fileId) }

        // бизнес-логика
        val compressed = compressFile(fileMeta, content)

        // вывод
        putFile(fileMeta.relativeUri(), compressed)
    }

    fun putFile(uri: URI, content: InputStream): Unit = TODO("Not yet implemented")

    fun findFileMeta(fileId: Long): FileMeta = TODO()

    private fun fetchFile(fileId: Long): InputStream {
        TODO("Not yet implemented")
    }

}

internal fun compressFile(fileMeta: FileMeta, file: Lazy<InputStream>): InputStream {
    if (fileMeta.isCompressible()) {
        throw FileNotCompressible()
    }
    // Ноухау моего одногрупиника - ахриватор, который всё сжимает в два байта
    return ByteArrayInputStream(byteArrayOf(42, 43))
}

data class FileMeta(val id: Long) {
    fun relativeUri(): URI = TODO("Not yet implemented")
    fun isCompressible(): Boolean = TODO("Not yet implemented")
}
