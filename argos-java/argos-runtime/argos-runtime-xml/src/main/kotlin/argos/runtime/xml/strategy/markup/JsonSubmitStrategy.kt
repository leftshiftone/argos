import argos.core.conversation.Json
import canon.api.IRenderable
import canon.model.Submit
import canon.parser.xml.strategy.SubmitStrategy
import org.w3c.dom.Node

class JsonSubmitStrategy() : SubmitStrategy() {
    override fun parse(node: Node, factory: (Node) -> List<IRenderable>): Submit {
        val submit = super.parse(node, factory)
        val json = factory.invoke(node).filter { it is Json }.firstOrNull() as Json?
                ?: return Submit(submit.id, submit.`class`, submit.text, submit.name)
        val jsonText = json.text.trim { it <= ' ' }.replace("\n".toRegex(), "")
        val text = if (node.attributes.getNamedItem("name") == null) jsonText else "{\"" + node.attributes.getNamedItem("name").nodeValue + "\": " + jsonText + "}"
        return Submit(submit.id, submit.`class`, text, submit.name)
    }
}