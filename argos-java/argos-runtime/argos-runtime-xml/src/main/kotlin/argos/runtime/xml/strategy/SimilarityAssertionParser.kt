package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.SimilarityAssertion
import argos.core.assertion.SimilarityAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class SimilarityAssertionParser : AbstractAssertionParser() {

    override fun parse(node: Node): IAssertion {
        val threshold: Float = node.findAttr("threshold").get().toFloat()
        val texts: List<String> = node.childNodes.map { it.textContent }
        val text1: String = texts.get(0)
        val text2: String = texts.get(1)

        return SimilarityAssertion(SimilarityAssertionSpec(text1, text2, threshold))
    }

}