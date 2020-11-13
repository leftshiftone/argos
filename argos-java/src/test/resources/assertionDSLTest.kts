import argos.api.ArgosOptions
import argos.runtime.dsl.*
import gaia.sdk.HMACCredentials
import gaia.sdk.core.GaiaConfig

val options = ArgosOptions("", GaiaConfig("", HMACCredentials("", "")))
ArgosDSL.argos("argos test", options) {
    assertIntent("Ich suche einen Anwalt", "findLawyer", 0.9f)
}