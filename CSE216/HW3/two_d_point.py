import math
from typing import List


class TwoDPoint:

    def __init__(self, x, y) -> None:
        self.__x = x
        self.__y = y

    @property
    def x(self):
        return self.__x

    @property
    def y(self):
        return self.__y

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, TwoDPoint):
            return False
        else:
            return self.x == other.x and self.y == other.y

    def __ne__(self, other: object) -> bool:
        return not self.__eq__(other)

    def __str__(self) -> str:
        return '(%g, %g)' % (self.__x, self.__y)

    # TODO: add magic methods such that two TwoDPoint objects can be added and subtracted coordinate-wise just by using
    #  syntax of the form p + q or p - q

    def __add__(self, other):
        return TwoDPoint(self.x + other.x, self.y + other.y)

    def __sub__(self, other):
        return TwoDPoint(self.x - other.x, self.y - other.y)

    @staticmethod
    def from_coordinates(coordinates: List[float]):
        if len(coordinates) % 2 != 0:
            raise Exception("Odd number of floats given to build a list of 2-d points")
        points = []
        it = iter(coordinates)
        for x in it:
            points.append(TwoDPoint(x, next(it)))
        return points

    @staticmethod
    def distance(point1, point2):
        # round(, 5)
        return math.sqrt((point1.x - point2.x) ** 2 + (point1.y - point2.y) ** 2)

    @staticmethod
    def midpoint(point1, point2):
        return TwoDPoint((point1.x + point2.x)/2, (point1.y + point2.y)/2)
