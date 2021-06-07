import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DensePolynomialTest {

    static DensePolynomial densePolynomialLinear;
    static DensePolynomial densePolynomialQuartic;
    static DensePolynomial densePolynomialZero;
    static DensePolynomial densePolynomialConstant;
    static SparsePolynomial sparsePolynomialOctic;
    static SparsePolynomial sparsePolynomialNegativeExponent;
    static String decimalString;
    static String negativeExponentsString;

    @BeforeAll
    static void setUpClass() {
        densePolynomialLinear = new DensePolynomial("4x + 2");
        densePolynomialQuartic = new DensePolynomial("3x^4 + 2x^2 + 11x");
        densePolynomialZero = new DensePolynomial("0");
        densePolynomialConstant = new DensePolynomial("7");
        sparsePolynomialOctic = new SparsePolynomial("16x^8 + -32x^4 + 16");
        sparsePolynomialNegativeExponent = new SparsePolynomial("10x^4 + 3x^-1 + 6");
        decimalString = "10x^3 + 1.2x^2 + -3";
        negativeExponentsString = "3x^6 + -24x + 5x^-1 + 7x^-4 + -9";
    }

    @Test
    void testDegree() {
        assertEquals(1, densePolynomialLinear.degree());
        assertEquals(4, densePolynomialQuartic.degree());
        assertEquals(0 , densePolynomialZero.degree());
    }

    @Test
    void testGetCoefficient() {
        assertEquals(0, densePolynomialLinear.getCoefficient(-1));
        assertEquals(7, densePolynomialConstant.getCoefficient(0));
        assertEquals(0, densePolynomialQuartic.getCoefficient(3));
        assertEquals(0, densePolynomialZero.getCoefficient(0));
        assertEquals(2, densePolynomialLinear.getCoefficient(0));
        assertEquals(11, densePolynomialQuartic.getCoefficient(1));
    }

    @Test
    void testIsZero() {
        assertFalse(densePolynomialQuartic.isZero());
        assertFalse(densePolynomialConstant.isZero());
        assertTrue(densePolynomialZero.isZero());
    }

    @Test
    void testAdd() {
        assertEquals(new DensePolynomial("3x^4 + 2x^2 + 15x + 2") , densePolynomialLinear.add(densePolynomialQuartic));
        assertEquals(new DensePolynomial("4x + 2"), densePolynomialLinear.add(densePolynomialZero));
        assertEquals(densePolynomialConstant, densePolynomialZero.add(densePolynomialConstant));
        assertThrows(NullPointerException.class, () -> densePolynomialQuartic.add(null));
        assertThrows(IllegalArgumentException.class,
                ()  -> densePolynomialLinear.add(sparsePolynomialNegativeExponent));
        assertEquals(new DensePolynomial("16x^8 + -32x^4 + 23"), densePolynomialConstant.add(sparsePolynomialOctic));
        assertNotEquals(sparsePolynomialOctic, densePolynomialZero.add(sparsePolynomialOctic));
    }

    @Test
    void testMultiply() {
        assertEquals(densePolynomialZero, densePolynomialQuartic.multiply(densePolynomialZero));
        assertEquals(new DensePolynomial("12x^5 + 6x^4 + 8x^3 + 48x^2 + 22x") ,
                densePolynomialLinear.multiply(densePolynomialQuartic));
        assertEquals(densePolynomialZero, densePolynomialLinear.multiply(densePolynomialZero));
        assertEquals(new DensePolynomial("49"), densePolynomialConstant.multiply(densePolynomialConstant));
        assertThrows(NullPointerException.class, () -> densePolynomialQuartic.multiply(null));
        assertThrows(IllegalArgumentException.class,
                ()  -> densePolynomialLinear.multiply(sparsePolynomialNegativeExponent));
        assertEquals(new DensePolynomial("112x^8 + -224x^4 + 112"),
                densePolynomialConstant.multiply(sparsePolynomialOctic));
        assertNotEquals(sparsePolynomialOctic, densePolynomialZero.multiply(sparsePolynomialOctic));
        assertEquals(new DensePolynomial("48x^12 + 32x^10 + 176x^9 + -96x^8 + -64x^6 + -352x^5 + 48x^4 + 32x^2 + 176x"),
                densePolynomialQuartic.multiply(sparsePolynomialOctic));
    }

    @Test
    void testSubtract() {
        assertEquals(new DensePolynomial("-3x^4 + -2x^2 + -7x + 2") ,
                densePolynomialLinear.subtract(densePolynomialQuartic));
        assertEquals(new DensePolynomial("4x + 2"), densePolynomialLinear.subtract(densePolynomialZero));
        assertEquals(new DensePolynomial("-7"), densePolynomialZero.subtract(densePolynomialConstant));
        assertThrows(NullPointerException.class, () -> densePolynomialQuartic.subtract(null));
        assertThrows(IllegalArgumentException.class,
                ()  -> densePolynomialLinear.subtract(sparsePolynomialNegativeExponent));
        assertEquals(new DensePolynomial("-16x^8 + 32x^4 + -9"),
                densePolynomialConstant.subtract(sparsePolynomialOctic));
        assertEquals(new DensePolynomial("-16x^8 + 32x^4 + -16"),
                densePolynomialZero.subtract(sparsePolynomialOctic));
    }

    @Test
    void testMinus() {
        assertEquals(new DensePolynomial("0"), densePolynomialZero.minus());
        assertEquals(new DensePolynomial("-3x^4 + -2x^2 + -11x"), densePolynomialQuartic.minus());
        assertEquals(new DensePolynomial("-7"), densePolynomialConstant.minus());
        assertEquals(new DensePolynomial("-4x + -2"), densePolynomialLinear.minus());
    }

    @Test
    void testWellFormed() {
        DensePolynomial p = new DensePolynomial("0");
        p.setStringRepresentation(null);
        assertThrows(IllegalArgumentException.class, () -> (new DensePolynomial(null)).wellFormed());
        assertFalse(p.wellFormed());
        p.setStringRepresentation(decimalString);
        assertFalse(p.wellFormed());
        p.setStringRepresentation(negativeExponentsString);
        assertFalse((p.wellFormed()));
        assertTrue(densePolynomialQuartic.wellFormed());
        assertTrue(densePolynomialZero.wellFormed());
    }

    @Test
    void testEquals() {
        Object o = new Object();
        Object noObj = null;
        assertEquals(densePolynomialLinear, densePolynomialLinear);
        assertNotEquals(new SparsePolynomial("4x + 2"), densePolynomialLinear);
        assertNotEquals(o, densePolynomialQuartic);
        assertNotEquals(noObj, densePolynomialZero);
        assertEquals(densePolynomialZero, (new DensePolynomial("0")));
        assertEquals(new DensePolynomial("0"), densePolynomialZero);
    }
}