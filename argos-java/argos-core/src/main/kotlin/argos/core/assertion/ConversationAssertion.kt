package argos.core.assertion

import argos.api.*
import argos.core.conversation.AbstractGatter
import argos.core.conversation.ConnectionEvaluator
import argos.core.conversation.handler.GatterConsumer
import argos.core.conversation.handler.GatterSupplier
import argos.core.conversation.handler.StringSupplier
import argos.core.conversation.listener.ContextMessageListener
import argos.core.conversation.listener.InteractionMessageListener
import argos.core.conversation.listener.LoggingMessageListener
import argos.core.conversation.listener.NotificationMessageListener
import argos.identity.support.Connection
import gaia.sdk.api.queue.ConversationQueueType.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

// TODO: javadoc
class ConversationAssertion(val spec: ConversationAssertionSpec) : IAssertion {
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val intQueue = LinkedBlockingQueue<AbstractGatter>()
            val ctxQueue = LinkedBlockingDeque<String>()
            val logQueue = LinkedBlockingDeque<String>()
            val ntfQueue = LinkedBlockingDeque<String>()

            val connection = Connection(options).connect()
                .subscribe(INTERACTION, InteractionMessageListener(intQueue))
                .subscribe(CONTEXT, ContextMessageListener(ctxQueue))
                .subscribe(LOGGING, LoggingMessageListener(logQueue))
                .subscribe(NOTIFICATION, NotificationMessageListener(ntfQueue))
                .reception(spec.attributes)

            val intSupplier = GatterSupplier(intQueue, timeout())
            val ctxSupplier = StringSupplier(ctxQueue)
            val logSupplier = StringSupplier(logQueue)
            val ntfSupplier = StringSupplier(ntfQueue)
            val intConsumer = GatterConsumer(connection)

            val errorCollector = ArrayList<String>()
            val evaluation = ConnectionEvaluator(spec.participants)
                .evaluate(intConsumer, intSupplier, ctxSupplier, logSupplier, ntfSupplier, errorCollector)

            errorCollector.forEach(System.err::println)

            when (evaluation) {
                true -> Flowable.just(Success("conversation successful"))
                false -> Flowable.just(Failure("conversation failed"))
            }
        }
        catch (ex: Throwable) {
            Flowable.just(Error(ex))
        }
    }

    /**
     * May be used to set a custom timeout per test implementation
     * @param timeout the time
     * @param timeUnit the unit of the timeout
     * @return a pair of timout and timeUnit
     */
    private fun timeout(timeout: Long = 30L, timeUnit: TimeUnit = TimeUnit.SECONDS): Pair<Long, TimeUnit> {
        return Pair(timeout, timeUnit)
    }

}