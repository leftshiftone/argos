package argos.runtime.dsl.config

import argos.core.conversation.AbstractParticipant
import argos.core.conversation.And
import argos.core.conversation.Gaia
import argos.core.conversation.User
import canon.parser.dsl.CanonDSLParser

/**
 * Class which is used to build a conversation.
 */
class Conversation {

    val participants = ArrayList<AbstractParticipant>()
    val attributes = HashMap<String, Any>()

    /**
     * Adds attributes to the conversation.
     *
     * @param attributes the conversation attributes
     */
    fun recipient(attributes: Map<String, Any>) {
        this.attributes.putAll(attributes)
    }

    /**
     * Adds a User-Participant to the conversation.
     *
     * @param config the renderables that this user contains
     */
    fun user(config: CanonDSLParser.CanonDSL.() -> Unit) {
        val renderables = CanonDSLParser.parse(config)
        participants.add(User(And(renderables)))
    }

    /**
     * Adds a GAIA-Participant to the conversation.
     *
     * @param config the renderables that this gaia participant contains
     */
    fun gaia(config: CanonDSLParser.CanonDSL.() -> Unit) {
        val renderables = CanonDSLParser.parse(config)
        participants.add(Gaia(And(renderables)))
    }

}