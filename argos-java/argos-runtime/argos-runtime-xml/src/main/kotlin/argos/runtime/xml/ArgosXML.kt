package argos.runtime.xml

import argos.api.ArgosOptions
import argos.api.Error
import argos.api.IAssertion
import argos.api.IAssertionResult
import argos.core.assertion.IntentAssertion
import argos.core.assertion.IntentAssertionSpec
import argos.runtime.xml.support.XmlParser
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.nio.charset.StandardCharsets
import java.util.concurrent.CopyOnWriteArrayList

// TODO: javadoc
class ArgosXML private constructor(private val name: String, private val options: ArgosOptions) {

    private val assertions = CopyOnWriteArrayList<IAssertion>()

    companion object {
        fun argos(name: String, options: ArgosOptions, config: ArgosXML.() -> Unit): Publisher<IAssertionResult> {
            val xml = ArgosXML(name, options).apply(config)
            return Flowable.defer {
                options.getListeners().forEach { it.onBeforeAssertions() }
                Flowable.fromIterable(xml.assertions)
            }
            .doOnNext { assertion ->
                options.getListeners().forEach { it.onBeforeAssertion(assertion) }
            }
            .flatMap { assertion ->
                Flowable.fromPublisher(assertion.assert(options))
                        .map { Pair(assertion, it) }
                        .onErrorReturn {Pair(assertion, Error(it))}
            }
            .doOnNext { result ->
                options.getListeners().forEach { it.onAfterAssertion(result.first, result.second) }
            }
            .doOnComplete {
                options.getListeners().forEach { it.onAfterAssertions() }
            }
            .map { it.second }
        }
    }

    fun loadFlatFormat(name: String, startNode: String) : List<IAssertion> {
        val xmlParser = XmlParser(this::class.java.classLoader.getResourceAsStream("argos.xsd"))
        val document = xmlParser.invoke(this::class.java.classLoader.getResourceAsStream(name))
        return xmlParser.parse(xmlParser.getStartNode(document, startNode))
    }

    fun assertIntent(assertion: IAssertion) = assertions.add(assertion)


}
