package argos.core.listener.support

import argos.api.AssertionGroup
import argos.api.Error
import argos.api.Failure
import argos.api.Success
import org.w3c.dom.Document
import org.w3c.dom.Node

data class AssertionTestcase(val assertionGroup: AssertionGroup): ArrayList<AssertionTestResult>() {

    fun createNode(doc: Document): Node {
        val element = doc.createElement("testcase")

        // Name-Attribute
        element.setAttribute("name", assertionGroup.name
            ?: assertionGroup.assertions.get(0)::class.java.simpleName)

        // Time-Attribute
        var time: Long = 0
        forEach { time += it.timeMillis }
        element.setAttribute("time",  time.toString())

        // Assertions-Attribute
        element.setAttribute("assertions", size.toString())

        // Result-Elemente
        forEach {
            val child: Node? = when(it.result) {
                is Success -> null
                is Failure -> {
                    val fail = doc.createElement("failure")
                    fail.textContent = it.msg
                    fail
                }
                is Error -> {
                    val err = doc.createElement("error")
                    err.textContent = it.msg
                    err
                }
                else -> null
            }
            if (child != null) element.appendChild(child)
        }

        return element
    }

    override fun add(element: AssertionTestResult): Boolean {
        if (assertionGroup.assertions.contains(element.assertion)) {
            if (super.contains(element))
                return false
            return super.add(element)
        }
        else
            return false
    }

    override fun addAll(elements: Collection<AssertionTestResult>): Boolean {
        elements.forEach {
            super.add(it)
        }
        return true
    }
}