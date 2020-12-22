package argos.core.conversation.listener

import gaia.sdk.api.queue.QueuePayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.BlockingDeque

class NotificationMessageListener(val deque: BlockingDeque<String>) : AbstractMessageListener() {
    private val logger: Logger = LoggerFactory.getLogger(NotificationMessageListener::class.java)

    override fun handle(message: QueuePayload<ByteArray>) {
        try {
            this.deque.putLast(String(message.content))
        } catch (e: Throwable) {
            logger.error("Error putting payload to queue", e)
            throw RuntimeException(e)
        }
    }
}