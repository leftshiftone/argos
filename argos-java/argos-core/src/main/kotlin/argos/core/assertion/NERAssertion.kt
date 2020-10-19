package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.lang.Exception

class NERAssertion(val spec: NERAssertionSpec): IAssertion {

    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("text" to spec.text))

        val result = Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    try {
                        val nerResult = e.get("ner")
                        if (nerResult !is List<*>) throw Exception("Format Error")

                        for (ner in nerResult) {
                            if (ner !is Map<*,*>) throw Exception("Format Error")

                            for (entity in spec.entities) {
                                if (ner.get("label")!!.equals(entity.label) && !entity.not)
                                    return@map Success("success")
                            }
                        }
                        Failure("failure")
                    }
                    catch (ex: Throwable) {
                        Error(ex)
                    }
                }

        return result
    }
}