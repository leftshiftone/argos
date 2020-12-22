package argos.core.assertion

import argos.core.conversation.AbstractParticipant

/**
 * This class holds the necessary specifications for a conversation assertion to perform.
 *
 * @param participants a list of the participants of this conversation
 * @param attributes add reception attributes to the conversation assertion
 */
data class ConversationAssertionSpec(val participants: List<AbstractParticipant>, val attributes: Map<String, Any>)