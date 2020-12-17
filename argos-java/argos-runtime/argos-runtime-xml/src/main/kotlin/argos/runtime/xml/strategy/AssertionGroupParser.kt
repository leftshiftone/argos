package argos.runtime.xml.strategy

import argos.api.AssertionGroup
import argos.runtime.xml.ArgosXML
import argos.runtime.xml.support.findAttr
import org.w3c.dom.Node
import java.io.File

class AssertionGroupParser(val include: File? = null) {
    fun parse(node: Node): AssertionGroup {
        return AssertionGroup(node.findAttr("name").get(),
                ArgosXML.parseNodeList(node.childNodes, include).flatMap { it.assertions })
    }
}