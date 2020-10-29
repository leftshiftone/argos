package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.imageio.ImageIO

class ImageSimilarityAssertion(val spec: ImageSimilarityAssertionSpec) : IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf(
                        "image1" to getByteArrayFromImage(spec.image1),
                        "image2" to getByteArrayFromImage(spec.image2)))

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

    private fun getByteArrayFromImage(imageURL: String): ByteArray {
        val byteStream = ByteArrayOutputStream()
        val url = URL(imageURL)
        val extension = imageURL.substringAfterLast(".")

        val image = ImageIO.read(url)
        ImageIO.write(image, extension, byteStream)

        return byteStream.toByteArray()
    }
}