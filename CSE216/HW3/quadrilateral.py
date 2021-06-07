from two_d_point import TwoDPoint


class Quadrilateral:

    def __init__(self, *floats):
        if len(floats) != 8:
            raise Exception("A Quadrilateral must have 4 vertices!")
        points = TwoDPoint.from_coordinates(list(floats))
        self.__vertices = tuple(points[0:4])

    @property
    def vertices(self):
        return self.__vertices

    @vertices.setter
    def vertices(self, points):
        self.__vertices = tuple(points[0:4])

    def __eq__(self, other):
        if not isinstance(other, Quadrilateral):
            return False
        else:
            for i in range(4):
                if self.vertices[i] != other.vertices[i]:
                    return False
            return True

    def __str__(self):
        string = "Quadrilateral: "
        for pt in self.vertices:
            string += pt.__str__() + ", "
        return string[0:len(string)-2]

    def side_lengths(self):
        """Returns a tuple of four floats, each denoting the length of a side of this quadrilateral. The value must be
        ordered clockwise, starting from the top left corner."""
        v = self.vertices
        return TwoDPoint.distance(v[1], v[0]), TwoDPoint.distance(v[0], v[3]),\
            TwoDPoint.distance(v[3], v[2]), TwoDPoint.distance(v[2], v[1])

    def smallest_x(self):
        """Returns the x-coordinate of the vertex with the smallest x-value of the four vertices of this
        quadrilateral."""
        v = self.vertices
        return min(v[0].x, v[1].x, v[2].x, v[3].x)
