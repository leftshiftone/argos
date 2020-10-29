package argos.runtime.xml.strategy.markup

import argos.core.conversation.Json
import canon.api.IRenderable
import canon.parser.xml.strategy.AbstractParseStrategy
import org.w3c.dom.Node

class JsonStrategy : AbstractParseStrategy<IRenderable>() {
    override fun parse(node: Node, factory: (Node) -> List<IRenderable>): IRenderable {
        return Json(node.textContent)
    }
}