package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.ImageAssertion
import argos.core.assertion.ImageAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class ImageAssertionParser: AbstractAssertionParser() {

    override fun parse(node: Node): IAssertion {
        val skill = node.findAttr("skill").get()
        val source = node.findAttr("source").get()
        val target = node.findAttr("target").get()

        return ImageAssertion(ImageAssertionSpec(skill, source, target))
    }
}