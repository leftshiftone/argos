package argos.core.assertion

/**
 * This class holds the necessary specifications for a image assertion to perform.
 *
 * @param skill the url to a skill which is performed on the image
 * @param source the url to an input image which the skill gets performed on
 * @param target the url to the target image to compare
 */
data class ImageAssertionSpec(val skill: String, val source: String, val target: String)