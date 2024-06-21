package pw.binom

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

abstract class ResourcePackerTask : DefaultTask() {
    @get:InputFile
    abstract val gradleWrapperJar: RegularFileProperty

    @get:InputFile
    abstract val inputBinaryFile: RegularFileProperty

    @get:OutputFile
    abstract val outputBinaryFile: RegularFileProperty

    private class Record(val file: Any, val name: String)

    private val records = ArrayList<Record>()

    fun resource(file: File, name: String) {
        records += Record(file = file, name = name)
        inputs.file(file)
    }

    fun resource(file: Property<File>, name: String) {
        records += Record(file = file, name = name)
        inputs.file(file)
    }

    @TaskAction
    fun execute() {
        val dest = outputBinaryFile.get().asFile
        val src = inputBinaryFile.get().asFile
        dest.parentFile?.mkdirs()
        src.copyTo(dest, overwrite = true)
        val fileLen = dest.length()
        var position = fileLen
        FileOutputStream(dest, true).use { out ->
            val sizes = records.map { record ->
                val fff = project.file(record.file)
                fff.inputStream().use { gradle ->
                    gradle.copyTo(out)
//                    out.write(fileLen.toBytes())
                }
                val r = position to fff.length()
                position += fff.length()
                r
            }
            var tableStart = position
            out.write(sizes.size.toBytes())
            sizes.forEachIndexed { index, pair ->
                val resourceName = records[index].name
                out.write(resourceName.length.toBytes())
                out.write(resourceName.encodeToByteArray())
                out.write(pair.first.toBytes())
                out.write(pair.second.toBytes())
            }
            out.write(tableStart.toBytes())
        }
//        gradleWrapperJar.get().asFile.inputStream().use { gradle ->
//            FileOutputStream(dest, true).use { out ->
//                gradle.copyTo(out)
//                out.write(fileLen.toBytes())
//            }
//        }
    }

    fun Int.toBytes(): ByteArray {
        val array = ByteArray(Int.SIZE_BYTES)
        val buffer = ByteBuffer.wrap(array)
        buffer.putInt(this)
        buffer.clear()
        return array
    }

    fun Long.toBytes(): ByteArray {
        val array = ByteArray(Long.SIZE_BYTES)
        val buffer = ByteBuffer.wrap(array)
        buffer.putLong(this)
        buffer.clear()
        return array
    }
}