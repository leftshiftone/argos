package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

/**
 * This IAssertion implementation is used to compare the result of the
 * intent detection skill evaluation with an expected result value.
 */
class IntentAssertion(val spec: IntentAssertionSpec) : IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text" to spec.text, "treshold" to spec.score))

        return Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    if (e[":type"] == "Match" && e["reference"] == spec.intent)
                        Success("success")
                    else
                        Failure("failure")
                }
    }

}
