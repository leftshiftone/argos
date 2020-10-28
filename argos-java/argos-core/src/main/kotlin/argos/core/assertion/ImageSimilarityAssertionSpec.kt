package argos.core.assertion

data class ImageSimilarityAssertionSpec(val image1: String, val image2: String, val threshold: Float = 0.9f)