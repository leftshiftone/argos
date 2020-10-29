package argos.core.conversation

abstract class AbstractParticipant(val gatter: AbstractGatter)

class Gaia(gatter: AbstractGatter) : AbstractParticipant(gatter)
class User(gatter: AbstractGatter) : AbstractParticipant(gatter)