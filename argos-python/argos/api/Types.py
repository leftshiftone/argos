from dataclasses import dataclass

from argos.api.IAssertionResult import IAssertionResult


@dataclass
class Success(IAssertionResult):
    message: str

    def get_message(self):
        return self.message


@dataclass
class Failure(IAssertionResult):
    message: str

    def get_message(self):
        return self.message


@dataclass
class Error(IAssertionResult):
    exception: Exception

    def get_message(self):
        return str(self.exception)
