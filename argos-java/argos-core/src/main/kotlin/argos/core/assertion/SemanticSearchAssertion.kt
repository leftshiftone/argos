package argos.core.assertion

import argos.api.*
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher

/**
 * This IAssertion implementation is used to compare the result of the
 * semantic search skill evaluation with an expected result value.
 */
class SemanticSearchAssertion(val spec: SemanticSearchAssertionSpec): IAssertion {
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        return try {
            val gaiaRef = Gaia.connect(options.config)

            val request: Publisher<SkillEvaluation> = gaiaRef.skill(options.config.url)
                .evaluate(mapOf("message" to mapOf("terms" to arrayOf(spec.text))))

            Flowable.fromPublisher(request)
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
                                            return@map Success(e.toString())
                                    }
                                }
                                else return@map Failure("Format Error: (result=${result!!::class.simpleName})")
                            }
                        }
                        else return@map Failure("Format Error: (results=${results::class.simpleName})")
                    }
                    else return@map Failure("Format error: (message=${message::class.simpleName})")

                    Failure("{message={results=${((e["message"] as Map<*,*>)["results"] as Array<*>).map { it }}}}")
                }
        }
        catch (ex: Throwable) {
            Flowable.just(Error(ex))
        }
    }
}