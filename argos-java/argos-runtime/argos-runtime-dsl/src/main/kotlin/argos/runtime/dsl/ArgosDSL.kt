package argos.runtime.dsl

import argos.api.ArgosOptions
import argos.api.Error
import argos.api.IAssertion
import argos.api.IAssertionResult
import argos.core.assertion.*
import argos.core.augmenter.QwertzAugmenter
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.util.concurrent.CopyOnWriteArrayList

// TODO: javadoc
class ArgosDSL private constructor(private val name: String, private val options: ArgosOptions) {

    private val assertions = CopyOnWriteArrayList<IAssertion>()

    companion object {
        // TODO: javadoc
        fun argos(name: String, options: ArgosOptions, config: ArgosDSL.() -> Unit): Publisher<IAssertionResult> {
            val dsl = ArgosDSL(name, options).apply(config)
            return Flowable.defer {
                options.getListeners().forEach { it.onBeforeAssertions() }
                Flowable.fromIterable(dsl.assertions)
            }
            .doOnNext { assertion ->
                options.getListeners().forEach { it.onBeforeAssertion(assertion) }
            }
            .flatMap { assertion ->
                Flowable.fromPublisher(assertion.assert(options))
                        .map { Pair(assertion, it) }
                        .onErrorReturn {Pair(assertion, Error(it))}
            }
            .doOnNext { result ->
                options.getListeners().forEach { it.onAfterAssertion(result.first, result.second) }
            }
            .doOnComplete {
                options.getListeners().forEach { it.onAfterAssertions() }
            }
            .map { it.second }
        }
    }

    /**
     * Asserts if the given text is classified to the given intent qualifier.
     *
     * @param text the text to classify
     * @param intent the intent qualifier
     * @param score the threshold score
     */
    @JvmOverloads
    fun assertIntent(text: String, intent: String, score: Float = 0.85f) {
        assertions.add(IntentAssertion(IntentAssertionSpec(text, intent, score)))
    }

    @JvmOverloads
    fun assertSimilarity(text1: String, text2: String, threshold: Float = 0.9f) {
        assertions.add(SimilarityAssertion(SimilarityAssertionSpec(text1, text2, threshold)))
    }

    fun assertNer(text: String, action: NER.() -> Unit) {
        val entity = NER()
        action(entity)
        assertions.add(NERAssertion(NERAssertionSpec(text, entity)))
    }

    class NER: ArrayList<NERAssertionSpec.Entity>() {
        fun entity(label: String, text: String? = null, index: Int?=null, not: Boolean = false)
                : NERAssertionSpec.Entity {
            val entity = NERAssertionSpec.Entity(label, text, index, not)
            add(entity)

            return entity
        }

        fun not(entity: NERAssertionSpec.Entity): NERAssertionSpec.Entity {
            remove(entity)
            val notEntity = NERAssertionSpec.Entity(entity.label, entity.text, entity.index, not = true)
            add(notEntity)

            return notEntity
        }
    }

    @JvmOverloads
    fun assertTranslation(inLang: String, inText: String,
                          translationLang: String, translatedText: String, threshold: Float = 0.9f) {
        assertions.add(TranslationAssertion(
                TranslationAssertionSpec(inLang, inText, translationLang, translatedText, threshold)))
    }

    // TODO: javadoc
    fun qwertzAugmentation(text: String, seed: Long? = null): String {
        return QwertzAugmenter(seed).augment(text)
    }
}
