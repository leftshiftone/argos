from dataclasses import dataclass

from rx import Observable

from argos.api import IAssertion, ArgosOptions, IAssertionResult


@dataclass
class IntentAssertionSpec:
    text:str
    intent:str
    score:float = 0.85


@dataclass
class IntentAssertion(IAssertion):
    spec:IntentAssertionSpec

    def assertion(self, options:ArgosOptions) -> Observable[IAssertionResult]:
        pass