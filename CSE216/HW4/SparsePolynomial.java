import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SparsePolynomial implements Polynomial{

    private final TreeMap<Integer, Integer> coefficients;
    private String stringRepresentation;

    public SparsePolynomial(String polynomial) throws IllegalArgumentException{
        if(polynomial == null)
            throw new IllegalArgumentException("This polynomial is not well-formed!");
        stringRepresentation = polynomial;
        coefficients = new TreeMap<>();
        if(!wellFormed() || polynomial.equals(""))
            throw new IllegalArgumentException("This polynomial is not well-formed!");
    }

    private SparsePolynomial(TreeMap<Integer, Integer> coefficients){
        assert coefficients != null;
        this.coefficients = coefficients;
        stringRepresentation = toString();
    }

    /**
     * Returns the degree of the polynomial.
     *
     * @return the largest exponent with a non-zero coefficient.  If all terms have zero exponents, it returns 0.
     */
    @Override
    public int degree() {
        return coefficients.lastKey();
    }

    /**
     * Returns the coefficient corresponding to the given exponent.  Returns 0 if there is no term with that exponent
     * in the polynomial.
     *
     * @param d the exponent whose coefficient is returned.
     * @return the coefficient of the term of whose exponent is d.
     */
    @Override
    public int getCoefficient(int d) {
        if(coefficients.get(d) == null)
            return 0;
        return coefficients.get(d);
    }

    /**
     * @return true if the polynomial represents the zero constant
     */
    @Override
    public boolean isZero() {
        for(Map.Entry<Integer, Integer> entry: coefficients.entrySet()){
            if(entry.getValue() != 0){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a polynomial by adding the parameter to the current instance. Neither the current instance nor the
     * parameter are modified.
     *
     * @param q the non-null polynomial to add to <code>this</code>
     * @return <code>this + </code>q
     * @throws NullPointerException if q is null
     */
    @Override
    public Polynomial add(Polynomial q) throws NullPointerException{
        if(q == null){
            throw new NullPointerException("Please provide another operand!");
        }

        TreeMap<Integer, Integer> newCoefficients = new TreeMap<>(Collections.reverseOrder());
        newCoefficients.putAll(coefficients);
        if(q instanceof DensePolynomial) {
            for(int i = 0; i < ((DensePolynomial) q).getCoefficients().length; i++){
                if(i < ((DensePolynomial) q).getCoefficients().length){
                    if (newCoefficients.get(i) == null) {
                        newCoefficients.put(i, ((DensePolynomial) q).getCoefficients()[i]);
                    } else {
                        newCoefficients.put(i, newCoefficients.get(i) + ((DensePolynomial) q).getCoefficients()[i]);
                    }
                }
            }
        }
        if(q instanceof SparsePolynomial) {
            ((SparsePolynomial) q).getCoefficients().forEach((k, v) -> newCoefficients.merge(k, v,
                    Integer::sum));
        }

        return new SparsePolynomial(newCoefficients);
    }

    /**
     * Returns a polynomial by multiplying the parameter with the current instance.  Neither the current instance nor
     * the parameter are modified.
     *
     * @param q the polynomial to multiply with <code>this</code>
     * @return <code>this * </code>q
     * @throws NullPointerException if q is null
     */
    @Override
    public Polynomial multiply(Polynomial q) throws NullPointerException{
        if(q == null){
            throw new NullPointerException("Please provide another operand!");
        }

        TreeMap<Integer, Integer> newCoefficients = new TreeMap<>(Collections.reverseOrder());

        SparsePolynomial resultant = new SparsePolynomial("0");
        if(q instanceof DensePolynomial){
            TreeMap<Integer, Polynomial> polynomials = new TreeMap<>(Collections.reverseOrder());
            int lowestPower = Collections.min(coefficients.keySet());
            for(int i = lowestPower; i <= degree(); i++){
                for(int j = 0; j <= q.degree(); j++) {
                    if(((DensePolynomial) q).getCoefficients()[j] == 0){
                        continue;
                    }
                    if(coefficients.containsKey(i)){
                        newCoefficients.put(i+j, coefficients.get(i)*((DensePolynomial) q).getCoefficients()[j]);
                    }
                }
                polynomials.put(i, new SparsePolynomial(newCoefficients));
                newCoefficients = new TreeMap<>(Collections.reverseOrder());
            }
            for(int i = lowestPower; i <= polynomials.size(); i++){
                if(polynomials.get(i) == null || polynomials.get(i).isZero()){
                    continue;
                }
                resultant = (SparsePolynomial) resultant.add(polynomials.get(i));
            }
        }

        if(q instanceof SparsePolynomial){
            SparsePolynomial[] polynomials = new SparsePolynomial[degree() + 1];
            int lowestPower = Collections.min(coefficients.keySet());
            int qLowestPower = Collections.min(((SparsePolynomial) q).getCoefficients().keySet());
            for(int i = lowestPower; i <= degree(); i++){
                for(int j = qLowestPower; j <= q.degree(); j++) {
                    if(coefficients.containsKey(i) && ((SparsePolynomial) q).getCoefficients().containsKey(j)){
                        newCoefficients.put(i+j, coefficients.get(i)*((SparsePolynomial) q).getCoefficients().get(j));
                    }
                }
                polynomials[i] = new SparsePolynomial(newCoefficients);
                newCoefficients = new TreeMap<>(Collections.reverseOrder());
            }

            for(SparsePolynomial p : polynomials){
                if(p == null || p.isZero()){
                    continue;
                }
                resultant = (SparsePolynomial) resultant.add(p);
            }
        }

        return resultant;
    }

    /**
     * Returns a  polynomial by subtracting the parameter from the current instance. Neither the current instance nor
     * the parameter are modified.
     *
     * @param q the non-null polynomial to subtract from <code>this</code>
     * @return <code>this - </code>q
     * @throws NullPointerException if q is null
     */
    @Override
    public Polynomial subtract(Polynomial q) throws NullPointerException{
        if(q == null){
            throw new NullPointerException("Please provide another operand!");
        }


        TreeMap<Integer, Integer> newCoefficients = new TreeMap<>(Collections.reverseOrder());
        newCoefficients.putAll(coefficients);
        if(q instanceof DensePolynomial) {
            for(int i = 0; i < ((DensePolynomial) q).getCoefficients().length; i++){
                if(i < ((DensePolynomial) q).getCoefficients().length){
                    if (newCoefficients.get(i) == null) {
                        newCoefficients.put(i, -((DensePolynomial) q).getCoefficients()[i]);
                    } else {
                        newCoefficients.put(i, newCoefficients.get(i) - ((DensePolynomial) q).getCoefficients()[i]);
                    }
                }
            }
        }
        if(q instanceof SparsePolynomial) {
            ((SparsePolynomial) q).getCoefficients().forEach((k, v) -> newCoefficients.merge(k, -v,
                    Integer::sum));
        }

        return new SparsePolynomial(newCoefficients);
    }

    /**
     * Returns a polynomial by negating the current instance. The current instance is not modified.
     *
     * @return -this
     */
    @Override
    public Polynomial minus() {
        TreeMap<Integer, Integer> negatedCoefficients = new TreeMap<>(Collections.reverseOrder());

        for(Map.Entry<Integer, Integer> entry : coefficients.entrySet()){
            negatedCoefficients.put(entry.getKey(), -entry.getValue());
        }

        return new SparsePolynomial(negatedCoefficients);
    }

    /**
     * Checks if the class invariant holds for the current instance. If the polynomial is wellformed, the
     * coefficients are stored in the coefficients instance variable.
     *
     * @return {@literal true} if the class invariant holds, and {@literal false} otherwise.
     */
    @Override
    public boolean wellFormed() {
        if(stringRepresentation == null){
            return false;
        }

        String decimalCheck = "\\.";
        Pattern pattern = Pattern.compile(decimalCheck);
        Matcher matcher = pattern.matcher(stringRepresentation);
        if(matcher.find()){
            return false;
        }

        String operands = "(-?\\d*x\\^?-?\\d*)|(-?\\d+)";
        pattern = Pattern.compile(operands);
        matcher = pattern.matcher(stringRepresentation);

        String s, num, exp;
        boolean passedVariable, isBaseNegative, isExponentNegative;

        while(matcher.find()) {
            s = matcher.group();
            num = ""; exp = "0";
            passedVariable = false; isBaseNegative = false; isExponentNegative = false;

            for (int i = 0; i < s.length(); i++) {
                while (i < s.length() && !Character.isDigit(s.charAt(i))) {

                    if (s.charAt(i) == 'x') {
                        passedVariable = true;
                    }
                    if (!passedVariable) {
                        if (s.charAt(i) == '-')
                            isBaseNegative = true;
                    } else {
                        if (s.charAt(i) == '-')
                            isExponentNegative = true;
                    }
                    i++;
                }

                if (!passedVariable) {
                    num += s.charAt(i);
                } else {
                    if (!(i < s.length())) {
                        exp += "1";
                        break;
                    }
                    exp += s.charAt(i);
                }

            }

            int base;
            if(num.equals(""))
                base = 1;
            else
                base = Integer.parseInt(num);

            if(!isExponentNegative){
                if(!isBaseNegative)
                    coefficients.put(Integer.parseInt(exp), base);
                else
                    coefficients.put(Integer.parseInt(exp), -base);
            }else{
                if(!isBaseNegative)
                    coefficients.put(-Integer.parseInt(exp), base);
                else
                    coefficients.put(-Integer.parseInt(exp), -base);
            }

        }
        return true;
    }

    public Map<Integer, Integer> getCoefficients() {
        return coefficients;
    }

    public void setStringRepresentation(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String getStringRepresentation() {
        return stringRepresentation;
    }

    @Override
    public String toString() {
        if(stringRepresentation != null){
            return stringRepresentation;
        }
        String s = "0";
        if(isZero())
            return s;
        s = "";

        for(Map.Entry<Integer, Integer> entry : coefficients.entrySet()){
            if(entry.getValue() == 0)
                continue;
            if(entry.getValue() != 1)
                s += entry.getValue();
            if(entry.getKey() == 0)
                s += " + ";
            if(entry.getKey()  == 1)
                s += "x + ";
            if(entry.getKey() > 1|| entry.getKey() <= -1)
                s += "x^" + entry.getKey() + " + ";

        }
        if(Character.isDigit(s.charAt(s.length() - 1))){
            return s;
        }
        int i = s.length() - 1;
        while(!(Character.isDigit(s.charAt(i - 1)) || Character.isLetter(s.charAt(i - 1)))){
            i--;
        }
        return s.substring(0, i);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(obj instanceof SparsePolynomial)
            return getStringRepresentation().equals(obj.toString());
        return false;
    }
}