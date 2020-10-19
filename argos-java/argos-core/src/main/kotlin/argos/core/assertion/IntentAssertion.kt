package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

// TODO: javadoc
class IntentAssertion(val spec: IntentAssertionSpec) : IAssertion {

    // TODO: javadoc
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text" to spec.text, "treshold" to spec.score))

        val result = Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    try {
                        if (e.get(":type")!!.equals("Match") && e.get("reference")!!.equals(spec.intent))
                            Success("success")
                        else
                            Failure("failure")
                    }
                    catch(ex: Throwable) {
                        Error(ex)
                    }
                }

        return result
    }

}
