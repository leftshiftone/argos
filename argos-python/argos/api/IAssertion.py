from abc import ABC, abstractmethod

from argos.api import ArgosOptions


class IAssertion(ABC):

    @abstractmethod
    def assertion(self, options: ArgosOptions):
        pass