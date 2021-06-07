import unittest
import math
from quadrilateral import Quadrilateral


class TestQuadrilateral(unittest.TestCase):

    def setUp(self) -> None:
        self.q1 = Quadrilateral(1, 3, 0, 2, -1, 0, 0, 0)
        self.q2 = Quadrilateral(1, 1, 0, 1, 0, 0, 0, 1)

    def test_side_lengths(self):
        # (round(math.sqrt(2), 5), round(math.sqrt(10), 5), round(math.sqrt(1), 5), round(math.sqrt(5), 5))
        self.assertEqual(self.q1.side_lengths(), (math.sqrt(2), math.sqrt(10), math.sqrt(1), math.sqrt(5)))
        self.assertEqual(self.q2.side_lengths(), (1, 1, 1, 1))

    def test_smallest_x(self):
        self.assertEqual(self.q1.smallest_x(), -1)
        self.assertEqual(self.q2.smallest_x(), 0)

    def test___eq__(self):
        self.assertEqual(self.q1 == self.q1, True)
        self.assertEqual(self.q2 == self.q1, False)
