import unittest
from square import Square
from quadrilateral import Quadrilateral


class TestSquare(unittest.TestCase):

    def setUp(self) -> None:
        self.s1 = Square(2.1, 2.1, 0.1, 2.1, 0.1, 0.1, 2.1, 0.1)
        self.s2 = Square(.2, .2, 0, .2, 0, 0, .2, 0)

    def test_get__is_member(self):
        self.assertEqual(self.s1.get_is_member(), True)
        self.assertEqual(self.s2.get_is_member(), True)

    def test_snap(self):
        self.assertEqual(self.s1.snap(), Quadrilateral(2, 2, 0, 2, 0, 0, 2, 0))
        self.assertEqual(self.s2.snap(), self.s2)

    def test___eq__(self):
        self.assertEqual(self.s1 == self.s2, False)
        self.assertEqual(self.s1 == self.s1, True)
