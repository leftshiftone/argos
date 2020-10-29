package argos.runtime.xml.strategy.markup

import JsonSubmitStrategy
import canon.api.IRenderable
import canon.parser.xml.CanonXmlParser
import canon.parser.xml.strategy.AbstractParseStrategy
import canon.parser.xml.strategy.LabelStrategy
import java.util.*

class MarkupParser(customStrategies: (String) -> Optional<AbstractParseStrategy<IRenderable>> = { Optional.empty() }) : CanonXmlParser(customStrategies = {
    Optional.ofNullable(customStrategies(it).orElseGet {
        when (it) {
            "label" -> LabelStrategy()
            "#text" -> LabelStrategy(true)
            "#comment" -> NoOpStrategy()
            "context" -> NoOpStrategy()
            "notification" -> NoOpStrategy()
            "and" -> NoOpStrategy()
            "or" -> NoOpStrategy()
            "button" -> ImplicitResultButtonStrategy()
            "submit" -> JsonSubmitStrategy()
            "json" -> JsonStrategy()
            "assertJson" -> AssertJsonStrategy()
            else -> null
        }
    })
})