package argos.api

/**
 * Classes which implements this interface can be used by argos to handle specific argos events.
 */
interface IAssertionListener {

    /**
     * Argos event which is invoked before all assertion.
     */
    fun onBeforeAssertions() {
        // do nothing
    }

    /**
     * Argos event which is invoked before each assertion
     *
     * @param assertion: the assertion instance
     */
    fun onBeforeAssertion(assertion: IAssertion) {
        // do nothing
    }

    /**
     * Argos event which is invoked after each assertion.
     */
    fun onAfterAssertions() {
        // do nothing
    }

    /**
     * Argos event which is invoked after all assertion.
     *
     * @param: assertion: the assertion instance
     * @param result: the assertion result instance
     */
    fun onAfterAssertion(assertion: IAssertion, result: IAssertionResult) {
        // do nothing
    }

}
