package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.conversation.*
import argos.runtime.xml.strategy.markup.MarkupParser
import canon.api.IRenderable
import org.apache.commons.lang3.BooleanUtils
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.util.*

class ConversationAssertionParser : AbstractAssertionParser() {

    private val parser = MarkupParser()

    override fun parse(nodeList: NodeList): List<IAssertion> {
        return emptyList<IAssertion>()
    }

    private fun getParticipant(node: Node): AbstractParticipant {
        return when (node.nodeName) {
            "user" -> User(getGatter(node.childNodes))
            "gaia" -> Gaia(getGatter(node.childNodes))
            else -> throw RuntimeException("cannot parse participant " + node.nodeName)
        }
    }

    private fun getGatter(list: NodeList): AbstractGatter {
        if (list.length == 0) return And(getRenderables(list))
        for (i in 0 until list.length) {
            val node = list.item(i)
            val nodeName = node.nodeName
            val children = node.childNodes

            if (nodeName == "#text") {
                continue
            }
            if (nodeName == "and") {
                val regex = getBooleanAttribute(node, "regex")
                return And(getRenderables(children), regex)
            }
            if (nodeName == "or") {
                val regex = getBooleanAttribute(node, "regex")
                return Or(getRenderables(children), regex)
            }
            if (nodeName == "context") {
                return Context(getRenderables(children))
            }
            if (nodeName == "log") {
                return Log(getRenderables(children))
            }
            return if (nodeName == "notification") {
                Notification(getRenderables(children))
            } else And(getRenderables(list))
        }
        return And(emptyList())
    }

    private fun getRenderables(list: NodeList): List<IRenderable> {
        val renderables: ArrayList<IRenderable> = ArrayList<IRenderable>()
        for (i in 0 until list.length) {
            val node = list.item(i)
            parser.toRenderable(node, renderables)
        }
        return renderables
    }

    private fun getBooleanAttribute(node: Node, name: String): Boolean {
        val attr = node.attributes.getNamedItem(name)
        return attr != null && BooleanUtils.toBoolean(attr.textContent)
    }


}