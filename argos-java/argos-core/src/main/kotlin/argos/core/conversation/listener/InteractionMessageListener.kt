package argos.core.conversation.listener

import argos.core.conversation.AbstractGatter
import gaia.sdk.api.queue.QueuePayload
import java.util.concurrent.BlockingQueue

class InteractionMessageListener(queue:BlockingQueue<AbstractGatter>) : AbstractMessageListener() {

    override fun handle(message:QueuePayload<ByteArray>) {
/*        try {
            val renderables = parser.toRenderables(JsonMapToNodeParser.parse(deserialize(message.content)))
            this.queue.put(new AND(renderables))
        } catch (Throwable e) {
            logger.error("Error during rendering", e)
            // Explicit logging because mqttv3 will silently swallow exceptions
            this.queue.put(new ErrorGatter(e))
        }*/
    }
}
