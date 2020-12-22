package argos.api

import gaia.sdk.HMACCredentials
import gaia.sdk.core.GaiaConfig
import gaia.sdk.mqtt.MqttSensorQueue
import gaia.sdk.spi.QueueOptions
import java.util.concurrent.CopyOnWriteArrayList

/**
 * This class contains all information which are necessary for argos to on the one hand connect to a gaia instance
 * and on the other hand to handle specific argos events via assertion listeners.
 */
class ArgosOptions {
    val identity: String
    val config: GaiaConfig

    private val listeners = CopyOnWriteArrayList<IAssertionListener>()

    constructor(identity: String) {
        val env = System.getenv()
        val queueOptions = QueueOptions(
            env.getOrDefault("GAIA_MQTT_HOST", "beta.api.leftshift.one"),
            env.getOrDefault("GAIA_MQTT_PORT", "443").toInt()
        )
        queueOptions.username = env.get("GAIA_MQTT_USERNAME")
        queueOptions.password = env.get("GAIA_MQTT_PASSWORD")
        queueOptions.isWebsocket = env.getOrDefault("GAIA_MQTT_IS_WEBSOCKET", "true")!!.toBoolean()
        queueOptions.isSsl = env.getOrDefault("GAIA_MQTT_IS_SSL", "true")!!.toBoolean()

        val config = GaiaConfig(
            url = env.getOrDefault("GAIA_CONFIG_URL", ""),
            credentials = HMACCredentials(
                env.getOrDefault("GAIA_CREDENTIALS_API_KEY", ""),
                env.getOrDefault("GAIA_CREDENTIALS_API_SECRET", "") ),
            queueProcessor = MqttSensorQueue(queueOptions)
        )

        this.identity = identity
        this.config = config
    }

    constructor(identity: String, config: GaiaConfig) {
        this.identity = identity
        this.config = config
    }

    /**
     * Registers the given assertion listener.
     */
    fun addListener(listener: IAssertionListener) = listeners.add(listener)

    /**
     * Returns all assertion listeners in an immutable manner. If no assertion listener is registered
     * the default assertion listener(s) get returned.
     *
     * @return a list of IAssertionListeners
     */
    fun getListeners(): List<IAssertionListener> {
        if (listeners.isEmpty()) {
            return listOf(PrintlnListener())
        }
        return CopyOnWriteArrayList(listeners)
    }


}
