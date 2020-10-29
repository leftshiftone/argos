package argos.runtime.dsl.config

import argos.core.conversation.AbstractParticipant

class Conversation {

    val participants = ArrayList<AbstractParticipant>()
    val attributes = HashMap<String, Any>()

    fun recipient(attributes: Map<String, Any>) {
        this.attributes.putAll(attributes)
    }

    fun user() {
        // TODO: use canon
    }

    fun gaia() {
        // TODO: use canon
    }

}