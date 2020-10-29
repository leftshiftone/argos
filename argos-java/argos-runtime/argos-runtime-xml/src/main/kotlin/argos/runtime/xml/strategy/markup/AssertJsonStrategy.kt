package argos.runtime.xml.strategy.markup

import argos.core.conversation.AssertJson
import canon.api.IRenderable
import canon.parser.xml.strategy.AbstractParseStrategy
import org.w3c.dom.Node

class AssertJsonStrategy : AbstractParseStrategy<IRenderable>() {
    override fun parse(node: Node, factory: (Node) -> List<IRenderable>): IRenderable {
        return AssertJson(node.textContent, node.attributes.getNamedItem("path").textContent)
    }
}