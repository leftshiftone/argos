package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.ClassificationAssertion
import argos.core.assertion.ClassificationAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class ClassificationAssertionParser: AbstractAssertionParser() {

    override fun parse(node: Node): IAssertion {
        val text = node.textContent
        val `class` = node.findAttr("class").get()

        return ClassificationAssertion(ClassificationAssertionSpec(text, `class`))
    }
}