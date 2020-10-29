package argos.core.conversation.listener

import gaia.sdk.api.queue.QueuePayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.BlockingDeque

class LoggingMessageListener(val deque: BlockingDeque<String>) : AbstractMessageListener() {
    private val logger: Logger = LoggerFactory.getLogger(ContextMessageListener::class.java)

    override fun handle(message: QueuePayload<ByteArray>) {
        try {
            val msg = deserialize(message.content)["message"] as String
            this.deque.putLast(msg)
            logger.info(msg)
        } catch (e: Throwable) {
            logger.error("Error putting payload to queue", e)
            throw RuntimeException(e)
        }
    }
}