import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustInt{
	private Statement statement; //used to create an instance of the connection
	private ResultSet resultSet; //used to hold the result of your query (if one exists)
	private String query;  //this will hold the query we are using
	private Connection connection;
	private Scanner in;
	
	public CustInt(Connection connectionc){
		connection = connectionc;
		in = new Scanner(System.in);
		int selection;
			do{
				System.out.printf("\nCustomer Interface\n1: Add A Customer\n2: Show Customer Info\n3: Find Price Between Cities\n4: Find Routes Between Cities\n5: Find Routes Between Cities with Seats\n6: Add A Reservation\n7: Check A Reservation\n8: Buy A Ticket Based on Existing Reservation\n0: Exit\n");
				do{
					selection = in.nextInt();	
				}while(selection != 1 && selection != 2 && selection != 3 && selection != 4 && selection != 5 && selection != 0 && selection != 6 && selection != 7 && selection != 8);
			
				switch(selection){
					case 1: addCust();
						break;
					case 2: showCustInfo();
						break;
					case 3: findPrice();
						break;
					case 4:	//findRoutes();
						break;
					case 5: //findRoutesWSeats();
						break;
					case 6: addRes();
						break;
					case 7: //showRes();
						break;
					case 8: //buyTicketWRes();
						break;
					case 0: System.out.println("Good Bye!");
						break;
				}
		}while(selection != 0);
	}
	
	private void addCust(){
		String sal, fname, lname, sname, city, state, phone_num, cc_num, cc_exp, email,cid="000000000";
		int temp;
		Pattern pattern;
		Matcher matcher;
		boolean nameFound=false;
		
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		String STATE_PATTERN = "^(?-i:A[LKSZRAEP]|C[AOT]|D[EC]|F[LM]|G[AU]|HI|I[ADLN]|K[SY]|LA|M[ADEHINOPST]|N[CDEHJMVY]|O[HKR]|P[ARW]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY])$";
		try{
			statement = connection.createStatement();
		}
		catch(SQLException SQLEx){
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}
		System.out.printf("Salutation: ");
		in.nextLine();
		sal = in.nextLine();
		System.out.printf("First Name: ");
		fname = in.nextLine();
		System.out.printf("Last Name: ");
		lname = in.nextLine();
		System.out.printf("Street Address: ");
		sname = in.nextLine();
		System.out.printf("City: ");
		city = in.nextLine();
		pattern = Pattern.compile(STATE_PATTERN);
		do{
			System.out.printf("State: ");
			state = in.nextLine();
			matcher = pattern.matcher(state);
		}while(matcher.matches() == false);
		System.out.printf("Phone Number: ");
		phone_num = in.nextLine();
		System.out.printf("Credit Card Number: ");
		cc_num = in.nextLine();
		System.out.printf("Credit Card Expiration Date(Format MM/YY): ");
		cc_exp = in.nextLine();
		pattern = Pattern.compile(EMAIL_PATTERN);
		do{
			System.out.printf("EMail Address: ");
			email = in.nextLine();
			matcher = pattern.matcher(email);
		}while(matcher.matches() == false);
		try{		
			do{
				temp = (int)(Math.random() * ((1000000000) + 1));
				cid = String.format("%09d",temp);
				resultSet = statement.executeQuery("SELECT COUNT(*) FROM CUSTOMER WHERE cid = '"+cid+"'");
				resultSet.next();
			}while(resultSet.getInt(1) >= 1);
			System.out.println();
			resultSet = statement.executeQuery("SELECT COUNT(*) FROM CUSTOMER WHERE first_name = '"+fname+"' AND last_name = '"+lname+"'");
			resultSet.next();
			if(resultSet.getInt(1) >= 1){
				nameFound = true;
			}
			if(nameFound == false){
				statement.executeQuery("INSERT INTO Customer VALUES('"+cid+"','"+sal+"','"+fname+"','"+lname+"','"+cc_num+"',"+"to_date('"+cc_exp+"','MM/YY'),'"+sname+"','"+city+"','"+state+"','"+phone_num+"','"+email+"')");
				System.out.println("Customer Inserted");
			}
			else{
				System.out.println("That name already exists in our database, data was not inserted");
			}
		}
		catch(SQLException SQLEx){
				System.out.println("SQL Exception");
				System.out.println(SQLEx.toString());
				SQLEx.printStackTrace();			
		}
	}
	
	private void showCustInfo(){
		String fname, lname;
		in.nextLine();
		System.out.printf("\nFirst Name: ");
		fname = in.nextLine();
		System.out.printf("Last Name: ");
		lname = in.nextLine();
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT COUNT(*) FROM CUSTOMER WHERE first_name = '"+fname+"' AND last_name = '"+lname+"'");
			resultSet.next();
			if(resultSet.getInt(1) >= 1){
				resultSet = statement.executeQuery("SELECT * FROM CUSTOMER WHERE first_name = '"+fname+"' AND last_name = '"+lname+"'");
				System.out.println();
				resultSet.next();
				System.out.println("PittRewards #: "+resultSet.getString(1));
				System.out.println("Name: "+resultSet.getString(2)+" "+resultSet.getString(3)+" "+resultSet.getString(4));
				System.out.println("Payment Info: "+resultSet.getString(5)+" Expires: "+resultSet.getString(6).substring(0,7));
				System.out.println("Billing Address: "+resultSet.getString(7)+" "+resultSet.getString(8)+", "+resultSet.getString(9));
				System.out.println("Contact: "+resultSet.getString(10)+" "+resultSet.getString(11));
			}
			else{
				System.out.println("That name doesn't exist, press 1 to become a registered user!");
			}
		}
		catch(SQLException SQLEx){
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
	}

	private void findPrice() {

		String cityA, cityB;
		int highAB, highBA, lowAB, lowBA;
		highAB = highBA = lowAB = lowBA = 0;

		in.nextLine();

		System.out.printf("City 1: ");
		cityA = in.nextLine();

		System.out.printf("City 2: ");
		cityB = in.nextLine();


		try{
			statement = connection.createStatement();

			//System.out.println("SELECT high_price, low_price FROM Price WHERE departure_city = '" + cityA + "' AND arrival_city = '" + cityB + "'");
			resultSet = statement.executeQuery("SELECT high_price, low_price FROM Price WHERE departure_city = '" + cityA + "' AND arrival_city = '" + cityB + "'");
			while(resultSet.next()) {
				highAB = resultSet.getInt(1);
				lowAB = resultSet.getInt(2);
				System.out.println(cityA + " ==> " + cityB + " highAB: " + highAB + " lowAB: " + lowAB);
			}

			resultSet = statement.executeQuery("SELECT high_price, low_price FROM Price WHERE departure_city = '" + cityB + "' AND arrival_city = '" + cityA + "'");
			while(resultSet.next()) {
				highBA = resultSet.getInt(1);
				lowBA = resultSet.getInt(2);
				System.out.println(cityB + " ==> " + cityA + " highBA: " + highBA + " lowBA: " + lowBA);
			}

			System.out.println("Round trip high price: " + (highAB + highBA));
			System.out.println("Round trip low price:  " + (lowAB  + lowBA));
		}catch(SQLException SQLEx) {
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}
	}

	private void addRes() {
		int leg = 1;
		int temp;
		String flightDateIn, flightNumberIn, resNum = "00000";
		boolean tryAgain;

		ArrayList <String> flightNumbers = new ArrayList <String> ();
		ArrayList <String> flightDates = new ArrayList <String> ();

		in.nextLine();
		do {
			tryAgain = false;
			System.out.printf("Leg: " + leg + "\n" + "Flight Number: ");
			flightNumberIn = in.nextLine();
			// make sure input is a valid flight number
			try {
				statement = connection.createStatement();

				resultSet = statement.executeQuery("SELECT COUNT(*) FROM Flight WHERE flight_number = '" + flightNumberIn + "'");
				while(resultSet.next()) {
					if(resultSet.getInt(1) == 0) {
						System.out.println("Not a valid Flight Number");
						tryAgain = true;
					}
				}
			}catch(SQLException SQLEx) {
				System.out.println("SQL Exception");
				System.out.println(SQLEx.toString());
				SQLEx.printStackTrace();
			}

			if(tryAgain) continue;

			System.out.printf("Flight Date: ");
			flightDateIn = in.nextLine();

			flightNumbers.add(flightNumberIn);
			flightDates.add(flightDateIn);

			if(flightNumberIn.compareTo("0") != 0) leg++;
		}while(flightNumberIn.compareTo("0") != 0);

		// cannot have 3 or more than 4 legs in trip
		if(leg == 3) {
			System.out.println("Sorry, cannot have 3 leg reservations");
			return;
		} else if(leg > 4) {
			System.out.println("Sorry, you have too many legs in your reservation");
			return;
		}

		// make sure there is room on all of the flights
		for(int i = 0; i < flightNumbers.size(); i++) {
			String fn = flightNumbers.get(i);
			if(freeSeats(fn) == 0) {
				System.out.println("Sorry, there are no seats available for flight number: " + fn);
				System.out.println("The reservation was not made");
				return;
			}
		}
		try{		
			do{
				temp = (int)(Math.random() * ((99998) + 1));
				resNum = String.format("%05d",temp);
				resultSet = statement.executeQuery("SELECT COUNT(*) FROM Reservation WHERE Reservation_number = '"+resNum+"'");
				resultSet.next();
			}while(resultSet.getInt(1) >= 1);
			System.out.println();
			
			
			//statement.executeQuery("INSERT INTO Customer VALUES('"+cid+"','"+sal+"','"+fname+"','"+lname+"','"+cc_num+"',"+"to_date('"+cc_exp+"','MM/YY'),'"+sname+"','"+city+"','"+state+"','"+phone_num+"','"+email+"')");
			//System.out.println("Customer Inserted");
			// insert into Reservation Table
			System.out.println("INSERT INTO Reservation(reservation_number, ticketed) VALUES('"+resNum+"', '0')");
			resultSet = statement.executeQuery("INSERT INTO Reservation(reservation_number, ticketed) VALUES('"+resNum+"', '0')");

			for(int i = 0; i < flightNumbers.size(); i++) {
				String fn = flightNumbers.get(i);
				leg = i + 1;
				String fd = flightDates.get(i);
				System.out.println("INSERT INTO Reservation_detail VALUES('"+resNum+"', '" +fn+"', to_date('"+fd+"','MM/DD/YYYY'), "+leg+")");
				resultSet = statement.executeQuery("INSERT INTO Reservation_detail VALUES('"+resNum+"', '" +fn+"', to_date('"+fd+"','MM/DD/YYYY'), "+leg+")");
			}
		} catch(SQLException SQLEx){
				System.out.println("SQL Exception");
				System.out.println(SQLEx.toString());
				SQLEx.printStackTrace();			
		}

		System.out.println("Reservation Number: " + resNum);
	}

	// return the free seats on a flight
	private int freeSeats(String flightNum) {
		int cap = 0;
		int curSize = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT plane_capacity FROM Plane NATURAL JOIN Flight WHERE flight_number = " + flightNum);
			while(resultSet.next()) {
				cap = resultSet.getInt(1);
			}

			resultSet = statement.executeQuery("SELECT COUNT(*) FROM Reservation_detail WHERE flight_number = " + flightNum);
			while(resultSet.next()) {
				curSize = resultSet.getInt(1);
			}
		}catch(SQLException SQLEx) {
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}	
		return cap - curSize;
		}
	}
}