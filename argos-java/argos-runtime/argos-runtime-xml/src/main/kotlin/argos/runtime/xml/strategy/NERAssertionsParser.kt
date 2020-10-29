package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.NERAssertion
import argos.core.assertion.NERAssertionSpec
import argos.runtime.xml.support.findAll
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.toList
import org.w3c.dom.NodeList

class NERAssertionsParser : AbstractAssertionParser() {

    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.toList().map {
            val text: String = it.findAttr("text").get()
            val entities = it.findAll("entity")
            val entityList = ArrayList<NERAssertionSpec.Entity>()
            for (entity in entities) {
                val entityText: String = entity.textContent
                val label: String = entity.attributes.getNamedItem("label")?.textContent!!
                val index: Int? = entity.attributes.getNamedItem("index")?.textContent?.toIntOrNull()
                val not: Boolean = entity.attributes.getNamedItem("not")?.textContent?.equals("true") ?: false

                entityList.add(NERAssertionSpec.Entity(label, entityText, index, not))
            }
            NERAssertion(NERAssertionSpec(text, entityList))
        }
    }

}