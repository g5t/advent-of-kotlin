import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()
fun readTestInput(name: String) = Path("src/$name.test").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun ByteArray.startsWith(byteArray: ByteArray): Boolean {
    if (this.size < byteArray.size) return false
    for (pair in this.zip(byteArray)) if (pair.first != pair.second) return false
    return true
}