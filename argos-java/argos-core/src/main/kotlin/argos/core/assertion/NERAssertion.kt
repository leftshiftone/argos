package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class NERAssertion(val spec: NERAssertionSpec): IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text" to spec.text))

        return Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val nerResult = e["ner"]

                    if (nerResult !is List<*>) return@map Failure("Format Error")

                    for (ner in nerResult) {
                        if (ner !is Map<*,*>) return@map Failure("Format Error")

                        for (entity in spec.entities) {
                            val label = ner["label"] ?: ""

                            if (label != entity.label && !entity.not)
                                return@map Failure("Entity mismatch: $entity ($label)")
                            if (label == entity.label && entity.not)
                                return@map Failure("Entity mismatch: $entity ($label)")
                        }
                    }
                    Success("success")
                }
    }
}