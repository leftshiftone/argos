package argos.runtime.dsl

import argos.api.ArgosOptions
import argos.api.Error
import argos.api.IAssertion
import argos.api.IAssertionResult
import argos.core.assertion.*
import argos.core.augmenter.QwertzAugmenter
import argos.runtime.dsl.config.Conversation
import gaia.sdk.core.Gaia
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Class to directly run Argos Assertion Tests.
 */
class ArgosDSL private constructor(private val name: String, private val options: ArgosOptions) {

    private val assertions = CopyOnWriteArrayList<IAssertion>()

    companion object {
        /**
         * Run an argos assertion test.
         *
         * @param name the name of the argos test
         * @param options the required options to connect to a gaia instance
         * @param config the assertions to perform in this test
         *
         * @return the results of the performed assertion tests
         */
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

    /**
     * Asserts if the given texts are similar to each other above the threshold.
     *
     * @param text1 the first text to compare
     * @param text2 the second text to compare
     * @param threshold the threshold score
     */
    @JvmOverloads
    fun assertSimilarity(text1: String, text2: String, threshold: Float = 0.9f) {
        assertions.add(SimilarityAssertion(SimilarityAssertionSpec(text1, text2, threshold)))
    }

    // TODO: javadoc
    fun assertConversation(config: Conversation.() -> Unit) {
        val conv = Conversation().apply(config)
        assertions.add(ConversationAssertion(ConversationAssertionSpec(conv.participants, conv.attributes)))
    }

    /**
     * Asserts if the given text applies to the given entities.
     *
     * @param text the text to check
     * @param entities the applicable entities
     */
    fun assertNer(text: String, entities: NER.() -> Unit) {
        val entity = NER()
        entities(entity)
        assertions.add(NERAssertion(NERAssertionSpec(text, entity)))
    }

    /**
     * Class which contains the entities for a NER Assertion.
     */
    class NER: ArrayList<NERAssertionSpec.Entity>() {
        /**
         * Adds an entity to the NER Assertion.
         *
         * @param label the label of this entity
         * @param text the text associated with this entity
         * @param index the index of this entity in the input text
         * @param not if true, than the input text should not apply to this entity
         *
         * @return the entity added to the NER Assertion
         */
        @JvmOverloads
        fun entity(label: String, text: String? = null, index: Int?=null, not: Boolean = false)
                : NERAssertionSpec.Entity {
            val entity = NERAssertionSpec.Entity(label, text, index, not)
            add(entity)

            return entity
        }

        /**
         * Adds an entity to the NER Assertion which should not occur in the input text.
         *
         * @param entity the entity which should not occur in the input text
         *
         * @return the entity added to the NER Assertion
         */
        fun not(entity: NERAssertionSpec.Entity): NERAssertionSpec.Entity {
            remove(entity)
            val notEntity = NERAssertionSpec.Entity(entity.label, entity.text, entity.index, not = true)
            add(notEntity)

            return notEntity
        }
    }

    /**
     * Asserts if two texts from different languages are similar to a given threshold.
     *
     * @param inLang the language of the input text
     * @param inText the input text
     * @param translationLang the language of the translated text
     * @param translatedText the translated text
     * @param threshold the threshold score
     */
    @JvmOverloads
    fun assertTranslation(inLang: String, inText: String,
                          translationLang: String, translatedText: String, threshold: Float = 0.9f) {
        assertions.add(TranslationAssertion(
                TranslationAssertionSpec(inLang, inText, translationLang, translatedText, threshold)))
    }

    /**
     * Asserts if the given text applies to a sentiment type.
     *
     * @param text the given text
     * @param type the given sentiment type
     */
    fun assertSentiment(text: String, type: String) {
        assertions.add(SentimentAssertion(SentimentAssertionSpec(text, type)))
    }

    /**
     * Asserts if the given images are similar to each other above a given threshold.
     *
     * @param image1 the URL to the first image
     * @param image2 the URL to the second image
     * @param threshold the threshold score
     */
    @JvmOverloads
    fun assertImageSimilarity(image1: String, image2: String, threshold: Float = 0.9f) {
        assertions.add(ImageSimilarityAssertion(ImageSimilarityAssertionSpec(image1, image2, threshold)))
    }

    /**
     * Asserts if the input image contains the given texts.
     *
     * @param image the URL to the given image
     * @param texts the texts to consider
     */
    fun assertOCR(image: String, texts: OCR.() -> Unit) {
        val text = OCR()
        texts(text)
        assertions.add(OCRAssertion(OCRAssertionSpec(image, text)))
    }

    /**
     * Class which contains the texts for an OCR Assertion.
     */
    class OCR: ArrayList<OCRAssertionSpec.Text>() {
        /**
         * Adds a text to the OCR Assertion.
         *
         * @param text the given text
         * @param fuzzy indicates if the recognized text is fuzzy on the input image
         */
        fun text(text: String, fuzzy: Boolean) {
            add(OCRAssertionSpec.Text(text, fuzzy))
        }
    }

    /**
     * Asserts if the text applies to the given language.
     *
     * @param text the given text
     * @param lang the given language
     */
    fun assertLanguageDetection(text: String, lang: String) {
        assertions.add(LanguageDetectionAssertion(LanguageDetectionAssertionSpec(text, lang)))
    }

    /**
     * Asserts if the text is classifiable by a given classification.
     *
     * @param text the given text
     * @param class the given classification
     */
    fun assertClassification(text: String, `class`: String) {
        assertions.add(ClassificationAssertion(ClassificationAssertionSpec(text, `class`)))
    }

    // TODO: javadoc
    fun assertRegression(text: String, score: Float) {
        assertions.add(RegressionAssertion(RegressionAssertionSpec(text, score)))
    }

    /**
     * Asserts if the source image is similar to the target image after applying a skill.
     *
     * @param skill the skill to apply on the source image
     * @param source the URL to the image on which the skill is applied
     * @param target the URL to the intended resulting image
     */
    fun assertImage(skill: String, source: String, target: String) {
        assertions.add(ImageAssertion(ImageAssertionSpec(skill, source, target)))
    }

    /**
     * Asserts if a given input text is similar to a resulting speech.
     *
     * @param text the given text
     * @param speech the URL to the WAV File of the resulting speech
     */
    fun assertText2Speech(text: String, speech: String) {
        assertions.add(Text2SpeechAssertion(Text2SpeechAssertionSpec(text, speech)))
    }

    /**
     * Asserts if a given speech is similar to a resulting text.
     *
     * @param speech the URL to the WAV File of the input speech
     * @param text the intended resulting text
     */
    fun assertSpeech2Text(speech: String, text: String) {
        assertions.add(Speech2TextAssertion(Speech2TextAssertionSpec(speech, text)))
    }

    // TODO: javadoc
    fun assertSemanticSearch(text: String, topN: Int, entries: SemanticSearch.() -> Unit) {
        val entry = SemanticSearch()
        entries(entry)
        assertions.add(SemanticSearchAssertion(SemanticSearchAssertionSpec(text, topN, entry)))
    }

    // TODO: javadoc
    class SemanticSearch: ArrayList<SemanticSearchAssertionSpec.Entry>() {
        // TODO: javadoc
        fun entry(id: String, score: Float) {
            add(SemanticSearchAssertionSpec.Entry(id, score))
        }
    }

    /**
     * Adds typos to the input text and returns it.
     *
     * @param text the given text
     * @param seed the initial seed of the random generator
     *
     * @return the text with typos added
     */
    fun qwertzAugmentation(text: String, seed: Long? = null): String {
        return QwertzAugmenter(seed).augment(text)
    }
}
