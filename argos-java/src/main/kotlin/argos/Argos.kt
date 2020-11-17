package argos

import argos.api.ArgosOptions
import argos.api.IAssertionResult
import argos.runtime.xml.ArgosXML
import de.swirtz.ktsrunner.objectloader.KtsObjectLoader
import de.swirtz.ktsrunner.objectloader.LoadException
import gaia.sdk.HMACCredentials
import gaia.sdk.core.GaiaConfig
import io.reactivex.Flowable
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.reactivestreams.Publisher
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

class Argos {

    companion object {

        @JvmStatic
        fun main(args:Array<String>) {
            val ret: MutableList<String> = mutableListOf()
            if(args.isNotEmpty()) {
                args.forEach { arg ->
                    when {
                        arg.endsWith(".xml") -> handleXml(ret, arg)
                        arg.endsWith(".kts") -> handleDsl(ret, arg)
                        else -> handleScript(ret, arg)
                    }
                }
            }
            else
                println("no arguments")

            println(ret.toString())
        }

        // TODO: parse url, apiKey and apiSecret from args
        private fun handleXml(ret: MutableList<String>, arg: String) {
            val parsedAssertions = ArgosXML.parse(FileInputStream(File(arg)))
            val options = ArgosOptions(parsedAssertions.identityId, GaiaConfig("", HMACCredentials("", "")))

            parsedAssertions.assertionList
                    .map { it.assert(options) }
                    .forEach { Flowable.fromPublisher(it).blockingForEach { ret.add(it.getMessage())} }
        }

        // TODO: parse url, apiKey and apiSecret from args
        private fun handleDsl(ret: MutableList<String>, arg: String) {
            setIdeaIoUseFallback()
            val scriptReader = Files.newBufferedReader(Paths.get(arg))
            val script: Publisher<IAssertionResult> = KtsObjectLoader().load(scriptReader)
            Flowable.fromPublisher(script).forEach { ret.add(it.getMessage()) }
        }

        // TODO: parse url, apiKey and apiSecret from args
        private fun handleScript(ret: MutableList<String>, arg: String) {
            setIdeaIoUseFallback()
            val script: Publisher<IAssertionResult> =
                    try {
                        KtsObjectLoader().load(arg)
                    } catch (ex: LoadException) {
                        try {
                            KtsObjectLoader().load(
                                    "import argos.api.ArgosOptions\n" +
                                            "import argos.runtime.dsl.ArgosDSL\n" +
                                            "import gaia.sdk.HMACCredentials\n" +
                                            "import gaia.sdk.core.GaiaConfig\n" +
                                            arg)
                        } catch (ex: LoadException) {
                            KtsObjectLoader().load(
                                    "import argos.api.ArgosOptions\n" +
                                            "import argos.runtime.dsl.ArgosDSL\n" +
                                            "import gaia.sdk.HMACCredentials\n" +
                                            "import gaia.sdk.core.GaiaConfig\n" +
                                            "ArgosDSL.argos(\"argos test\", ArgosOptions(\"\", " +
                                            "GaiaConfig(\"\", HMACCredentials(\"\", \"\")))) { " +
                                            arg + "}")
                        }
                    }
            Flowable.fromPublisher(script).forEach { ret.add(it.getMessage()) }
        }

    }

}