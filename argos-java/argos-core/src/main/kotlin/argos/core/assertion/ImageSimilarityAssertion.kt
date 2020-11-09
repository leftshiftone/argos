package argos.core.assertion

import argos.api.*
import argos.core.assertion.support.ImageSupport
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class ImageSimilarityAssertion(val spec: ImageSimilarityAssertionSpec) : IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf(
                        "image1" to ImageSupport.getByteArrayFromImage(spec.image1),
                        "image2" to ImageSupport.getByteArrayFromImage(spec.image2)))

        return Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val score = e["score"]!!
                    if (score is Float && score >= spec.threshold)
                        Success("success")
                    else
                        Failure("failure")
                }
    }
}