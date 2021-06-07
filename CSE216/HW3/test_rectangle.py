import unittest
from rectangle import Rectangle
from two_d_point import TwoDPoint


class TestRectangle(unittest.TestCase):

    def setUp(self) -> None:
        self.r1 = Rectangle(2, 1, 0, 1, 0, 0, 2, 0)
        self.r2 = Rectangle(2, 2, 0, 2, 0, 0, 2, 0)

    def test_get__is_member(self):
        self.assertEqual(self.r1.get_is_member(), True)
        self.assertEqual(self.r2.get_is_member(), True)

    def test_center(self):
        self.assertEqual(self.r1.center(), TwoDPoint(1, .5))
        self.assertEqual(self.r2.center(), TwoDPoint(1, 1))

    def test_area(self):
        self.assertEqual(self.r1.area(), 2)
        self.assertEqual(self.r2.area(), 4)

    def test___eq__(self):
        self.assertEqual(self.r1 == self.r2, False)
        self.assertEqual(self.r1 == self.r1, True)
