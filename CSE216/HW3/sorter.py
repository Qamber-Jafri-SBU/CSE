from quadrilateral import Quadrilateral


class ShapeSorter:

    @staticmethod
    def sort(*args):
        shapes = list(args)

        for shape in shapes:
            shapes.sort(key=Quadrilateral.smallest_x)
        return shapes
