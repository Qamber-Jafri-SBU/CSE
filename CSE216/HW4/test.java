import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class test {

    public static void main(String[] args) {
//        int numOperators = 0, numOperands = 0;
//        String operands = "(-?\\d*x\\^?\\d*)|(-?\\d+)";
//        String operators = "[+*/]";
//        String input = "5x";
//        Pattern pattern = Pattern.compile(operands);
//        Matcher matcher = pattern.matcher(input);
//        while(matcher.find()){
//            System.out.println(matcher.group());
//            numOperands++;
//        }
//        System.out.println("operands" + numOperands);
//        pattern = Pattern.compile(operators);
//        matcher = pattern.matcher(input);
//        while(matcher.find()){
//            System.out.println(matcher.group());
//            numOperators++;
//        }
//        System.out.println("operators" + numOperators);
        //numOperators == numOperands - 1
        DensePolynomial densePolynomialOne = new DensePolynomial("4x + 2");
        Polynomial p = new DensePolynomial("2x^4 + 0x^3 + 5x^2 + -2x");
        Polynomial q = new SparsePolynomial("10x^4 + 3x^-1 + 6");
        //DensePolynomial x = (DensePolynomial)p.minus();
        System.out.println(p);
    }
}
