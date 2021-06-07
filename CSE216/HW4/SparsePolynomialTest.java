import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SparsePolynomialTest {

    static SparsePolynomial sparsePolynomialLinear;
    static SparsePolynomial sparsePolynomialQuartic;
    static SparsePolynomial sparsePolynomialZero;
    static SparsePolynomial sparsePolynomialConstant;
    static DensePolynomial densePolynomialOctic;
    static DensePolynomial densePolynomial;
    static String decimalString;
    static String negativeExponentsString;

    @BeforeAll
    static void setUpClass() {
        sparsePolynomialLinear = new SparsePolynomial("4x + 2");
        sparsePolynomialQuartic = new SparsePolynomial("3x^4 + 2x^2 + 11x");
        sparsePolynomialZero = new SparsePolynomial("0");
        sparsePolynomialConstant = new SparsePolynomial("7");
        densePolynomialOctic = new DensePolynomial("16x^8 + -32x^4 + 16");
        densePolynomial = new DensePolynomial("10x^4 + 3x^1 + 6");//
        decimalString = "10x^3 + 1.2x^2 + -3";
        negativeExponentsString = "3x^6 + -24x + 5x^-1 + 7x^-4 + -9";
    }

    @Test
    void testDegree() {
        assertEquals(1, sparsePolynomialLinear.degree());
        assertEquals(4, sparsePolynomialQuartic.degree());
        assertEquals(0 , sparsePolynomialZero.degree());
    }

    @Test
    void testGetCoefficient() {
        assertEquals(0, sparsePolynomialLinear.getCoefficient(-1));
        assertEquals(7, sparsePolynomialConstant.getCoefficient(0));
        assertEquals(0, sparsePolynomialQuartic.getCoefficient(3));
        assertEquals(0, sparsePolynomialZero.getCoefficient(0));
        assertEquals(2, sparsePolynomialLinear.getCoefficient(0));
        assertEquals(11, sparsePolynomialQuartic.getCoefficient(1));
    }

    @Test
    void testIsZero() {
        assertFalse(sparsePolynomialQuartic.isZero());
        assertFalse(sparsePolynomialConstant.isZero());
        assertTrue(sparsePolynomialZero.isZero());
    }

    @Test
    void testAdd() {
        assertEquals(new SparsePolynomial("3x^4 + 2x^2 + 15x + 2") , sparsePolynomialLinear.add(sparsePolynomialQuartic));
        assertEquals(new SparsePolynomial("4x + 2"), sparsePolynomialLinear.add(sparsePolynomialZero));
        assertEquals(sparsePolynomialConstant, sparsePolynomialZero.add(sparsePolynomialConstant));
        assertThrows(NullPointerException.class, () -> sparsePolynomialQuartic.add(null));
        assertEquals(new SparsePolynomial("10x^4 + 7x + 8"),
                sparsePolynomialLinear.add(densePolynomial));
        assertEquals(new SparsePolynomial("16x^8 + -32x^4 + 23"), sparsePolynomialConstant.add(densePolynomialOctic));
        assertNotEquals(densePolynomialOctic, sparsePolynomialZero.add(densePolynomialOctic));
    }

    @Test
    void testMultiply() {
        assertEquals(sparsePolynomialZero, sparsePolynomialQuartic.multiply(sparsePolynomialZero));
        assertEquals(new SparsePolynomial("12x^5 + 6x^4 + 8x^3 + 48x^2 + 22x") ,
                sparsePolynomialLinear.multiply(sparsePolynomialQuartic));
        assertEquals(sparsePolynomialZero, sparsePolynomialLinear.multiply(sparsePolynomialZero));
        assertEquals(new SparsePolynomial("49"), sparsePolynomialConstant.multiply(sparsePolynomialConstant));
        assertThrows(NullPointerException.class, () -> sparsePolynomialQuartic.multiply(null));
        assertEquals(new SparsePolynomial("40x^5 + 20x^4 + 12x^2 + 30x + 12"), sparsePolynomialLinear.multiply(densePolynomial));
        assertEquals(new SparsePolynomial("112x^8 + -224x^4 + 112"),
                sparsePolynomialConstant.multiply(densePolynomialOctic));
        assertNotEquals(densePolynomialOctic, sparsePolynomialZero.multiply(densePolynomialOctic));
        assertEquals(new SparsePolynomial("48x^12 + 32x^10 + 176x^9 + -96x^8 + -64x^6 + -352x^5 + 48x^4 + 32x^2 + 176x"),
                sparsePolynomialQuartic.multiply(densePolynomialOctic));
    }

    @Test
    void testSubtract() {
        assertEquals(new SparsePolynomial("-3x^4 + -2x^2 + -7x + 2") ,
                sparsePolynomialLinear.subtract(sparsePolynomialQuartic));
        assertEquals(new SparsePolynomial("4x + 2"), sparsePolynomialLinear.subtract(sparsePolynomialZero));
        assertEquals(new SparsePolynomial("-7"), sparsePolynomialZero.subtract(sparsePolynomialConstant));
        assertThrows(NullPointerException.class, () -> sparsePolynomialQuartic.subtract(null));
        assertEquals(new SparsePolynomial("-10x^4 + x + -4"),
                sparsePolynomialLinear.subtract(densePolynomial));
        assertEquals(new SparsePolynomial("-16x^8 + 32x^4 + -9"),
                sparsePolynomialConstant.subtract(densePolynomialOctic));
        assertEquals(new SparsePolynomial("-16x^8 + 32x^4 + -16"),
                sparsePolynomialZero.subtract(densePolynomialOctic));
    }

    @Test
    void testMinus() {
        assertEquals(new SparsePolynomial("0"), sparsePolynomialZero.minus());
        assertEquals(new SparsePolynomial("-3x^4 + -2x^2 + -11x"), sparsePolynomialQuartic.minus());
        assertEquals(new SparsePolynomial("-7"), sparsePolynomialConstant.minus());
        assertEquals(new SparsePolynomial("-4x + -2"), sparsePolynomialLinear.minus());
    }

    @Test
    void testWellFormed() {
        SparsePolynomial p = new SparsePolynomial("0");
        p.setStringRepresentation(null);
        assertThrows(IllegalArgumentException.class, () -> (new SparsePolynomial(null)).wellFormed());
        assertFalse(p.wellFormed());
        p.setStringRepresentation(decimalString);
        assertFalse(p.wellFormed());
        p.setStringRepresentation(negativeExponentsString);
        assertTrue((p.wellFormed()));
        assertTrue(sparsePolynomialQuartic.wellFormed());
        assertTrue(sparsePolynomialZero.wellFormed());
    }

    @Test
    void testEquals() {
        Object o = new Object();
        Object noObj = null;
        assertEquals(sparsePolynomialLinear, sparsePolynomialLinear);
        assertNotEquals(new DensePolynomial("4x + 2"), sparsePolynomialLinear);
        assertNotEquals(o, sparsePolynomialQuartic);
        assertNotEquals(noObj, sparsePolynomialZero);
        assertEquals(sparsePolynomialZero, (new SparsePolynomial("0")));
        assertEquals(new SparsePolynomial("0"), sparsePolynomialZero);
    }
}