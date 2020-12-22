/*
 * Copyright (c) 2016-2020, Leftshift One
 * __________________
 * [2020] Leftshift One
 * All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains
 * the property of Leftshift One and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Leftshift One
 * and its suppliers and may be covered by Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Leftshift One.
 */
package argos.identity.support

import argos.api.ArgosOptions
import argos.core.conversation.listener.AbstractMessageListener
import canon.model.Button
import canon.model.Submit
import gaia.sdk.api.queue.ConvInteraction
import gaia.sdk.api.queue.ConversationQueueType
import gaia.sdk.api.queue.QueueHeader
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

// TODO: javadoc
class Connection(options: ArgosOptions) {
    private val logger = LoggerFactory.getLogger(Connection::class.java)
    private val header = QueueHeader(UUID.fromString(options.identity), UUID.randomUUID())
    private val queue = options.config.queueProcessor

    fun connect(): Connection {
        queue.connect().blockingAwait()
        return this
    }

    fun subscribe(type: ConversationQueueType, listener: AbstractMessageListener): Connection {
        queue.subscribe(type, header) { listener.handle(it) }.blockingAwait()
        logger.info("Successfully subscribed to {}", type.getName())
        return this
    }

    fun reception(attributes: Map<String, Any>): Connection {
        publish("reception", attributes, null)
        return this
    }

    fun publishButton(button: Button) {
        val attributes = HashMap<String, Any?>()
        attributes["name"] = button.name
        attributes["value"] = button.value
        publish("button", attributes, null)
    }

    fun publishSubmit(submit: Submit) {
        val attributes = HashMap<String, Any?>()
        attributes["name"] = submit.name
        if (submit.text != null && submit.text!!.isNotBlank())
            attributes["value"] = submit.text
        else
            attributes["value"] = "{}"
        publish("submit", attributes, null)
    }

    fun publishUtterance(text: String) {
        val attributes: MutableMap<String, Any> = HashMap()
        attributes["text"] = text
        publish("utterance", attributes, text)
    }

    private fun publish(type: String, attributes: Any, payload: Any?) {
        val interaction = toConvInteraction(type, attributes, payload)
        queue.publishConvInteraction(header, interaction).blockingAwait()
    }

    private fun toConvInteraction(type: String, attributes: Any, payload: Any?): ConvInteraction {
        val content: MutableMap<String, Any> = HashMap()
        content["type"] = type
        content["attributes"] = attributes
        if (payload != null)
            content["payload"] = payload
        return ConvInteraction(content)
    }

}