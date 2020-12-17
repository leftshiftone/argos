package argos.core.assertion

import argos.api.*
import argos.core.listener.LoggingAssertionListener
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class SimilarityAssertion(val spec: SimilarityAssertionSpec) : IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val gaiaRef = Gaia.connect(options.config)

            val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text1" to spec.text1, "text2" to spec.text2))

            Flowable.fromPublisher(request).map { it.asMap() }
                .map { e ->
                    val score = e["score"] ?: 0.0
                    if (score is Float && score >= spec.threshold)
                        Success(e.toString())
                    else
                        Failure(e.toString())
                }
        }
        catch (ex: Throwable) {
            Flowable.just(Error(ex))
        }
    }
}