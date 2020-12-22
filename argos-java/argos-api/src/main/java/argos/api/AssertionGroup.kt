package argos.api

/**
 * Class to group IAssertion elements.
 *
 * @param name the name of this assertion group, <code>null</code> if an assertion isn't part of an assertion group
 * @param assertions the IAssertion elements that are part of this group
 */
data class AssertionGroup(val name: String?, val assertions: List<IAssertion>)