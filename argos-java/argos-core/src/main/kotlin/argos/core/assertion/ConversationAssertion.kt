package argos.core.assertion

import argos.api.ArgosOptions
import argos.api.IAssertion
import argos.api.IAssertionResult
import org.reactivestreams.Publisher

class ConversationAssertion(val spec: ConversationAssertionSpec): IAssertion {
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        TODO("Not yet implemented")
    }
}