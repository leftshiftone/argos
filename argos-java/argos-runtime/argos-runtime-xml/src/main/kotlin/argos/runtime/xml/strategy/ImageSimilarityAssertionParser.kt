package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.ImageSimilarityAssertion
import argos.core.assertion.ImageSimilarityAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.toList
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class ImageSimilarityAssertionParser : AbstractAssertionParser() {
    override fun parse(node: Node): IAssertion {
        val image1 = node.findAttr("image1").get()
        val image2 = node.findAttr("image2").get()
        val threshold = node.findAttr("threshold").get().toFloat()

        return ImageSimilarityAssertion(ImageSimilarityAssertionSpec(image1, image2, threshold))
    }

}