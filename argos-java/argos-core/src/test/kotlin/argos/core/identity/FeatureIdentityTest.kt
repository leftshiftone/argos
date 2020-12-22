package argos.core.identity

import org.junit.jupiter.api.Test
import java.io.File

class FeatureIdentityTest: AbstractIdentityTest(File("src/test/resources/gaia/feature/")) {

    @Test fun `test feature_ambiguous_identity_supervisors`() = testIdentity("feature_ambiguous_identity_supervisors")
    @Test fun `test feature_atreus_gaiaquery`() = testIdentity("feature_atreus_gaiaquery")
    @Test fun `test feature_atreus_http`() = testIdentity("feature_atreus_http")
    @Test fun `test feature_atreus_pdfscript`() = testIdentity("feature_atreus_pdfscript")
    @Test fun `test feature_atreus_protoscript`() = testIdentity("feature_atreus_protoscript")
    @Test fun `test feature_buttons_ordinary_invoke`() = testIdentity("feature_buttons_ordinary_invoke")
    @Test fun `test feature_buttons_textual_invoke`() = testIdentity("feature_buttons_textual_invoke")
    @Test fun `test feature_context_in_out`() = testIdentity("feature_context_in_out")
    @Test fun `test feature_gateway_default_flow`() = testIdentity("feature_gateway_default_flow")
    @Test fun `test feature_identity_supervisors`() = testIdentity("feature_identity_supervisors")
    @Test fun `test feature_link_event`() = testIdentity("feature_link_event")
    @Test fun `test feature_nested_prompt`() = testIdentity("feature_nested_prompt")
    @Test fun `test feature_notification`() = testIdentity("feature_notification")
    @Test fun `test feature_reprompt_if_invalid_input`() = testIdentity("feature_reprompt_if_invalid_input")
    @Test fun `test feature_subprocess`() = testIdentity("feature_subprocess")
    @Test fun `test feature_zmq`() = testIdentity("feature_zmq")

//    @Test
//    fun testAllFeatures() {
//        File("src/test/resources/gaia/feature/")
//                .walk()
//                .filter { it.isFile }
//                .forEach { testIdentity(it.nameWithoutExtension) }
//    }

}