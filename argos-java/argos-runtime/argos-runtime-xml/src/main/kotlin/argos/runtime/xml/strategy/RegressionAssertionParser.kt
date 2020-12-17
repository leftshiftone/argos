package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.RegressionAssertion
import argos.core.assertion.RegressionAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class RegressionAssertionParser: AbstractAssertionParser() {

    override fun parse(node: Node): IAssertion {
        val text = node.textContent
        val score = node.findAttr("score").get().toFloat()

        return RegressionAssertion(RegressionAssertionSpec(text, score))
    }
}