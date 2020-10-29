package argos.core.assertion

import argos.core.conversation.AbstractParticipant

data class ConversationAssertionSpec(val participants: List<AbstractParticipant>, val attributes: Map<String, Any>)