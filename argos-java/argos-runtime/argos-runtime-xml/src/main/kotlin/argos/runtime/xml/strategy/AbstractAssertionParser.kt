package argos.runtime.xml.strategy

import argos.api.IAssertion
import org.w3c.dom.NodeList

abstract class AbstractAssertionParser {

    abstract fun parse(nodeList: NodeList):List<IAssertion>

}