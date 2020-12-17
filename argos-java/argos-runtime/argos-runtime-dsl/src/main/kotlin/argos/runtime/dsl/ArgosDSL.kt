package argos.runtime.dsl

import argos.api.ArgosOptions
import argos.api.AssertionGroup
import argos.api.IAssertion
import argos.api.IAssertionResult
import argos.core.assertion.*
import argos.core.augmenter.QwertzAugmenter
import argos.runtime.dsl.config.Conversation
import org.reactivestreams.Publisher
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Class to directly run Argos Assertion Tests.
 */
class ArgosDSL {
    private val assertions: CopyOnWriteArrayList<AssertionGroup> = CopyOnWriteArrayList()

    companion object: AbstractArgos() {
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
            val dsl = ArgosDSL().apply(config)
            return argos(name, options, dsl.assertions)
        }
    }

    private fun addAssertion(assertion: IAssertion) {
        assertions.add(AssertionGroup(null, listOf(assertion)))
    }

    fun assertionGroup(name: String, config: ArgosDSL.() -> Unit) {
        val copy = CopyOnWriteArrayList(assertions)
        assertions.clear()

        apply(config)
        val group = AssertionGroup(name, assertions.flatMap { it.assertions })
        assertions.clear()
        assertions.addAll(copy)
        assertions.add(group)
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
        addAssertion(IntentAssertion(IntentAssertionSpec(text, intent, score)))
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
        addAssertion(SimilarityAssertion(SimilarityAssertionSpec(text1, text2, threshold)))
    }

    // TODO: javadoc
    fun assertConversation(config: Conversation.() -> Unit) {
        val conv = Conversation().apply(config)
        addAssertion(ConversationAssertion(ConversationAssertionSpec(conv.participants, conv.attributes)))
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
        addAssertion(NERAssertion(NERAssertionSpec(text, entity)))
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
        fun entity(label: String, text: String, index: Int?=null, not: Boolean = false)
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
        addAssertion(TranslationAssertion(
                TranslationAssertionSpec(inLang, inText, translationLang, translatedText, threshold)))
    }

    /**
     * Asserts if the given text applies to a sentiment type.
     *
     * @param text the given text
     * @param type the given sentiment type
     */
    fun assertSentiment(text: String, type: String) {
        addAssertion(SentimentAssertion(SentimentAssertionSpec(text, type)))
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
        addAssertion(ImageSimilarityAssertion(ImageSimilarityAssertionSpec(image1, image2, threshold)))
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
        addAssertion(OCRAssertion(OCRAssertionSpec(image, text)))
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
        addAssertion(LanguageDetectionAssertion(LanguageDetectionAssertionSpec(text, lang)))
    }

    /**
     * Asserts if the text is classifiable by a given classification.
     *
     * @param text the given text
     * @param class the given classification
     */
    fun assertClassification(text: String, `class`: String) {
        addAssertion(ClassificationAssertion(ClassificationAssertionSpec(text, `class`)))
    }

    // TODO: javadoc
    fun assertRegression(text: String, score: Float) {
        addAssertion(RegressionAssertion(RegressionAssertionSpec(text, score)))
    }

    /**
     * Asserts if the source image is similar to the target image after applying a skill.
     *
     * @param skill the skill to apply on the source image
     * @param source the URL to the image on which the skill is applied
     * @param target the URL to the intended resulting image
     */
    fun assertImage(skill: String, source: String, target: String) {
        addAssertion(ImageAssertion(ImageAssertionSpec(skill, source, target)))
    }

    /**
     * Asserts if a given input text is similar to a resulting speech.
     *
     * @param text the given text
     * @param speech the URL to the WAV File of the resulting speech
     */
    fun assertText2Speech(text: String, speech: String) {
        addAssertion(Text2SpeechAssertion(Text2SpeechAssertionSpec(text, speech)))
    }

    /**
     * Asserts if a given speech is similar to a resulting text.
     *
     * @param speech the URL to the WAV File of the input speech
     * @param text the intended resulting text
     */
    fun assertSpeech2Text(speech: String, text: String) {
        addAssertion(Speech2TextAssertion(Speech2TextAssertionSpec(speech, text)))
    }

    // TODO: javadoc
    fun assertSemanticSearch(text: String, topN: Int, entries: SemanticSearch.() -> Unit) {
        val entry = SemanticSearch()
        entries(entry)
        addAssertion(SemanticSearchAssertion(SemanticSearchAssertionSpec(text, topN, entry)))
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
