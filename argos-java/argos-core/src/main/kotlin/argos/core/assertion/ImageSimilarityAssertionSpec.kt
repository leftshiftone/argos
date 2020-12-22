package argos.core.assertion

/**
 * This class holds the necessary specifications for a image similarity assertion to perform.
 *
 * @param image1 the url to the first image to compare
 * @param image2 the url to the second image to compare
 * @param threshold the threshold score
 */
data class ImageSimilarityAssertionSpec(val image1: String, val image2: String, val threshold: Float = 0.9f)