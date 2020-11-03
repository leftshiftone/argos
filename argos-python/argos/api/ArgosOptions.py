from dataclasses import dataclass

from gaia_sdk.gaia import GaiaConfig

from argos.api.Listeners import PrintlnListener


@dataclass
class ArgosOptions:
    identity: str
    config: GaiaConfig

    listeners = []

    def add_listener(self):
        pass

    def get_listeners(self):
        if len(self.listeners) == 0:
            return [PrintlnListener()]
        return self.listeners
