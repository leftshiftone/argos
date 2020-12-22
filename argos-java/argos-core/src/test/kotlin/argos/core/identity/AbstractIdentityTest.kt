package argos.core.identity

import argos.api.ArgosOptions
import argos.api.IAssertionResult
import argos.api.Success
import argos.runtime.xml.ArgosXML
import gaia.sdk.HMACCredentials
import gaia.sdk.core.GaiaConfig
import gaia.sdk.mqtt.MqttSensorQueue
import gaia.sdk.spi.QueueOptions
import io.reactivex.Flowable
import org.junit.jupiter.api.Assertions
import java.io.File
import java.io.FileInputStream

/**
 * Classes which implement this abstract class can be used to perform ArgosIdentityTests.
 *
 * @param testFolder the folder which holds the identity test files
 * @param includesFolder the folder which holds include files for the identity tests
 * @param delayMs a delay between each test
 */
abstract class AbstractIdentityTest(val testFolder: File, val includesFolder: File? = null, val delayMs: Long = 300) {
    /**
     * Runs an identity test.
     *
     * @param name the filename of the test to perform
     */
    protected fun testIdentity(name: String) {
        val parsed = ArgosXML(includesFolder).parse(FileInputStream(File(testFolder, "$name.xml")))
        val identityId = parsed.identityId
        val assertions = parsed.getAllAssertions()

        val options = ArgosOptions(identityId)
        val results = mutableListOf<IAssertionResult>()
        assertions.forEach { assertion ->
            results.add(Flowable.fromPublisher(assertion.assert(options)).blockingFirst())
        }

        results.forEach { result ->
            println("Result message: ${result.getMessage()}")
            Assertions.assertTrue(result is Success)
            Thread.sleep(delayMs)
        }
    }
}