from abc import ABC

from argos.api import IAssertion
from argos.api.IAssertionResult import IAssertionResult


class IAssertionListener(ABC):

    def on_before_assertions(self):
        pass

    def on_before_assertion(self, assertion:IAssertion):
        pass

    def on_after_assertions(self):
        pass

    def on_after_assertion(self, assertion:IAssertion, result: IAssertionResult):
        pass