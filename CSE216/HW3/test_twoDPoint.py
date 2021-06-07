import unittest
from two_d_point import TwoDPoint


class TestTwoDPoint(unittest.TestCase):

    def setUp(self) -> None:
        self.point1 = TwoDPoint(0, 0)
        self.point2 = TwoDPoint(1, 2)
        self.point3 = TwoDPoint(5, 0)
        self.point4 = TwoDPoint(0, 0)
        self.coordinates_even = [1, -2.8, 3, 4.6]
        self.coordinates_odd = [-1, 2, -3.7, 4, 5]

    def tearDown(self) -> None:
        pass

    def test_from_coordinates(self):
        self.assertEqual(TwoDPoint.from_coordinates(self.coordinates_even), [TwoDPoint(1, -2.8), TwoDPoint(3, 4.6)])
        with self.assertRaises(Exception):
            TwoDPoint.from_coordinates(self.coordinates_odd)

    def test_distance(self):
        self.assertEqual(TwoDPoint.distance(self.point1, self.point3), 5)
        self.assertEqual(TwoDPoint.distance(self.point1, self.point4), 0)

    def test_midpoint(self):
        self.assertEqual(TwoDPoint.midpoint(self.point1, self.point3), TwoDPoint(2.5, 0))
        self.assertEqual(TwoDPoint.midpoint(self.point1, self.point2), TwoDPoint(.5, 1))

    def test___add__(self):
        self.assertEqual(self.point1 + self.point2, TwoDPoint(1, 2))
        self.assertEqual(self.point2 + self.point3, TwoDPoint(6, 2))

    def test___sub__(self):
        self.assertEqual(self.point1 - self.point2, TwoDPoint(-1, -2))
        self.assertEqual(self.point3 - self.point1, TwoDPoint(5, 0))

    def test___eq__(self):
        self.assertEqual(self.point1 == self.point4, True)
        self.assertEqual(self.point1 == self.point2, False)

    def test___ne__(self):
        self.assertEqual(self.point1 != self.point4, False)
        self.assertEqual(self.point1 != self.point2, True)

    def test___str__(self):
        pass

    # if __name__ == '__main__':
    #     unittest.main()
