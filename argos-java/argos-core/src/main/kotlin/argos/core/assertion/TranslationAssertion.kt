package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class TranslationAssertion(val spec: TranslationAssertionSpec) : IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.Companion.connect(options.config)
        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text" to spec.inText, "lang" to spec.inLang))

        return Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    if (e["lang"] == spec.translationLang && e["text"] == spec.translatedText)
                        Success("success")
                    else
                        Failure("failure")
                }
    }
}