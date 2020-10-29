/*
 * Copyright (c) 2016-2020, Leftshift One
 * __________________
 * [2020] Leftshift One
 * All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains
 * the property of Leftshift One and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Leftshift One
 * and its suppliers and may be covered by Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Leftshift One.
 */
package argos.runtime.xml.strategy.markup

import canon.api.IRenderable
import canon.model.Button
import canon.parser.xml.strategy.ButtonStrategy
import org.w3c.dom.Node

class EncodedValueButtonStrategy() : ButtonStrategy() {

    override fun parse(node: Node, factory: (Node) -> List<IRenderable>): Button {
        val bt = super.parse(node, factory)
        val value = getValueFromNode(node)
        return Button(bt.id, bt.`class`, bt.text, bt.name, value, bt.renderables)
    }

    private fun getValueFromNode(node: Node): String? {
        if (node.attributes == null) return null
        val attribute = node.attributes.getNamedItem("value")
        return if (attribute == null || attribute.textContent == null) null else attribute.textContent
    }
}