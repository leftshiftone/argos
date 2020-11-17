import argos.api.ArgosOptions
import argos.runtime.dsl.*
import gaia.sdk.HMACCredentials
import gaia.sdk.core.GaiaConfig

val options = ArgosOptions("test_id", GaiaConfig("test_url", HMACCredentials("test_key", "test_secret")))
ArgosDSL.argos("argos test", options) {
    assertIntent("Ich suche einen Anwalt", "findLawyer", 0.9f)
}