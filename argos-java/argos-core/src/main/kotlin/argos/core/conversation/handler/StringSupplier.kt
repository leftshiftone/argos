package argos.core.conversation.handler

import java.util.concurrent.BlockingDeque
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class StringSupplier(private val deque: BlockingDeque<String>) : Supplier<String> {
    override fun get(): String {
        try {
            return when (val e = deque.pollLast(30, TimeUnit.SECONDS)) {
                null -> throw RuntimeException("Waiting for context timed out")
                else -> e
            }
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    fun clear() {
        deque.clear()
    }

}
