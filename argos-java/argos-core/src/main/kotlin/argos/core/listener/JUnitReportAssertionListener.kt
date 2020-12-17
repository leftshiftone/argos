package argos.core.listener

import argos.api.AssertionGroup
import argos.api.IAssertion
import argos.api.IAssertionListener
import argos.api.IAssertionResult
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

class JUnitReportAssertionListener: IAssertionListener {
    private val logger: Logger = LoggerFactory.getLogger(JUnitReportAssertionListener::class.java)
    private val logPath = File("outputFolder") // TODO: change output folder
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
            val testfile = File(logPath, "${testsuite!!.name}_$timestamp.xml")

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