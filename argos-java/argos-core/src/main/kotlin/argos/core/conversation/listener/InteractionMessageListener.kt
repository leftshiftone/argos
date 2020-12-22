package argos.core.conversation.listener

import argos.core.conversation.AbstractGatter
import argos.core.conversation.And
import argos.core.conversation.Error
import canon.parser.map.CanonMapParser
import canon.parser.xml.CanonXmlParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import gaia.sdk.api.queue.QueuePayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.util.concurrent.BlockingQueue

class InteractionMessageListener(val queue:BlockingQueue<AbstractGatter>) : AbstractMessageListener() {
    private val logger: Logger = LoggerFactory.getLogger(InteractionMessageListener::class.java)

    override fun handle(message:QueuePayload<ByteArray>) {
        try {
            val map = deserialize(message.content)
            @Suppress("UNCHECKED_CAST")
            val elements: List<Map<String, Any>> = map["elements"] as List<Map<String, Any>>

            val renderables = elements.map { CanonMapParser.parse(it) }
            this.queue.put(And(renderables))
        } catch (e: Throwable) {
            logger.error("Error during rendering", e)
            // Explicit logging because mqttv3 will silently swallow exceptions
            this.queue.put(Error(e))
        }
    }
}
