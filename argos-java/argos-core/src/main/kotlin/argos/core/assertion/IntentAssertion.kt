package argos.core.assertion

import argos.api.ArgosOptions
import argos.api.IAssertion
import argos.api.IAssertionResult
import io.reactivex.Flowable
import org.reactivestreams.Publisher

// TODO: javadoc
class IntentAssertion(val spec: IntentAssertionSpec) : IAssertion {

    // TODO: javadoc
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return Flowable.empty()
    }

}
