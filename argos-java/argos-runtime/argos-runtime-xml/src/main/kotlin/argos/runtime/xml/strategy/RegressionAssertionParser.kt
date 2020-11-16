package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.RegressionAssertion
import argos.core.assertion.RegressionAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.NodeList

class RegressionAssertionParser: AbstractAssertionParser() {

    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.map {
            val text = it.textContent
            val score = it.findAttr("score").get().toFloat()

            RegressionAssertion(RegressionAssertionSpec(text, score))
        }
    }
}