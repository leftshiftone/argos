package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.ImageAssertion
import argos.core.assertion.ImageAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.NodeList

class ImageAssertionParser: AbstractAssertionParser() {

    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.map {
            val skill = it.findAttr("skill").get()
            val source = it.findAttr("source").get()
            val target = it.findAttr("target").get()

            ImageAssertion(ImageAssertionSpec(skill, source, target))
        }
    }
}