package argos.core.conversation

import argos.api.anyMatch
import argos.api.decode
import argos.core.conversation.handler.StringSupplier
import canon.api.IRenderable
import canon.model.Button
import canon.model.Container
import canon.model.Text
import com.jayway.jsonpath.JsonPath
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.util.function.Consumer
import java.util.function.Supplier

class ConnectionEvaluator(val list: List<AbstractParticipant>) {

    private val logger = LoggerFactory.getLogger(ConnectionEvaluator::class.java)

    fun evaluate(consumer: Consumer<AbstractGatter>,
                 interactionSupplier: Supplier<AbstractGatter>,
                 contextSupplier: StringSupplier,
                 loggingSupplier: StringSupplier,
                 notificationSupplier: StringSupplier,
                 errorCollector: MutableList<String>): Boolean {

        for (participant in list) {
            if (participant is User) {
                contextSupplier.clear() // clear context deque in order to wait for new context after user input

                val expectedGatter = participant.gatter
                consumer.accept(expectedGatter)

                logger.info("user:\n${expectedGatter}\n")
            }
            if (participant is Gaia) {
                val gatter = participant.gatter

                if (gatter is Context) {
                    val contextResult = evaluateGatter("context", gatter, contextSupplier)
                    if (contextResult) continue
                    else return false
                }
                if (gatter is Notification) {
                    val notificationResult = evaluateGatter("notification", gatter, notificationSupplier)
                    if (notificationResult) continue
                    else return false
                }
                if (gatter is Log) {
                    val notificationResult = evaluateGatter("log", gatter, loggingSupplier)
                    if (notificationResult) continue
                    else return false
                }

                decodeExpectedValue(gatter)
                val expects = toList(gatter).map { replaceSpecialCharacters(it) }.sorted()

                val actualGatter = interactionSupplier.get()
                val filteredGatter = filter(actualGatter)
                decodeExpectedValue(filteredGatter)
                val actuals = toList(filteredGatter).map { replaceSpecialCharacters(it) }.sorted()

                if (!actuals.anyMatch(expects, gatter.regex)) {
                    val builder = StringBuilder()
                    builder.append("gaia (SOLL):").append("\n")
                    builder.append(toList(gatter)).append("\n")
                    builder.append("").append("\n")
                    builder.append("gaia (IST):").append("\n")
                    builder.append(toList(filteredGatter)).append("\n")
                    builder.append("").append("\n")

                    errorCollector.add(builder.toString())
                    return false
                }

                logger.info("gaia: \n${filteredGatter}\n")
            }
        }

        return true
    }

    private fun evaluateGatter(type: String, gatter: AbstractGatter, contextSupplier: StringSupplier): Boolean {
        val message = contextSupplier.get()
        val jsonContext = JsonPath.parse(message)

        return gatter.renderables
                .map { it as AssertJson }
                .map {
                    val pathResult = jsonContext.read<String>(it.path)
                    logger.info("Expected '${it.expectation}' and got '$pathResult' for json path '${it.path}' and ${type} $message")
                    val pathAssertionResult = pathResult == it.expectation
                    pathAssertionResult
                }
                .all { it }
    }

    private fun replaceSpecialCharacters(it: String): String {
        return StringUtils.deleteWhitespace(StringEscapeUtils.escapeHtml4(it))
    }

    private fun toList(gatter: AbstractGatter): List<String> {
        if (gatter is Or)
            return gatter.renderables.map { it.toString() }
        return listOf(gatter.renderables.joinToString("\n") { it.toString() })
    }

    private fun filter(gatter: AbstractGatter): AbstractGatter {
        val actualToCompare = ArrayList<IRenderable>()
        if (gatter.renderables.size == 1 && gatter.renderables.get(0) is Container) {
            val container = gatter.renderables[0] as Container
            actualToCompare.addAll(container.renderables!!)
        } else {
            return gatter
        }

        val collect = actualToCompare.filter { ren -> !(ren is Text && ren.value == "\n") }

        return object : AbstractGatter(collect, false) {}
    }

    private fun decodeExpectedValue(gatter: AbstractGatter) {
        for (renderable in gatter.renderables) {
            if (renderable is Button) {
                renderable.value = renderable.value?.decode(Map::class.java)?.get("payload") as String
            }
        }
    }

}
