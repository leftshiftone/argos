package argos.core.assertion

import argos.api.*
import argos.core.support.ImageSupport
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class ImageAssertion(val spec: ImageAssertionSpec): IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf(
                        "skillName" to spec.skill,
                        "sourceImage" to ImageSupport.getByteArrayFromImage(spec.source)))

        return Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val image = e["image"] ?: return@map Failure("Empty response")
                    if (image !is ByteArray)
                        return@map Failure("Format Error (${image::class})")
                    if (image.contentEquals(ImageSupport.getByteArrayFromImage(spec.target)))
                        Success("success")
                    else
                        Failure("failure")
                }
    }
}