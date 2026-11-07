"""聚合占位。"""

from domain.template.model.entity import XxxEntity

class XxxAggregate:
    def __init__(self, entity: XxxEntity | None = None) -> None:
        self.entity = entity
