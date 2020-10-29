package argos.runtime.xml.strategy.markup

import canon.api.IRenderable
import canon.model.Button
import canon.parser.xml.strategy.ButtonStrategy
import org.w3c.dom.Node

class ImplicitResultButtonStrategy : ButtonStrategy() {

    override fun parse(node: Node, factory: (Node) -> List<IRenderable>): Button {
        val bt = super.parse(node, factory)
        var name = getNameFromNode(node)
        if (name == null) name = "result"
        return Button(bt.id, bt.`class`, bt.text, name, bt.value, bt.renderables)
    }

    private fun getNameFromNode(node: Node): String? {
        if (node.attributes == null) return null
        val attribute = node.attributes.getNamedItem("name")
        return if (attribute == null || attribute.textContent == null) null else attribute.textContent
    }
}