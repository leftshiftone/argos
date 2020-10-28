package argos.runtime.dsl

import argos.api.*
import argos.core.assertion.Conversation
import gaia.sdk.HMACCredentials
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import gaia.sdk.core.GaiaConfig
import gaia.sdk.core.GaiaRef
import io.mockk.*
import io.reactivex.Flowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher

class ArgosDSLTest {
    private val options = ArgosOptions("", GaiaConfig("", HMACCredentials("", "")))
    private lateinit var gaiaRef: GaiaRef

    @Test
    fun testIntent() {
        setResponse(mapOf(":type" to "Match", "reference" to "findLawyer"))

        val result = ArgosDSL.argos("argos test", options) {
            assertIntent("ich suche einen anwalt", "findLawyer")
        }
        val type = Flowable.fromPublisher(result).blockingFirst()

        Assertions.assertTrue(type is Success)
    }

    @Test
    fun testSimilarity() {
        setResponse(mapOf("score" to 0.9f))

        val result = ArgosDSL.argos("argos test", options) {
            assertSimilarity("Der erste Text", "Der zweite Text")
        }
        val type = Flowable.fromPublisher(result).blockingFirst()

        Assertions.assertTrue(type is Success)
    }

    @Test
    fun testNER() {
        setResponse(mapOf("ner" to listOf(mapOf("text" to "steiermark", "label" to "location"))))

        val result = ArgosDSL.argos("argos test", options) {
            assertNer("ich suche einen anwalt in der steiermark") {
                entity("location", "steiermark", 6)
                not(entity("organization"))
            }
        }
        val type = Flowable.fromPublisher(result).blockingFirst()

        Assertions.assertTrue(type is Success)
    }

    @Test
    fun testTranslation() {
        setResponse(mapOf("text" to "i am looking for a lawyer", "lang" to "en"))

        val result = ArgosDSL.argos("argos test", options) {
            assertTranslation("de", "ich suche einen anwalt",
                    "en", "i am looking for a lawyer", 0.9f)
        }
        val type = Flowable.fromPublisher(result).blockingFirst()

        Assertions.assertTrue(type is Success)
    }

    @Test
    fun testSentiment() {
        setResponse(mapOf("type" to "neutral"))
        val result = ArgosDSL.argos("argos test", options) {
            assertSentiment("ich suche einen anwalt", "neutral")
        }
        Assertions.assertTrue(Flowable.fromPublisher(result).blockingFirst() is Success)


        every { gaiaRef.skill("").evaluate(mapOf("text" to "Das Produkt ist gut")) } returns Flowable.just(
                SkillEvaluation(mapOf("type" to "positive")))
        every { gaiaRef.skill("").evaluate(mapOf("text" to "Das Produkt ist nicht gut")) } returns Flowable.just(
                SkillEvaluation(mapOf("type" to "negative")))

        val response = ArgosDSL.argos("argos test", options) {
            assertSentiment("Das Produkt ist gut", "positive")
            assertSentiment("Das Produkt ist nicht gut", "negative")
        }

        Flowable.fromPublisher(response).forEach { Assertions.assertTrue(it is Success) }
    }

    @Test
    fun testImageSimilarity() {
        setResponse(mapOf("score" to 0.9f))
        val exampleImage = "https://image.shutterstock.com/image-photo/abstract-ocean-art-natural-luxury-600w-1040400583.jpg"

        val result = ArgosDSL.argos("argos test", options) {
            assertImageSimilarity(exampleImage, exampleImage,0.9f)
        }
        val type = Flowable.fromPublisher(result).blockingFirst()

        Assertions.assertTrue(type is Success)
    }

    @Test
    fun testConversation() {
        ArgosDSL.argos("argos test", options) {
            assertConversation {
                gaia(
                        Conversation.Property.Text(textContent = "Hallo, ich bin Ihr digitaler UBIT-Assistent. Wollen Sie Ihren Digitalisierungs-Index in 7 Minuten ermitteln oder wollen Sie direkt einen UBIT-Profi über die Trendit Roadmap suchen?"),
                        Conversation.Property.Button(name = "result", value = "digicheck", textContent = "Ermittle Digitalisierungs-Index"),
                        Conversation.Property.Button(name = "result", value = "prozess", textContent = "Suche UBIT-Profi"))
                user(
                        Conversation.Property.Button(position = "right", value = "digicheck", name = "result", textContent = "Ermittle Digitalisierungs-Index"))

                this.forEach { it.printElement() }
            }
        }

    }

    @BeforeEach
    fun initMock() {
        mockkObject(ArgosDSL)
        mockkObject(Gaia)
        gaiaRef = mockk()

        every { Gaia.connect(options.config) } returns gaiaRef
    }

    @AfterEach
    fun verifyMock() {
        verify { Gaia.connect(options.config) }
        verify { gaiaRef.skill("").evaluate(any()) }
        verify { ArgosDSL.argos("argos test", options, any()) }

        confirmVerified(ArgosDSL)
        confirmVerified(gaiaRef)
    }

    fun setResponse(map: Map<String, Any>) {
        every { gaiaRef.skill("").evaluate(any()) } returns Flowable.just(
                SkillEvaluation(map))
    }
}