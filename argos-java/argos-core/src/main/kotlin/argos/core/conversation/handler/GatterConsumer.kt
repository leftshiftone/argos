package argos.core.conversation.handler

import argos.api.preprocess
import argos.core.conversation.AbstractGatter
import argos.identity.support.Connection
import canon.model.Button
import canon.model.Label
import canon.model.Submit
import java.util.function.Consumer


class GatterConsumer(private val connection: Connection) : Consumer<AbstractGatter> {
    override fun accept(gatter: AbstractGatter) {
        when (val renderable = gatter.renderables.first()) {
            is Label -> connection.publishUtterance(renderable.text!!.preprocess())
            is Submit -> connection.publishSubmit(renderable)
            is Button -> connection.publishButton(renderable)
            else -> throw RuntimeException("cannot handle renderable: $renderable")
        }
    }

}
