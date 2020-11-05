package argos.core.assertion

import argos.api.*
import argos.core.assertion.support.ImageSupport
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class OCRAssertion(val spec: OCRAssertionSpec): IAssertion {
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.Companion.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("image" to ImageSupport.getByteArrayFromImage(spec.image)))

        return Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val responseText = e["text"] ?: ""
                    if (responseText !is String) return@map Failure("Format Error")

                    for (text in spec.texts) {
                        if(text.text == responseText)
                            if (text.fuzzy)
                                return@map Success("success")
                            else
                                return@map Failure("Texts shouldn't match: ${text.text} ($responseText)")
                    }
                    return@map Failure("No matching Text found: ${spec.texts} ($responseText)")
                }
    }
}