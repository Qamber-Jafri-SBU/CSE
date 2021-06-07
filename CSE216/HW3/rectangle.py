from quadrilateral import Quadrilateral
from two_d_point import TwoDPoint


class Rectangle(Quadrilateral):

    def __init__(self, *floats):
        super().__init__(*floats)
        if not self.__is_member():
            raise TypeError("A rectangle cannot be formed by the given coordinates.")

    def __eq__(self, other):
        return isinstance(other, Rectangle) and super().__eq__(other)

    def __str__(self):
        s = "Rectangle: "
        for pt in self.vertices:
            s += pt.__str__() + ", "
        return s[0:len(s) - 2]

    def __is_member(self):
        """Returns True if the given coordinates form a valid rectangle, and False otherwise."""
        sides = self.side_lengths()
        if sides[0] == sides[2] or sides[1] == sides[3]:
            return True
        else:
            return False

    def get_is_member(self):
        return self.__is_member()

    def center(self):
        """Returns the center of this rectangle, calculated to be the point of intersection of its diagonals."""
        v = self.vertices
        x, y = 0, 0

        for pt in v:
            x += pt.x
            y += pt.y
        if TwoDPoint.midpoint(v[0], v[2]) == TwoDPoint.midpoint(v[1], v[3]):
            return TwoDPoint.midpoint(v[0], v[2])
        else:
            return TwoDPoint(x/4, y/4)

    def area(self):
        """Returns the area of this rectangle. The implementation invokes the side_lengths() method from the superclass,
        and computes the product of this rectangle's length and width."""
        sides = self.side_lengths()
        # round(, 5)
        return sides[0]*sides[1]
