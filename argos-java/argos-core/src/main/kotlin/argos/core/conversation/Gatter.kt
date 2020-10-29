package argos.core.conversation

import canon.api.IRenderable

abstract class AbstractGatter(val renderables: List<IRenderable>, val regex: Boolean = false)

class And(renderables: List<IRenderable>, regex: Boolean = false): AbstractGatter(renderables, regex)
class Context(renderables: List<IRenderable>): AbstractGatter(renderables)
class Error(val throwable: Throwable) : AbstractGatter(emptyList())
class Log(renderables: List<IRenderable>): AbstractGatter(renderables)
class Notification(renderables: List<IRenderable>): AbstractGatter(renderables)
class Or(renderables: List<IRenderable>, regex: Boolean = false): AbstractGatter(renderables, regex)

// json support
data class AssertJson(val expectation: String, val path: String) : IRenderable
class Json(val text: String) : IRenderable