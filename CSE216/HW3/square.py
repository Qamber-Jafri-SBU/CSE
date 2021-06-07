from rectangle import Rectangle
from quadrilateral import Quadrilateral


class Square(Rectangle):

    def __init__(self, *floats):
        super().__init__(*floats)
        if not self.__is_member():
            raise TypeError("A square cannot be formed by the given coordinates.")

    def __eq__(self, other):
        return isinstance(other, Square) and super().__eq__(other)

    def __str__(self):
        string = "Square: "
        for pt in self.vertices:
            string += pt.__str__() + ", "
        return string[0:len(string) - 2]

    # Ask Banerjee about this!!!
    # if super().__is_member():
    #     sides = self.side_lengths()
    #     if sides[0] == sides[1]:
    #         return True
    #     else:
    #         return False
    def __is_member(self):
        sides = self.side_lengths()
        return sides[0] == sides[1] and sides[1] == sides[2] and sides[2] == sides[3] and sides[3] == sides[0]

    def get_is_member(self):
        return self.__is_member()

    def snap(self):
        """Snaps the sides of the square such that each corner (x,y) is modified to be a corner (x',y') where x' is the
        integer value closest to x and y' is the integer value closest to y. This, of course, may change the shape to a
        general quadrilateral, hence the return type. The only exception is when the square is positioned in a way where
        this approximation will lead it to vanish into a single point. In that case, a call to snap() will not modify
        this square in any way."""
        new_square = Quadrilateral(round(self.vertices[0].x), round(self.vertices[0].y), round(self.vertices[1].x),
                                   round(self.vertices[1].y), round(self.vertices[2].x), round(self.vertices[2].y),
                                   round(self.vertices[3].x), round(self.vertices[3].y))

        for i in range(3):
            j = 1 + i
            for j in range(i):
                if new_square.vertices[i].x == new_square.vertices[j].x and new_square.vertices[i].y == \
                        new_square.vertices[j].y:
                    return self

        return new_square
