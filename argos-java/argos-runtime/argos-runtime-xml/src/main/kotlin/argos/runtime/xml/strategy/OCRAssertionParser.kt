package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.OCRAssertion
import argos.core.assertion.OCRAssertionSpec
import argos.runtime.xml.support.findAll
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class OCRAssertionParser: AbstractAssertionParser() {

    override fun parse(node: Node): IAssertion {
        val image = node.findAttr("image").get()
        val texts = node.findAll("text")
        val textList = ArrayList<OCRAssertionSpec.Text>()
        for (text in texts) {
            val textContent = text.textContent
            val fuzzy = text.findAttr("fuzzy").get().toBoolean()

            textList.add(OCRAssertionSpec.Text(textContent, fuzzy))
        }
        return OCRAssertion(OCRAssertionSpec(image, textList))
    }

}