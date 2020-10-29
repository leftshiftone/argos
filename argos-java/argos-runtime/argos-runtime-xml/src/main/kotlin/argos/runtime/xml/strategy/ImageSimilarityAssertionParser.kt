package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.ImageSimilarityAssertion
import argos.core.assertion.ImageSimilarityAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.toList
import org.w3c.dom.NodeList

class ImageSimilarityAssertionParser : AbstractAssertionParser() {
    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.toList().map {
            val image1 = it.findAttr("image1").get()
            val image2 = it.findAttr("image2").get()
            val threshold = it.findAttr("threshold").get().toFloat()

            return@map ImageSimilarityAssertion(ImageSimilarityAssertionSpec(image1, image2, threshold))
        }
    }

}