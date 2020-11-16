package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.ClassificationAssertion
import argos.core.assertion.ClassificationAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.NodeList

class ClassificationAssertionParser: AbstractAssertionParser() {

    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.map {
            val text = it.textContent
            val class_ = it.findAttr("class").get()

            ClassificationAssertion(ClassificationAssertionSpec(text, class_))
        }
    }
}