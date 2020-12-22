package argos.core.identity

import org.junit.jupiter.api.Test
import java.io.File

class MarkupIdentityTest: AbstractIdentityTest(File("src/test/resources/gaia/markup/tests/"),
        File("src/test/resources/gaia/markup/includes/")) {

    @Test fun `test advanced`() = testIdentity("advanced")
    @Test fun `test basic`() = testIdentity("basic")
    @Test fun `test input`() = testIdentity("input")
    @Test fun `test multi`() = testIdentity("multi")
}