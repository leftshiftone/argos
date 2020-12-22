package argos.core.listener.support

import argos.api.Error
import argos.api.Failure
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.text.SimpleDateFormat

/**
 * This class holds all the assertion testcases that were executed in this test.
 *
 * @param name the name of this test
 */
data class AssertionTestsuite(val name: String): ArrayList<AssertionTestcase>() {
    /**
     * Create a 'testsuite'-node for a JUnit XML-Report
     *
     * @param doc the parent document
     *
     * @return a <code>Node</code> instance which holds the testcases for this test
     */
    fun createNode(doc: Document): Node {
        val element = doc.createElement("testsuite")
        element.setAttribute("name", name)
        element.setAttribute("tests", flatMap { it }.map { it.result }.size.toString())
        element.setAttribute("failures", flatMap { it }.map { it.result }.filterIsInstance<Failure>().size.toString())
        element.setAttribute("errors", flatMap { it }.map { it.result }.filterIsInstance<Error>().size.toString())
        element.setAttribute("timestamp", SimpleDateFormat("yyyy-MM-dd'T'HH:mm::ss.SSSZ").format(System.currentTimeMillis()))
        element.setAttribute("hostname", "localhost")
        var time: Long = 0
        forEach { it.forEach { time += it.timeMillis } }
        element.setAttribute("time",  time.toString())
        forEach { element.appendChild(it.createNode(doc)) }
        return element
    }

    override fun add(element: AssertionTestcase): Boolean {
        if (contains(element))
            return false
        else
            return super.add(element)
    }
}