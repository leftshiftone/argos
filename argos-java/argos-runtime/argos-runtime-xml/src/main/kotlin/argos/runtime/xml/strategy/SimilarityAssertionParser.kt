package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.SimilarityAssertion
import argos.core.assertion.SimilarityAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.NodeList

class SimilarityAssertionParser : AbstractAssertionParser() {

    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.map {
            val threshold: Float = it.findAttr("threshold").get().toFloat()
            val texts: List<String> = it.childNodes.map { it.textContent }
            val text1: String = texts.get(0)
            val text2: String = texts.get(1)

            SimilarityAssertion(SimilarityAssertionSpec(text1, text2, threshold))
        }
    }

}