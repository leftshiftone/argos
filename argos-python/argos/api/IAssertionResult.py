from abc import abstractmethod, ABC


class IAssertionResult(ABC):

    @abstractmethod
    def get_message(self) -> str:
        pass