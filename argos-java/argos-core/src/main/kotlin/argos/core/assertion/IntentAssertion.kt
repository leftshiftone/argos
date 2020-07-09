package argos.core.assertion

import argos.api.ArgosOptions
import argos.api.IAssertion
import argos.api.IAssertionResult
import argos.api.InteractionTypes
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import gaia.sdk.mqtt.MqttSensorQueue
import gaia.sdk.mqtt.queue.ConvInteraction
import gaia.sdk.mqtt.queue.ConversationQueueType
import gaia.sdk.mqtt.queue.QueueHeader
import gaia.sdk.spi.QueueOptions
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.util.*

// TODO: javadoc
class IntentAssertion(val spec: IntentAssertionSpec) : IAssertion {

    private val objectMapper = ObjectMapper()

    // TODO: javadoc
    override fun assert(options: ArgosOptions): Publisher<IAssertionResult> {
        // create client to gaia sdk, subscribe an send request
        val queueOptions = QueueOptions(options.url, 443)
        queueOptions.username = options.apiKey
        val mqttSensorQueue = MqttSensorQueue(queueOptions)
        val header = QueueHeader(UUID.fromString(options.identity), UUID.randomUUID())

        return mqttSensorQueue.connect()
                .andThen(mqttSensorQueue.subscribeConvInteraction(header) {
                    println(it)
                })
                .andThen(mqttSensorQueue.publishConvInteraction(header, ConvInteraction(
                        mapOf("type" to InteractionTypes.UTTERANCE.value,
                        "text" to spec.text,
                        "attributes" to HashMap<String, Any>())
                )))
                .toFlowable()
    }

}
