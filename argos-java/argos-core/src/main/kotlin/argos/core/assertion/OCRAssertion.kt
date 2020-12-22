package argos.core.assertion

import argos.api.*
import argos.core.support.ImageSupport
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

/**
 * This IAssertion implementation is used to compare the result of the
 * optical character recognition skill evaluation with an expected result value.
 */
class OCRAssertion(val spec: OCRAssertionSpec): IAssertion {
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val gaiaRef = Gaia.connect(options.config)

            val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("image" to ImageSupport.getByteArrayFromImage(spec.image)))

            Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val responseText = e["text"] ?: ""
                    if (responseText !is String) return@map Failure("Format Error")

                    for (text in spec.texts) {
                        if(text.text == responseText)
                            return@map Success(e.toString())
                    }
                    Failure(e.toString())
                }
        }
        catch (ex: Throwable) {
            Flowable.just(Error(ex))
        }
    }
}