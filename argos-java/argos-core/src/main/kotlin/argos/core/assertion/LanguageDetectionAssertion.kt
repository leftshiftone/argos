package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

/**
 * This IAssertion implementation is used to compare the result of the
 * language detection skill evaluation with an expected result value.
 */
class LanguageDetectionAssertion(val spec: LanguageDetectionAssertionSpec): IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val gaiaRef = Gaia.Companion.connect(options.config)

            val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text" to spec.text))

            Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val lang = e["lang"] ?: ""
                    if (spec.lang == lang)
                        return@map Success(e.toString())
                    Failure(e.toString())
                }
        }
        catch (ex: Throwable) {
            Flowable.just(Error(ex))
        }
    }
}