from argos.api import IAssertionListener, IAssertion, IAssertionResult


class PrintlnListener(IAssertionListener):

    def on_after_assertion(self, assertion: IAssertion, result: IAssertionResult):
        print(result.get_message())
