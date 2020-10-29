package argos.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.codec.binary.Base64
import java.io.IOException
import java.util.regex.Pattern

@Suppress("UNUSED_PARAMETER")
fun <T> String.decode(cls: Class<T>): T {
    return try {
        val decoded = Base64.decodeBase64(this.toByteArray())
        ObjectMapper().readValue(decoded, object : TypeReference<T>() {})
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

fun String.preprocess():String {
    return this.replace("\n".toRegex(), " ").replace(" +".toRegex(), " ")
}

fun List<String>.anyMatch(expects: List<String>, regex: Boolean): Boolean {
    if (!regex) {
        return this.anyMatch(expects)
    }
    for (expect in expects) {
        //fixme: do it at the right place
        val preparedExpect = expect
                .replace("\\(".toRegex(), "\\\\(")
                .replace("\\)".toRegex(), "\\\\)")
                .replace("\\[".toRegex(), "\\\\[")
                .replace("\\]".toRegex(), "\\\\]")
                .replace("\\{".toRegex(), "\\\\{")
                .replace("\\}".toRegex(), "\\\\}")
        val expectPattern = Pattern.compile(preparedExpect)
        for (actual in this) {
            val matcher = expectPattern.matcher(actual)
            if (matcher.find()) {
                return true
            }
        }
    }
    return false
}

private fun <T> List<T>.anyMatch(list: List<T>): Boolean {
    for (instance1 in this) {
        for (instance2 in list) {
            if (instance1 == instance2) {
                return true
            }
        }
    }
    return false
}