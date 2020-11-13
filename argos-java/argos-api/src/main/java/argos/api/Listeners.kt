package argos.api

/**
 * This assertion listener prints the results to the console.
 */
class PrintlnListener : IAssertionListener {
    override fun onAfterAssertion(assertion: IAssertion, result: IAssertionResult) {
//        when (result) {
//            is Success -> System.out.println(result.getMessage())
//            is Failure -> System.err.println(result.getMessage())
//            is Error -> result.throwable.printStackTrace()
//        }
    }
}
