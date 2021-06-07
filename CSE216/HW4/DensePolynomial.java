import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DensePolynomial implements Polynomial{

    private int[] coefficients;
    private String stringRepresentation;

    public DensePolynomial(String polynomial) throws IllegalArgumentException{
        if(polynomial == null)
            throw new IllegalArgumentException();
        stringRepresentation = polynomial;
        if(!wellFormed() || polynomial.equals(""))
            throw new IllegalArgumentException("This polynomial is not well-formed!");
    }

    private DensePolynomial(int[] coefficients){
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
        if(isZero()){
            return 0;
        }
        return coefficients.length - 1;
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
        if(isZero() || d >= coefficients.length || d < 0){
            return 0;
        }
        return coefficients[d];
    }

    /**
     * @return true if the polynomial represents the zero constant
     */
    @Override
    public boolean isZero() {
        int i = 0;
        while(i < coefficients.length){
            if(coefficients[i] != 0){
                 return false;
            }
            i++;
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
     * @throws IllegalArgumentException if q is a SparsePolynomial with negative exponents
     */
    public Polynomial add(Polynomial q) throws NullPointerException, IllegalArgumentException{
        if(q == null){
            throw new NullPointerException("Please provide another operand!");
        }

        int max = Math.max(degree(), q.degree()) + 1;
        int[] newCoefficients = new int[max];

        if(q instanceof DensePolynomial) {
            for(int i = 0; i < max; i++){
                if(i < coefficients.length){
                    newCoefficients[i] = coefficients[i];
                }
                if(i < ((DensePolynomial) q).getCoefficients().length){
                    newCoefficients[i] += ((DensePolynomial) q).getCoefficients()[i];
                }
            }
            return new DensePolynomial(newCoefficients);
        }
        if(q instanceof SparsePolynomial) {
            //non-negative
            if(hasNegativeExponent((SparsePolynomial)q) == null){
                for(int i = 0; i < max; i++){
                    if(i < coefficients.length){
                        newCoefficients[i] = coefficients[i];
                    }
                    if(((SparsePolynomial) q).getCoefficients().containsKey(i)){
                        newCoefficients[i] += ((SparsePolynomial) q).getCoefficients().get(i);
                    }
                }
                return new DensePolynomial(newCoefficients);
            }else{
                throw new IllegalArgumentException("An operand has a negative exponent!");
            }
        }

        return null;
    }

    /**
     * Returns a polynomial by multiplying the parameter with the current instance.  Neither the current instance nor
     * the parameter are modified.
     *
     * @param q the polynomial to multiply with <code>this</code>
     * @return <code>this * </code>q
     * @throws NullPointerException if q is null
     * @throws IllegalArgumentException if <code>this *</code> has negative exponents
     */
    @Override
    public Polynomial multiply(Polynomial q) throws NullPointerException, IllegalArgumentException{
        if(q == null){
            throw new NullPointerException("Please provide another operand!");
        }

        int max = degree()+q.degree() + 1;
        int[] newCoefficients = new int[max];
        DensePolynomial p = new DensePolynomial("0");
        DensePolynomial[] polynomials = new DensePolynomial[degree() + 1];

        if(q instanceof DensePolynomial){
            int[] qCoefficient = ((DensePolynomial) q).getCoefficients();

            for (int i = 0; i <= degree(); i++){
                for(int j = 0; j <= q.degree(); j++){
                    newCoefficients[i+j] = coefficients[i]*qCoefficient[j];
                }
                polynomials[i] = new DensePolynomial(newCoefficients);
                newCoefficients = clearCoefficients(newCoefficients);
            }

            for(DensePolynomial x : polynomials){
                if (x.isZero())
                    continue;
                p = (DensePolynomial) p.add(x);
            }

            return p;
        }
        if(q instanceof SparsePolynomial){
            //non-negative
            int[] negativeExponents = hasNegativeExponent((SparsePolynomial) q);
            if(negativeExponents == null){
                for(int i = 0; i <= degree(); i++){
                    for(int j = 0; j <= q.degree(); j++){
                        if(!((SparsePolynomial) q).getCoefficients().containsKey(j)){
                            newCoefficients[i+j] = 0;
                        }else{
                            newCoefficients[i+j] = coefficients[i]*((SparsePolynomial) q).getCoefficients().get(j);
                        }
                    }
                    polynomials[i] = new DensePolynomial(newCoefficients);
                    newCoefficients = clearCoefficients(newCoefficients);
                }

                for(DensePolynomial x : polynomials) {
                    if (x.isZero())
                        continue;
                    p = (DensePolynomial) p.add(x);
                }
            }else{
                int lowestPower = Arrays.stream(negativeExponents).min().getAsInt();
                for (int negativeExponent : negativeExponents) {
                    for (int j = 0; j < degree(); j++) {
                        if (getCoefficient(j) != 0) {
                            if (negativeExponent + j < 0) {
                                throw new IllegalArgumentException("A DensePolynomial cannot have negative exponents!");
                            }
                        }
                    }
                }

                for (int i = 0; i <= degree(); i++){
                    for(int j = lowestPower; j <= q.degree(); j++){
                        if(((SparsePolynomial) q).getCoefficients().get(j) != null && coefficients[i] != 0){
                            newCoefficients[i+j] = coefficients[i]*((SparsePolynomial) q).getCoefficients().get(j);
                        }
                    }
                    polynomials[i] = new DensePolynomial(newCoefficients);
                    newCoefficients = clearCoefficients(newCoefficients);
                }

                for(DensePolynomial x : polynomials) {
                    if (x == null || x.isZero())
                        continue;
                    p = (DensePolynomial) p.add(x);
                }

            }
            return p;
        }

        return null;
    }

    /**
     * Returns a  polynomial by subtracting the parameter from the current instance. Neither the current instance nor
     * the parameter are modified.
     *
     * @param q the non-null polynomial to subtract from <code>this</code>
     * @return <code>this - </code>q
     * @throws NullPointerException if q is null
     * @throws IllegalArgumentException if q is a SparsePolynomial with negative exponents
     */
    @Override
    public Polynomial subtract(Polynomial q) throws NullPointerException, IllegalArgumentException{
        if(q == null){
            throw new NullPointerException("Please provide another operand!");
        }

        int max = Math.max(degree(), q.degree()) + 1;
        int[] newCoefficients = new int[max];

        if(q instanceof DensePolynomial) {
            for(int i = 0; i < max; i++){
                if(i < coefficients.length){
                    newCoefficients[i] = coefficients[i];
                }
                if(i < ((DensePolynomial) q).getCoefficients().length){
                    newCoefficients[i] -= ((DensePolynomial) q).getCoefficients()[i];
                }
            }
            return new DensePolynomial(newCoefficients);
        }
        if(q instanceof SparsePolynomial) {
            //non-negative
            if(hasNegativeExponent((SparsePolynomial)q) == null){
                for(int i = 0; i < max; i++){
                    if(i < coefficients.length){
                        newCoefficients[i] = coefficients[i];
                    }
                    if(((SparsePolynomial) q).getCoefficients().containsKey(i)){
                        newCoefficients[i] -= ((SparsePolynomial) q).getCoefficients().get(i);
                    }
                }
                return new DensePolynomial(newCoefficients);
            }else{
                throw new IllegalArgumentException("An operand has a negative exponent!");
            }
        }

        return null;
    }

    /**
     * Returns a polynomial by negating the current instance. The current instance is not modified.
     *
     * @return -this
     */
    @Override
    public Polynomial minus() {
        int[] negatedCoefficients = new int[coefficients.length];
        for(int i = 0; i < coefficients.length; i++){
            negatedCoefficients[i] = -coefficients[i];
        }

        return new DensePolynomial(negatedCoefficients);
    }

    /**
     * Checks whether a SparsePolynomial has a negative exponent
     *
     * @param p the non-null SparsePolynomial to be checked
     * @return the list of negative exponents or null if there are none
     */
    private static int[] hasNegativeExponent(SparsePolynomial p){
        assert  p != null;

        int[] exponents = new int[p.degree() + 1];
        int i = 0;
        boolean flag = false;
        for(Map.Entry<Integer, Integer> entry : p.getCoefficients().entrySet()){
            if(entry.getKey() < 0){
                exponents[i++] = entry.getKey();
                flag = true;
            }

        }
        if(flag){
            return exponents;
        }
        return null;
    }

    /**
     * Returns an empty int[] array with the same length as coefficients
     *
     * @param coefficients the array to be replaced
     * @return an empty array with the length of coefficients
     */
    private static int[] clearCoefficients(int[] coefficients){
        return new int[coefficients.length];
    }

    /**
     * Checks if the class invariant holds for the current instance. If the polynomial is wellformed, the coefficients
     * are stored in the coefficients instance variable.
     *
     * @return {@literal true} if the class invariant holds, and {@literal false} otherwise.
     */
    @Override
    public boolean wellFormed() {
        if(stringRepresentation == null){
            return false;
        }

        int numOperands = 0;
        String decimalCheck = "\\.";
        Pattern pattern = Pattern.compile(decimalCheck);
        Matcher matcher = pattern.matcher(stringRepresentation);
        if(matcher.find()){
            return false;
        }

        String zeroCheck = "\\s0";
        pattern = Pattern.compile(zeroCheck);
        matcher = pattern.matcher(stringRepresentation);


        String operands = "(-?\\d*x\\^?-?\\d*)|(-?\\d+)";
        pattern = Pattern.compile(operands);
        matcher = pattern.matcher(stringRepresentation);

        while(matcher.find()){
            numOperands++;
        }

        ArrayList<Integer> coefficients = new ArrayList<>(numOperands);
        ArrayList<Integer> exponents = new ArrayList<>(numOperands);
        matcher = pattern.matcher(stringRepresentation);

        String s, num, exp;
        boolean passedVariable, isBaseNegative;

        //start


        while(matcher.find()){
            s = matcher.group();
            num = ""; exp = "0";
            passedVariable = false; isBaseNegative = false;

            for(int i = 0; i < s.length(); i++){
                while(i < s.length() && !Character.isDigit(s.charAt(i))){
                    if(s.charAt(i) == 'x'){
                        passedVariable = true;
                    }
                    else{
                        if(!passedVariable) {
                            if (s.charAt(i) == '-')
                                isBaseNegative = true;
                        }else {
                            if (s.charAt(i) == '-')
                                return false;
                        }
                    }
                    i++;
                }

                if(!passedVariable){
                    num += s.charAt(i);
                }else{
                    if(!(i < s.length())){
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

            if(!isBaseNegative)
                    coefficients.add(base);
            else
                    coefficients.add(-base);

            exponents.add(Integer.parseInt(exp));
            if(exponents.get(exponents.size() - 1) < 0){
                return false;
            }
        }

        this.coefficients = new int[exponents.get(0) + 1];

        for(int i = coefficients.size() - 1; i >= 0; i--){
            this.coefficients[exponents.get(i)] = coefficients.get(i);
        }


        //end
        return true;
    }

    public int[] getCoefficients() {
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

        if(isZero()){
            return s;
        }
        s = "";
        for(int i = coefficients.length - 1; i >= 0; i--){
            if(coefficients[i] == 0)
                continue;
            if(coefficients[i] != 1)
                s += coefficients[i];
            if(i == 1)
                s += "x + ";
            if(i > 1)
                s += "x^" + i + " + ";
        }

        int i = s.length();
        while(!(Character.isDigit(s.charAt(i - 1)) || Character.isLetter(s.charAt(i - 1)))){
            //i > 0
            i--;
        }
        return s.substring(0, i);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(obj instanceof DensePolynomial)
            return getStringRepresentation().equals(obj.toString());
        return false;
    }

}