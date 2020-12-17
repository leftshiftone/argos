package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.SemanticSearchAssertion
import argos.core.assertion.SemanticSearchAssertionSpec
import argos.runtime.xml.support.findAll
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class SemanticSearchAssertionParser: AbstractAssertionParser() {
    override fun parse(node: Node): IAssertion {
        val text = node.findAttr("text").get()
        val topN = node.findAttr("topN").get().toInt()
        val entries = node.findAll("entry")
        val entryList = ArrayList<SemanticSearchAssertionSpec.Entry>()
        for (entry in entries) {
            val id = entry.findAttr("id").get()
            val score = entry.findAttr("score").get().toFloat()

            entryList.add(SemanticSearchAssertionSpec.Entry(id, score))
        }

        return SemanticSearchAssertion(SemanticSearchAssertionSpec(text, topN, entryList))
    }
}