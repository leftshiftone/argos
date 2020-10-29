package argos.core.conversation.listener

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import gaia.sdk.api.queue.QueuePayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

abstract class AbstractMessageListener {

    private val objectMapper = ObjectMapper()
    private val logger: Logger = LoggerFactory.getLogger(AbstractMessageListener::class.java)

    abstract fun handle(message: QueuePayload<ByteArray>)

    open fun deserialize(message: ByteArray): Map<String, Any> {
        return try {
            val mapType = object : TypeReference<Map<String, Any>>() {}
            objectMapper.readValue(message, mapType)
        } catch (e: IOException) {
            logger.error("Cannot deserialize message")
            HashMap()
        }
    }

}