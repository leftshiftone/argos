package argos.runtime.xml.support

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// TODO: javadoc

fun <T>NodeList.map(function: (Node) -> T):List<T> {
    return IntRange(0, this.length - 1)
            .flatMap {
                when (this.item(it).nodeName) {
                    "#text" -> emptyList()
                    else -> listOf(function(this.item(it)))
                }
    }
}

fun NodeList.toList(): List<Node> {
    return IntRange(0, this.length - 1).map { this.item(it) }
}

fun NodeList.filter(function: (Node) -> Boolean): List<Node> {
    return IntRange(0, this.length - 1)
            .map { this.item(it) }
            .filter { e -> function(e) }
}

fun Node.find(tag: String): Node {
    val nodeList = this.childNodes
    for (i in 0 until nodeList.length) {
        if (nodeList.item(i).nodeName === tag) {
            return nodeList.item(i)
        }
    }
    throw RuntimeException("An error occurred while parsing GQL file. Check validity.")
}

fun Node.findAll(vararg tags: String): List<Node> {
    val result = ArrayList<Node>()
    val nodeList = this.childNodes
    for (i in 0 until nodeList.length - 1) {
        val nodeName = nodeList.item(i).nodeName
        if (nodeName in tags) {
            result.add(nodeList.item(i))
        }
    }
    return result
}

fun Node.findAttr(name: String): Optional<String> {
    val attrNode = this.attributes.getNamedItem(name)
    return if (attrNode != null) Optional.of(attrNode.textContent) else Optional.empty()
}

fun Node.findText(tag: String): String {
    return this.find(tag).textContent
}

fun Node.toAttributes(): Map<String, String> {
    val result = HashMap<String, String>()
    for (i in 0 until (this.attributes?.length?:0)) {
        val attribute = this.attributes.item(i)
        result.put(attribute.nodeName, attribute.nodeValue)
    }
    return result
}
