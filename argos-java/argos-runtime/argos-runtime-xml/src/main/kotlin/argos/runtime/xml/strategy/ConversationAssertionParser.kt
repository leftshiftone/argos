package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.ConversationAssertion
import argos.core.assertion.ConversationAssertionSpec
import argos.core.conversation.*
import argos.runtime.xml.ArgosXML
import argos.runtime.xml.strategy.markup.JsonStrategy
import argos.runtime.xml.strategy.markup.MarkupParser
import argos.runtime.xml.support.*
import canon.api.IRenderable
import canon.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sun.org.apache.bcel.internal.classfile.Code
import jdk.nashorn.internal.parser.JSONParser
import org.apache.commons.lang3.BooleanUtils
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.awt.TextArea
import java.io.File
import java.io.FileInputStream
import java.util.*

class ConversationAssertionParser(val includeFolder: File? = null) : AbstractAssertionParser() {

    private val parser = MarkupParser()

    override fun parse(node: Node): IAssertion {
        val participants = mutableListOf<AbstractParticipant>()
        val attributes = mutableMapOf<String, Any>()

        for (child in node.childNodes.toList()) {
            when(child.nodeName) {
                "user", "gaia" -> participants.add(getParticipant(child))
                "reception" -> attributes.putAll(getAttributes(child))
                "include" -> participants.addAll(getInclude(child, includeFolder!!.path))
            }
        }
        return ConversationAssertion(ConversationAssertionSpec(participants, attributes))
    }

    private fun getParticipant(node: Node): AbstractParticipant {
        return when (node.nodeName) {
            "user" -> User(getGatter(node.childNodes))
            "gaia" -> Gaia(getGatter(node.childNodes))
            else -> throw RuntimeException("cannot parse participant " + node.nodeName)
        }
    }

    private fun getInclude(node: Node, includeFolder: String): List<AbstractParticipant> {
        val fileName = node.attributes.getNamedItem("href").textContent

        val parsedAssertions = ArgosXML.parse(FileInputStream(File("$includeFolder/$fileName")))
        val participants = parsedAssertions.getAllAssertions()
                .filterIsInstance<ConversationAssertion>()
                .flatMap { it.spec.participants }

        return participants
    }

    private fun getGatter(list: NodeList): AbstractGatter {
        if (list.length == 0) return And(getRenderables(list))
        for (i in 0 until list.length) {
            val node = list.item(i)
            val nodeName = node.nodeName
            val children = node.childNodes

            if (nodeName == "#text") {
                continue
            }
            if (nodeName == "and") {
                val regex = getBooleanAttribute(node, "regex")
                return And(getRenderables(children), regex)
            }
            if (nodeName == "or") {
                val regex = getBooleanAttribute(node, "regex")
                return Or(getRenderables(children), regex)
            }
            if (nodeName == "context") {
                return Context(getRenderables(children))
            }
            if (nodeName == "log") {
                return Log(getRenderables(children))
            }
            return if (nodeName == "notification") {
                Notification(getRenderables(children))
            } else And(getRenderables(list))
        }
        return And(emptyList())
    }

    private fun getRenderables(list: NodeList): List<IRenderable> {
        val renderables: ArrayList<IRenderable> = ArrayList<IRenderable>()
        for (i in 0 until list.length) {
            val node = list.item(i)
            parser.toRenderable(node, renderables)
        }
        return renderablesNullCheck(renderables)
    }

    private fun renderablesNullCheck(renderables: List<IRenderable>?): List<IRenderable> {
        fun String?.nullIfEmpty() = this?.ifEmpty { null }

        if (renderables == null)
            return emptyList()

        return renderables.map {
            when (it) {
                is Basket -> Basket(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.required, renderablesNullCheck(it.renderables))
                is Block -> Block(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Bold -> Bold(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.text.nullIfEmpty())
                is Break -> Break(it.id.nullIfEmpty(), it.`class`.nullIfEmpty())
                is Button -> Button(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.text.nullIfEmpty(), it.name.nullIfEmpty(), it.value.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Camera -> Camera(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.required, it.maxCompressSize, renderablesNullCheck(it.renderables))
                is Carousel -> Carousel(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.text.nullIfEmpty(), it.name.nullIfEmpty(), it.selected, renderablesNullCheck(it.renderables))
                is Choice -> Choice(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.text.nullIfEmpty(), it.selected.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is CodeReader -> CodeReader(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.format.nullIfEmpty())
                is Col -> Col(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Container -> Container(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Email -> Email(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.placeholder.nullIfEmpty(), it.required, it.name.nullIfEmpty(), it.value.nullIfEmpty())
                is Foreach -> Foreach(it.forEachStmt.nullIfEmpty(), if (it.renderable != null) renderablesNullCheck(listOf(it.renderable!!))[0] else null)
                is Form -> Form(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Headline -> Headline(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.text.nullIfEmpty())
                is If -> If(it.expression.nullIfEmpty(), if (it.renderable != null) renderablesNullCheck(listOf(it.renderable!!))[0] else null)
                is Image -> Image(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.src.nullIfEmpty(), it.width.nullIfEmpty(), it.height.nullIfEmpty(), it.alt.nullIfEmpty())
                is Italic -> Italic(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.text.nullIfEmpty())
                is Item -> Item(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Items -> Items(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.ordered, renderablesNullCheck(it.renderables))
                is Label -> Label(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.text.nullIfEmpty())
                is Link -> Link(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.value.nullIfEmpty(), it.text.nullIfEmpty())
                is canon.model.Map -> canon.model.Map(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.src.nullIfEmpty(), it.mapType.nullIfEmpty(), it.centerLng.nullIfEmpty(), it.centerLat.nullIfEmpty(), it.markerIcon.nullIfEmpty(), it.selectedMarkerIcon.nullIfEmpty(), it.routeStartIcon.nullIfEmpty(), it.routeEndIcon.nullIfEmpty(), it.routePoints.nullIfEmpty(), it.centerBrowserLocation, it.required, it.zoom.nullIfEmpty(), it.maxSelections)
                is MultipleChoice -> MultipleChoice(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.sieve, it.required, renderablesNullCheck(it.renderables))
                is Overlay -> Overlay(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.trigger.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Overlays -> Overlays(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.trigger.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Phone -> Phone(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.placeholder.nullIfEmpty(), it.required, it.name.nullIfEmpty(), it.value.nullIfEmpty())
                is Reel -> Reel(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is ReelValue -> ReelValue(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.value.nullIfEmpty(), it.valueType.nullIfEmpty())
                is Row -> Row(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Selectable -> Selectable(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Selection -> Selection(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.countdownInSec, renderablesNullCheck(it.renderables))
                is SelectionItem -> SelectionItem(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is SingleChoice -> SingleChoice(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.sieve, it.required, renderablesNullCheck(it.renderables))
                is Slider -> Slider(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.min, it.max, it.step, it.value, it.name.nullIfEmpty(), it.values.nullIfEmpty())
                is SlotMachine -> SlotMachine(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Spinner -> Spinner(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.min, it.max, it.step, it.value, it.name.nullIfEmpty())
                is Submit -> Submit(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.text.nullIfEmpty(), it.name.nullIfEmpty())
                is Suggestion -> Suggestion(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.text.nullIfEmpty(), it.name.nullIfEmpty(), it.value.nullIfEmpty())
                is Table -> Table(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Text -> Text(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.regex.nullIfEmpty(), it.placeholder.nullIfEmpty(), it.required, it.name.nullIfEmpty(), it.value.nullIfEmpty())
                is Textarea -> Textarea(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.placeholder.nullIfEmpty(), it.name.nullIfEmpty(), it.value.nullIfEmpty(), it.required, it.rows, it.cols)
                is Transition -> Transition(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.direction.nullIfEmpty(), it.wrapped.nullIfEmpty(), renderablesNullCheck(it.renderables))
                is Trigger -> Trigger(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.name.nullIfEmpty(), it.text.nullIfEmpty())
                is Upload -> Upload(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.accept.nullIfEmpty(), it.name.nullIfEmpty(), it.text.nullIfEmpty(), it.maxSize, it.maxCompressSize, it.required)
                is Video -> Video(it.id.nullIfEmpty(), it.`class`.nullIfEmpty(), it.src.nullIfEmpty())
                else -> it
            }
        }
    }

    private fun getBooleanAttribute(node: Node, name: String): Boolean {
        val attr = node.attributes.getNamedItem(name)
        return attr != null && BooleanUtils.toBoolean(attr.textContent)
    }

    private fun getAttributes(node: Node): Map<String, Any> {
        return node.childNodes
                .filter { "json".equals(it.nodeName) }
                .map { ObjectMapper().readValue<Map<String, Any>>(it.textContent) }
                .firstOrNull() ?: emptyMap()
    }
}