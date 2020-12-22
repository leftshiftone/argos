package argos.core.listener

import argos.api.*
import argos.core.assertion.*
import argos.core.listener.support.AssertionTestcase
import argos.core.support.FileSupport
import argos.core.support.ImageSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This class implements the IAssertionListener-Interface to log assertion test results.
 * This listener has to be added to an ArgosOptions instance to be executed in an argos test.
 *
 * @see ArgosOptions.addListener
 */
class LoggingAssertionListener: IAssertionListener {
    private val logger: Logger = LoggerFactory.getLogger(LoggingAssertionListener::class.java)

    private var startTime: Long? = null
    private var stopTime: Long? = null
    private var msg: String? = null

    override fun onBeforeAssertion(assertion: IAssertion) {
        startTime = System.currentTimeMillis()
    }

    override fun onAfterAssertion(assertion: IAssertion, result: IAssertionResult) {
        try {
            val expected: String = when(assertion) {
                is ClassificationAssertion -> "{class=${assertion.spec.`class`}}"
                is ImageAssertion -> "{image=${ImageSupport.getByteArrayFromImage(assertion.spec.target).asList()}}"
                is ImageSimilarityAssertion -> "{score>=${assertion.spec.threshold}}"
                is IntentAssertion -> "{:type=Match, reference=${assertion.spec.intent}}"
                is LanguageDetectionAssertion -> "{lang=${assertion.spec.lang}}"
                is NERAssertion -> "{ner=${assertion.spec.entities}}"
                is OCRAssertion -> "{text=${assertion.spec.texts.map { it.text }}}"
                is RegressionAssertion -> "{score>=${assertion.spec.score}}"
                is SemanticSearchAssertion -> "{message=${assertion.spec.entries}}"
                is SentimentAssertion -> "{type=${assertion.spec.type}}"
                is SimilarityAssertion -> "{score>=${assertion.spec.threshold}}"
                is Speech2TextAssertion -> "{text=${assertion.spec.text}}"
                is Text2SpeechAssertion -> "{speech=${FileSupport.getByteArrayFromFile(assertion.spec.speech).asList()}}"
                is TranslationAssertion -> "{text=${assertion.spec.translatedText}, lang=${assertion.spec.translationLang}}"
                else -> throw RuntimeException("cannot find assertion type ${assertion::class.java.simpleName}")
            }

            when(result) {
                is Success  -> logSuccess("${assertion::class.java.simpleName}: Success")
                is Failure  -> logFailure("\n(Expected):   $expected\n(Actual):     ${result.getMessage()}")
                is Error    -> logError(result.throwable)
            }
            stopTime = System.currentTimeMillis()
        }
        catch (ex: RuntimeException) {
            logError(ex)
        }
        JUnitReportAssertionListener.logResult(assertion, result, msg!!, stopTime!! - startTime!!)
        startTime = null
        stopTime = null
        msg = null
    }

    private fun logSuccess(msg: String) {
        this.msg = msg
        logger.info(msg)
    }

    private fun logFailure(msg: String) {
        this.msg = msg
        logger.error(msg)
    }

    private fun logError(ex: Throwable) {
        this.msg = ex.message ?: "an error occurred"
        logger.error(ex.message)
    }
}