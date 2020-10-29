package argos.core.conversation.handler

import argos.core.conversation.AbstractGatter
import argos.core.conversation.Error
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class GatterSupplier(private val queue: BlockingQueue<AbstractGatter>,
                     private val timeoutTuple: Pair<Long, TimeUnit>) : Supplier<AbstractGatter> {

    override fun get(): AbstractGatter {
        try {
            return when (val e = queue.poll(timeoutTuple.first, timeoutTuple.second)) {
                null -> throw RuntimeException("Waiting for gatter timed out")
                is Error -> throw RuntimeException(e.throwable)
                else -> e
            }
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }
}
