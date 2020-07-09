package argos.api

import java.util.concurrent.CopyOnWriteArrayList

/**
 * This class contains all information which are necessary for argos to on the one hand connect to a gaia instance
 * and on the other hand to handle specific argos events via assertion listeners.
 */
class ArgosOptions(val identity: String,
                   val apiKey: String,
                   val secret: String,
                   val url: String) {

    private val listeners = CopyOnWriteArrayList<IAssertionListener>()

    // TODO: initialize the gaia sdk client
    // but thats is not what I want I want an mqtt connect -> MqttSensorQueue



    /**
     * Registers the given assertion listener.
     */
    fun addListener(listener: IAssertionListener) = listeners.add(listener)

    /**
     * Returns all assertion listeners in an immutable manner. If no assertion listener is registered
     * the default assertion listener(s) get returned.
     */
    fun getListeners(): List<IAssertionListener> {
        if (listeners.isEmpty()) {
            return listOf(PrintlnListener())
        }
        return CopyOnWriteArrayList(listeners)
    }




}
