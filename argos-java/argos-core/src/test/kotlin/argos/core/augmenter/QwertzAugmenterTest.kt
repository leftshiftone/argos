package argos.core.augmenter

import org.assertj.core.api.ErrorCollector
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class QwertzAugmenterTest {
    data class Result (val sampleText: String, val iterations: Int, val errors: Int, val percentage: Double)

    @Test
    fun testAugment1() {
        printResult(testAugment(10, 2500))
    }

    @Test
    fun testAugment2() {
        printResult(testAugment(15, 2500))
    }

    @Test
    fun testAugment3() {
        printResult(testAugment(10, 3000))
    }

    fun testAugment(sampleLength: Int, iterations: Int): Result {
        val sampleText = createSample(sampleLength)
        val collector = ErrorCollector()
        val augmenter = QwertzAugmenter()

        for (x in 1..iterations) {
            try {
                Assertions.assertEquals(sampleText, augmenter.augment(sampleText))
            } catch (e: Throwable) {
                collector.addError(e)
            }
        }
        return Result(sampleText, iterations, collector.errors().size,
                collector.errors().size.toDouble() / iterations.toDouble() * 100.0)
    }

    fun createSample(sampleLength: Int): String {
        var sample = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor " +
                "invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et " +
                "justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum " +
                "dolor sit amet. "
        while(sampleLength > sample.length)
            sample += sample

        return sample.subSequence(0, sampleLength).toString()
    }

    fun printResult (result: Result) {
        println("Sample Length: ${result.sampleText.length}\n" +
                "Iterations: ${result.iterations}\n" +
                "Errors: ${result.errors}\n" +
                "Percentage: ${String.format("%.3f", result.percentage)}")
    }
}