package argos.core.assertion

import org.junit.jupiter.api.Test

class ConversationAssertionTest {

    @Test
    fun test() {
        val convo: MutableList<Conversation.Element> = emptyList<Conversation.Element>().toMutableList()
        val gaia = Conversation.create(Conversation.Type.GAIA,
                Conversation.Property.Text(
                        "Hallo, ich bin Ihr digitaler UBIT-Assistent. Wollen Sie Ihren Digitalisierungs-Index in 7 Minuten" +
                                "ermitteln oder wollen Sie direkt einen UBIT-Profi über die Trendit Roadmap suchen?"),
                Conversation.Property.Button(textContent = "Ermittle Digitalisierungs-Index", name = "result", value = "digicheck"),
                Conversation.Property.Button(textContent = "Suche UBIT-Profi", name = "result", value = "prozess"))
        convo.add(gaia)
        val user = Conversation.create(Conversation.Type.USER,
                Conversation.Property.Button(textContent = "Ermittle Digitalisierungs-Index", position = "right", value = "digicheck", name = "result"))
        convo.add(user)

        convo.forEach { it.printElement() }
    }
}