package argos.core.listener.support

import argos.api.AssertionGroup
import argos.api.Error
import argos.api.Failure
import argos.api.Success
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * This class holds all assertion test results for the given assertion group.
 *
 * @param assertionGroup the assertionGroup instance whose test results are stored in this class
 */
data class AssertionTestcase(val assertionGroup: AssertionGroup): ArrayList<AssertionTestResult>() {
    /**
     * Create a 'testcase'-node for a JUnit XML-Report
     *
     * @param doc the parent document
     *
     * @return a <code>Node</code> instance which holds the test results for this assertion group
     */
    fun createNode(doc: Document): Node {
        val element = doc.createElement("testcase")

        element.setAttribute("name", assertionGroup.name
            ?: assertionGroup.assertions.get(0)::class.java.simpleName)

        var time: Long = 0
        forEach { time += it.timeMillis }
        element.setAttribute("time",  time.toString())

        element.setAttribute("assertions", size.toString())

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