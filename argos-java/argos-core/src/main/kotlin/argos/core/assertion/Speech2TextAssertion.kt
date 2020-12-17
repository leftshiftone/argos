package argos.core.assertion

import argos.api.*
import argos.core.listener.LoggingAssertionListener
import argos.core.support.FileSupport
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class Speech2TextAssertion(val spec: Speech2TextAssertionSpec): IAssertion {
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val gaiaRef = Gaia.connect(options.config)

            val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("speech" to FileSupport.getByteArrayFromFile(spec.speech)))

            Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val text = e["text"] ?: return@map Failure("Empty response")
                    if (text !is String)
                        return@map Failure("Format Error (${text::class})")
                    if (text == spec.text)
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