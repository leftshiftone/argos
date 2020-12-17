package argos.runtime.xml.strategy

import argos.api.IAssertion
import org.w3c.dom.Node

abstract class AbstractAssertionParser {

    abstract fun parse(node: Node): IAssertion

}