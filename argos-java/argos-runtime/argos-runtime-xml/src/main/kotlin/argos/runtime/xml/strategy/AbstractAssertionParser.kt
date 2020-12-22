package argos.runtime.xml.strategy

import argos.api.IAssertion
import org.w3c.dom.Node

/**
 * Classes which implements this abstract class can be used to parse a DOM-Node for an assertion tag.
 */
abstract class AbstractAssertionParser {
    /**
     * Parse a node instance for an assertion tag.
     *
     * @param node a DOM-Node which holds a defined assertion tag
     *
     * @return the parsed <code>IAssertion</code> instance
     */
    abstract fun parse(node: Node): IAssertion
}