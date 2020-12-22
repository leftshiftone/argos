package argos.core.assertion

import argos.api.*
import argos.core.support.FileSupport
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

/**
 * This IAssertion implementation is used to compare the result of the
 * text to speech skill evaluation with an expected result value.
 */
class Text2SpeechAssertion(val spec: Text2SpeechAssertionSpec): IAssertion {
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val gaiaRef = Gaia.connect(options.config)

            val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text" to spec.text))

            Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val speech = e["speech"] ?: return@map Failure("Empty response")
                    if (speech !is ByteArray)
                        return@map Failure("Format Error: (speech=${speech::class.simpleName})")
                    if (speech.contentEquals(FileSupport.getByteArrayFromFile(spec.speech)))
                        Success(e.toString())
                    else
                        Failure("{speech=${speech.asList()}}")
                }
        }
        catch (ex: Throwable) {
            Flowable.just(Error(ex))
        }
    }
}