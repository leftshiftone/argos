package argos.core.assertion

import argos.api.*
import argos.core.support.ImageSupport
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

/**
 * This IAssertion implementation is used to compare the result of the
 * image similarity skill evaluation with an expected result value.
 */
class ImageSimilarityAssertion(val spec: ImageSimilarityAssertionSpec) : IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val gaiaRef = Gaia.connect(options.config)

            val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf(
                    "image1" to ImageSupport.getByteArrayFromImage(spec.image1),
                    "image2" to ImageSupport.getByteArrayFromImage(spec.image2)))

            Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val score = e["score"]!!
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