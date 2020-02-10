/**
 * The Transaction class creates Transactions that have a date,
 * 	amount (in dollars), and description
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class Transaction{
	
	private String date; //The date of the transaction
	private String description; //Description of what the transaction is
	private double amount; //The amount of money used in the transaction

	/**
	 * Returns an instance of Transaction
	 * 
	 * @param date 
	 * Date the transaction was conducted
	 * @param amount
	 * Amount of money transacted
	 * @param description
	 * Description of transaction
	 */
	public Transaction(String date, double amount, String description){
		this.date = date;
		this.amount = amount;
		this.description = description;
	}

	/**
	 * @return
	 * Creates and returns a copy of this object
	 */
	public Object clone(){
		return new Transaction(this.date, this.amount, this.description);
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj
	 * The object to be compared
	 * 
	 * @return
	 * True if the transactions are equal to each other and false otherwise
	 */
	public boolean equals(Object obj){
		if(obj instanceof Transaction){
			if(this.amount == ((Transaction)obj).getAmount()){
				if(this.date.equals(((Transaction)obj).getDate())){
					if(this.description.equals(((Transaction)obj).getDescription())){
						return true;
					}
				}
			}
		}
			return false;
	}

	/**
	 * @return
	 * Returns a string representation of the object
	 */
	public String toString(){
		if(amount < 0){
            return String.format("%-7s%-14s%-7s%-7s%-7s", "", date, "", Math.abs(amount), description);
        }else{
            return String.format("%-7s%-14s%-7s%-7s%-7s", "", date, amount, "" , description);
        }
	}

	/**
	 * Checks if the date has a legal format of YYYY/MM/DD
	 * 
	 * @return
	 * Returns true if the date has a legal format and false otherwise
	 */
	public boolean hasLegalDate(){
		boolean correctDateFormat = true;
		for(int i = 0; i < date.length(); i++){
            if(i == 4 || i == 7){
                i++;
            }
            if(!(date.charAt(i) >= '0' && date.charAt(i) <= '9')){
                correctDateFormat = false;
            }
        }

        if(correctDateFormat){
            if(!((this.getYear() >= 1900 && this.getYear() <= 2050)
            && (this.getMonth() >= 1 && this.getMonth() <= 12)
            && (this.getDay() >= 1 && this.getDay() <= 30))){
                correctDateFormat = false;
            }
		} 
		
		return correctDateFormat;
	}

	/**
	 * @return
	 * Returns the date the transaction was conducted in the format YYYY/MM/DD
	 */
	public String getDate(){
		return date;
	}

	/**
	 * @return
	 * Returns the year the transaction was conducted
	 */
	public int getYear(){
		return Integer.parseInt(date.substring(0,4));
	}

	/**
	 * @return
	 * Returns the month the transaction was conducted
	 */
	public int getMonth(){
		return Integer.parseInt(date.substring(5,7));
	}

	/**
	 * @return
	 * Returns the day the transaction was conducted
	 */
	public int getDay(){
		return Integer.parseInt(date.substring(8));

	}

	/** 
	 * @return
	 * Returns the description of the transaction
	 */
	public String getDescription(){
		return description;
	}

	/**
	 * @return
	 * Returns the amount of money transacted in dollars
	 */
	public double getAmount(){
		return amount;
	}
}