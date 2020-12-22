package argos.core.assertion

import argos.api.*
import argos.core.support.ImageSupport
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

/**
 * This IAssertion implementation is used to compare the result of the
 * image skill evaluation with an expected result value.
 */
class ImageAssertion(val spec: ImageAssertionSpec): IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val gaiaRef = Gaia.connect(options.config)

            val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf(
                    "skillName" to spec.skill,
                    "sourceImage" to ImageSupport.getByteArrayFromImage(spec.source)))

            Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val image = e["image"] ?: return@map Failure("Empty response")
                    if (image !is ByteArray)
                        return@map Failure("Format Error (${image::class})")
                    if (image.contentEquals(ImageSupport.getByteArrayFromImage(spec.target)))
                        Success(e.toString())
                    else
                        Failure("{image=${image.asList()}")
                }
        }
        catch (ex: Throwable) {
            Flowable.just(Error(ex))
        }

    }
}