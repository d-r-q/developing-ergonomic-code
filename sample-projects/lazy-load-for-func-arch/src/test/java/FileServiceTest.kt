import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import pro.azhidkov.llffa.FileMeta
import pro.azhidkov.llffa.FileNotCompressible
import pro.azhidkov.llffa.FileService
import pro.azhidkov.llffa.compressFile
import java.io.ByteArrayInputStream

internal class FileServiceTest {

    @Test
    fun `Compressor не должнен сжимать файлы, сжатие которых запрещено`(fileService: FileService) {
        // Дан несжимаемый файл
        val incompressibleFileMeta = FileMeta(1 /* фикстура*/)
        val fakeFile = lazy { ByteArrayInputStream(byteArrayOf()) }

        // При попытке его сжать
        // Должно быть выброшено исключение FileNotCompressible
        shouldThrow<FileNotCompressible> {
            compressFile(incompressibleFileMeta, fakeFile)
        }
    }
}