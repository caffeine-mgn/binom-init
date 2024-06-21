package pw.binom.init

import pw.binom.*
import pw.binom.io.ByteBuffer
import pw.binom.io.Input
import pw.binom.io.file.File
import pw.binom.io.file.openRead
import pw.binom.io.file.openWrite
import pw.binom.io.readByteArray
import pw.binom.io.use

object GradleResources {

    data class Resource(val name: String, val start: Long, val size: Long) {
        fun stream(): Input {
            val exe = File(Environment.currentExecutionPath)
            val stream = exe.openRead()
            stream.position = start
            return stream.withLimit(size)
        }
    }

    private val tables by lazy {
        val exe = File(Environment.currentExecutionPath)
        val endPosition = exe.size
        val data = ArrayList<Resource>()
        ByteBuffer(DEFAULT_BUFFER_SIZE).use { buffer ->
            exe.openRead().use { stream ->
                val dataEnd = endPosition - Long.SIZE_BYTES
                stream.position = dataEnd
                val tableStart = stream.readLong(buffer)
                stream.position = tableStart
                val size = stream.readInt(buffer)
                repeat(size) {
                    val len = stream.readInt(buffer)
                    val name = stream.readByteArray(len, buffer).decodeToString()
                    val start = stream.readLong(buffer)
                    val dataSize = stream.readLong(buffer)
                    data += Resource(name = name, start = start, size = dataSize)
                }
            }
        }
        data.trimToSize()
        data
    }

    fun unpack(name: String, into: File) {
        val resource = tables.find { it.name == name }
            ?: throw IllegalStateException("Resource \"$name\" not found")
        resource.stream().use { input ->
            into.openWrite().use { output ->
                input.copyTo(output)
            }
        }
    }

    fun unpackGradleWrapper(into: File) {
        unpack(
            name = "gradle-wrapper.jar",
            into = into,
        )
    }

    fun unpackGradlew(into: File) {
        unpack(
            name = "gradlew",
            into = into,
        )
    }

    fun unpackGradlewBat(into: File) {
        unpack(
            name = "gradlew.bat",
            into = into,
        )
    }

    fun gradleWrapperProperties(version: String) = """distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-${version}-all.zip
networkTimeout=10000
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists 
"""
}
