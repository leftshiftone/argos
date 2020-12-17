package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.SentimentAssertion
import argos.core.assertion.SentimentAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.toList
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class SentimentAssertionParser : AbstractAssertionParser() {
    override fun parse(node: Node): IAssertion {
        val text = node.textContent
        val type = node.findAttr("type").get()

        return SentimentAssertion(SentimentAssertionSpec(text, type))
    }

}