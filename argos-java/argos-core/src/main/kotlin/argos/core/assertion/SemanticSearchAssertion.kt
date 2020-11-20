package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

class SemanticSearchAssertion(val spec: SemanticSearchAssertionSpec): IAssertion {
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        val gaiaRef = Gaia.connect(options.config)

        val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("message" to mapOf("terms" to arrayOf(spec.text))))

        return Flowable.fromPublisher(request)
                .map { it.asMap() }
                .map { e ->
                    val message = e["message"] ?: return@map Failure("Empty response")

                    if (message is Map<*, *>) {
                        val results = message["results"] ?: return@map Failure("No results")

                        if (results is Array<*>) {
                            if (results.size != spec.topN) return@map Failure("Unexpected results size: (${results.size})")

                            for (result in results) {
                                if (result is Map<*,*>) {
                                    val id = result["id"] as String
                                    val score = result["score"] as Float

                                    spec.entries.forEach {
                                        if (id == it.id && score >= it.score)
                                            return@map Success("success")
                                    }
                                }
                                else return@map Failure("Format Error: (result: ${result!!::class})")
                            }
                        }
                        else return@map Failure("Format Error: (results: ${results::class})")
                    }
                    else return@map Failure("Format error: (message: ${message::class})")

                    Failure("failure")
                }
    }
}