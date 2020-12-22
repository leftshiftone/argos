package argos.core.listener

import argos.api.*
import argos.core.listener.support.AssertionTestResult
import argos.core.listener.support.AssertionTestcase
import argos.core.listener.support.AssertionTestsuite
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.text.SimpleDateFormat
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.math.log

/**
 * This class implements the IAssertion-Interface to create JUnit XML-Reports for an argos test.
 * This listener has to be added to an ArgosOptions instance to be executed in an argos test.
 *
 * @see ArgosOptions.addListener
 */
class JUnitReportAssertionListener(private val logPath: String = "build/reports"): IAssertionListener {
    private val logger: Logger = LoggerFactory.getLogger(JUnitReportAssertionListener::class.java)
    private var testsuite: AssertionTestsuite? = null
    private val groupMap = mutableMapOf<AssertionGroup, MutableList<AssertionTestResult>>()

    override fun onBeforeAssertions(name: String) {
        testsuite = AssertionTestsuite(name)
    }

    override fun onAfterAssertionGroup(assertionGroup: AssertionGroup) {
        assertionGroup.assertions.forEach { assertion ->
            for (result in testResults) {
                if (assertion === result.assertion) {
                    if (groupMap[assertionGroup]?.contains(result) != true)
                        groupMap[assertionGroup]?.add(result) ?: groupMap.put(assertionGroup, mutableListOf(result))
                }
            }
        }
    }

    override fun onAfterAssertions() {
        groupMap.forEach { (group, tests) ->
            val testcase = AssertionTestcase(group)
            testcase.addAll(tests)
            testsuite!!.add(testcase)
        }

        if (testsuite != null) {
            val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = builder.newDocument()

            doc.appendChild(testsuite!!.createNode(doc))

            val transformer = TransformerFactory.newInstance().newTransformer()
            val domSource = DOMSource(doc)

            val timestamp = SimpleDateFormat("yyyy-MM-dd--HH-mm-ss").format(System.currentTimeMillis())
            var testfile = File(logPath, "${testsuite!!.name}_$timestamp.xml")

            var i = 0
            while (testfile.exists()) {
                testfile = File(logPath, "${testsuite!!.name}_${timestamp}_${++i}.xml")
            }

            val streamResult = StreamResult(testfile)

            transformer.transform(domSource, streamResult)

            logger.info("Report created at ${testfile.absolutePath}")
        }
    }

    companion object {
        private val testResults = mutableListOf<AssertionTestResult>()

        fun logResult(assertion: IAssertion, result: IAssertionResult, msg: String, time: Long) {
            testResults.add(AssertionTestResult(assertion, result, time, msg))
        }
    }
}