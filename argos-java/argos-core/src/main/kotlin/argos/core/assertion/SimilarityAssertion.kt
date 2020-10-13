package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class SimilarityAssertion(val spec: SimilarityAssertionSpec) : IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.connect(options.config)

        val skillEvalPub: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text1" to spec.text1, "text2" to spec.text2))

        val result = Flowable.fromPublisher(skillEvalPub).map { it.asMap() }
                .map { e ->
                    try {
                        val score = e.get("score")!!
                        if (score is Float && score >= spec.threshold)
                            Success("success")
                        else
                            Failure("failure")
                    } catch (ex: Throwable) {
                        Error(ex)
                    }
                }

        return result
    }
}