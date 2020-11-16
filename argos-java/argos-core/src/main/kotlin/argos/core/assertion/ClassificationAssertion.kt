package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class ClassificationAssertion(val spec: ClassificationAssertionSpec): IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.Companion.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text" to spec.text))

        return Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    if (e["class"] == spec.`class`)
                        Success("success")
                    else
                        Failure("failure")
                }
    }
}