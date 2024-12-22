package pw.binom.init

import pw.binom.DEFAULT_BUFFER_SIZE
import pw.binom.io.ByteBuffer
import pw.binom.io.file.*
import pw.binom.io.use

fun File.copyInto(dest: File) {
    ByteBuffer(DEFAULT_BUFFER_SIZE).use { buffer ->
        openRead().use { input ->
            dest.parent.mkdirs()
            dest.openWrite().use { output ->
                while (true) {
                    buffer.clear()
                    val l = input.read(buffer)
                    if (l.isNotAvailable) {
                        break
                    }
                    buffer.flip()
                    output.write(buffer)
                }
            }
        }
    }
}
