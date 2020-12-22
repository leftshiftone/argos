package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

/**
 * This IAssertion implementation is used to compare the result of the
 * named entity recognition skill evaluation with an expected result value.
 */
class NERAssertion(val spec: NERAssertionSpec): IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val gaiaRef = Gaia.connect(options.config)

            val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text" to spec.text))

            Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val nerResult = e["ner"]

                    if (nerResult !is List<*>) return@map Failure("Format Error")

                    for (ner in nerResult) {
                        if (ner !is Map<*,*>) return@map Failure("Format Error")

                        for (entity in spec.entities) {
                            val label = ner["label"] ?: ""
                            val text = ner["text"] ?: ""

                            if (text == entity.text) {
                                if (label == entity.label) {
                                    if (entity.not)
                                        return@map Failure("{ner=not(Entity(text=${text}, label=${label}))}")
                                }
                                else {
                                    if (!entity.not)
                                        return@map Failure("{ner=Entity(text=${text}, label=${label})}")
                                }
                            }
                        }
                    }
                    Success(e.toString())
                }
        }
        catch (ex: Throwable) {
            Flowable.just(Error(ex))
        }
    }
}