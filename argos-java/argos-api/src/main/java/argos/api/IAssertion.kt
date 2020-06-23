package argos.api

import org.reactivestreams.Publisher

/**
 * Classes which implements this interface are able to be used by argos to test a specific gaia feature.
 */
interface IAssertion {

    /**
     * Asserts the gaia feature.
     */
    fun assert(options: ArgosOptions):Publisher<IAssertionResult>

}
