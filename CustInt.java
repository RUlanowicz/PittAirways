/*
Written By
Zach Liss - zll1@pitt.edu
Ryan Ulanowicz - rru3@pitt.edu
12/10/2013
*/

import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustInt{
	private Statement statement, batchStatement; //used to create an instance of the connection
	private ResultSet resultSet; //used to hold the result of your query (if one exists)
	private String query;  //this will hold the query we are using
	private Connection connection;
	private Scanner in;
	private CallableStatement cs;
	private Pattern pattern;
	private Matcher matcher;
	private String DATE_PATTERN = "((?:[0]?[1-9]|[1][012])[-:\\/.](?:(?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/.](?:(?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3})))(?![\\d])";
	
	//constructor that takes user selection and takes in the connection object and starts the task of the users choosing
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
					case 4:	findRoutes();
						break;
					case 5: findRoutesWSeats();
						break;
					case 6: addRes();
						break;
					case 7: showRes();
						break;
					case 8: buyTicketWRes();
						break;
					case 0: System.out.println("Good Bye From Group One!");
						break;
				}
		}while(selection != 0);
	}
	
	//Adds a customer to the system (Task 1)
	private void addCust(){
		String sal, fname, lname, sname, city, state, phone_num, cc_num, cc_exp, email,cid="000000000";
		int temp;
		boolean nameFound=false;
		
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		String STATE_PATTERN = "^(?-i:A[LKSZRAEP]|C[AOT]|D[EC]|F[LM]|G[AU]|HI|I[ADLN]|K[SY]|LA|M[ADEHINOPST]|N[CDEHJMVY]|O[HKR]|P[ARW]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY])$";
		String CARD_EXP_PATTERN = "((?:(?:0[1-9])|(?:1[0-2]))\\/(?:\\d{2}))(?![\\d])";
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
		pattern = Pattern.compile(CARD_EXP_PATTERN);
		do{
			System.out.printf("Credit Card Expiration Date(Format MM/YY): ");
			cc_exp = in.nextLine();
			matcher = pattern.matcher(cc_exp);
		}while(matcher.matches() == false);
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
	
	//prints out information on a customer, based on their first and last name (Task 2)
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
	}
	
	//Shows a previously booked reservation based on the reservation number (Task 7)
	private void showRes(){
		String resNum;
		in.nextLine();
		System.out.printf("Reservation Number: ");
		resNum = in.nextLine();
		query = "SELECT COUNT(*) FROM RESERVATION WHERE reservation_number = '"+resNum+"'";
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			resultSet.next();
			if(resultSet.getInt(1) == 0){
				System.out.println("That reservation number does not exist, please check it and try again!");
			}
			else{
				query = "SELECT * FROM Reservation_detail WHERE reservation_number = '"+resNum+"' ORDER BY LEG ASC";
				resultSet = statement.executeQuery(query);
				while(resultSet.next()){
					System.out.println("Flight Number: "+resultSet.getString(2)+" Flight Date: "+resultSet.getString(3).substring(0,10)+" Leg: "+resultSet.getString(4));
				}	
			}
		}
		catch(SQLException SQLEx){
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}
	}
	
	//Moves a users reservation to ticketed, so that they are able to take the trip (Task 8)
	private void buyTicketWRes(){
		String resNum;
		in.nextLine();
		System.out.printf("Reservation Number: ");
		resNum = in.nextLine();
		query = "SELECT COUNT(*) FROM RESERVATION WHERE reservation_number = '"+resNum+"'";
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			resultSet.next();
			if(resultSet.getInt(1) >= 1){
				query = "UPDATE RESERVATION SET ticketed = '1' WHERE reservation_number = '"+resNum+"'";
				statement.executeQuery(query);
			}
			System.out.println("You're now ready to fly! Enjoy your trip!");
		}
		catch(SQLException SQLEx){
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}
	}
	
	//Shows user possible ways to get from Point A - Point B (Task 5)
	private void findRoutes(){
		String depCity, arvCity;
		in.nextLine();
		System.out.print("Departure City: ");
		depCity = in.nextLine();
		System.out.print("Arrival City: ");
		arvCity = in.nextLine();
		String scheduleA, scheduleB;
		boolean sked, hour;
		int counter = 1;
		int timeA, timeB;
		try{	
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT COUNT(*) FROM Flight WHERE departure_city = '"+depCity+"' AND arrival_city = '"+arvCity+"'");
			resultSet.next();
			if(resultSet.getInt(1) >= 1){
				resultSet = statement.executeQuery("SELECT flight_number, departure_time, arrival_time FROM Flight WHERE departure_city = '"+depCity+"' AND arrival_city = '"+arvCity+"'");
				resultSet.next();
				System.out.println("Route [1]");
				System.out.println("	Flight Number: "+resultSet.getString(1)+" Departure City: "+depCity+" Departure Time: "+resultSet.getString(2)+" ==> Arrival City: "+arvCity+" Arrival Time: "+resultSet.getString(3));
				counter++;
			}
			resultSet = statement.executeQuery("SELECT DISTINCT A.departure_city, A.arrival_city, B.departure_city, B.arrival_city, A.weekly_schedule, B.weekly_schedule, A.arrival_time, B.arrival_time, A.departure_time, B.departure_time, A.flight_number, B.flight_number FROM Flight A CROSS JOIN Flight B WHERE A.departure_city = '"+depCity+"' AND A.arrival_city = B.departure_city AND B.arrival_city = '"+arvCity+"'");
			while(resultSet.next()){
				sked = compareSchedules(resultSet.getString(6),resultSet.getString(5));
				hour = compareTime(resultSet.getString(7),resultSet.getString(10));
				if(sked && hour){
					System.out.println("Route ["+counter+"]");
					System.out.println("	Flight[1]: Flight Number: "+resultSet.getString(11)+" Departure City: "+depCity+" Departure Time: "+resultSet.getString(9)+" ==> Arrival City: "+resultSet.getString(2)+" Arrival Time: "+resultSet.getString(7));
					System.out.println("	Flight[2]: Flight Number: "+resultSet.getString(12)+" Departure City: "+resultSet.getString(3)+ " Departure Time: "+resultSet.getString(10)+" ==> Arrival City: "+arvCity+" Arrival Time: "+resultSet.getString(8));
				}
			}
		
		}
		catch(SQLException SQLEx){
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();	
		}
	}
	
	private boolean compareSchedules(String s1, String s2){
		for(int i = 0; i < 7; i++) {
			if(s1.charAt(i) == s2.charAt(i)) {
				return true;
			}
		}
			return false;
	}

	private boolean compareTime(String t1, String t2){
		if(Integer.parseInt(t2) - Integer.parseInt(t1) >= 100) return true;
		return false;
	}
	
	//finds all available routes that can be taken based on the tickets that have been issued, or whether or not the plane can be enlarged (Task 5)
	private void findRoutesWSeats(){
		String depCity, arvCity, fDate;
		in.nextLine();
		System.out.print("Departure City: ");
		depCity = in.nextLine();
		System.out.print("Arrival City: ");
		arvCity = in.nextLine();
		pattern = Pattern.compile(DATE_PATTERN);
		do{
			System.out.print("Flight Date (Format MM/DD/YYYY): ");
			fDate = in.nextLine();
			matcher = pattern.matcher(fDate);
		}while(matcher.matches() == false);
		String scheduleA, scheduleB;
		boolean sked, hour;
		int counter = 1;
		int timeA, timeB;
		try{	
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT COUNT(*) FROM Flight WHERE departure_city = '"+depCity+"' AND arrival_city = '"+arvCity+"'");
			resultSet.next();
			if(resultSet.getInt(1) >= 1){
				resultSet = statement.executeQuery("SELECT flight_number, departure_time, arrival_time FROM Flight WHERE departure_city = '"+depCity+"' AND arrival_city = '"+arvCity+"'");
				resultSet.next();
				String f1 = resultSet.getString(1);
				String dt = resultSet.getString(2);
				String at = resultSet.getString(3);
				if(freeSeats(f1) > 0 || (freeSeats(f1) == 0 && enlargePlaneBool(f1))) {
					System.out.println("Route [1]");
					System.out.println("	Flight Number: "+f1+" Departure City: "+depCity+" Departure Time: "+dt+" ==> Arrival City: "+arvCity+" Arrival Time: "+at);
					counter++;
				}
			}
			
			resultSet = statement.executeQuery("SELECT COUNT(*) FROM Flight A CROSS JOIN Flight B WHERE A.departure_city = '"+depCity+"' AND A.arrival_city = B.departure_city AND B.arrival_city = '"+arvCity+"'");
			resultSet.next();
			if(resultSet.getInt(1) >= 1) {
			
				resultSet = statement.executeQuery("SELECT DISTINCT A.departure_city, A.arrival_city, B.departure_city, B.arrival_city, A.weekly_schedule, B.weekly_schedule, A.arrival_time, B.arrival_time, A.departure_time, B.departure_time, A.flight_number, B.flight_number FROM Flight A CROSS JOIN Flight B WHERE A.departure_city = '"+depCity+"' AND A.arrival_city = B.departure_city AND B.arrival_city = '"+arvCity+"'");
				while(resultSet.next()){
					String fA = resultSet.getString(11);
					String fB = resultSet.getString(12);
					String schedB = resultSet.getString(6);
					String schedA = resultSet.getString(5);
					String depTimeB = resultSet.getString(10);
					String arvTimeA = resultSet.getString(7);
					String depCityB = resultSet.getString(3);
					String depTimeA = resultSet.getString(9);
					String arvCityA = resultSet.getString(2);
					String arvTimeB = resultSet.getString(8);
					if((freeSeats(fA) >0 || (freeSeats(fA) == 0 && enlargePlaneBool(fA))) && (freeSeats(fB) >0 || (freeSeats(fB) == 0 && enlargePlaneBool(fB)))){
						sked = compareSchedules(schedB,schedA);
						hour = compareTime(arvTimeA,depTimeB);
						if(sked && hour){
							System.out.println("Route ["+counter+"]");
							System.out.println("	Flight[1]: Flight Number: "+fA+" Departure City: "+depCity+" Departure Time: "+depTimeA+" ==> Arrival City: "+arvCityA+" Arrival Time: "+arvTimeA);
							System.out.println("	Flight[2]: Flight Number: "+fB+" Departure City: "+depCityB+ " Departure Time: "+depTimeB+" ==> Arrival City: "+arvCity+" Arrival Time: "+arvTimeB);
						}
					}
				}
			}
		}
		catch(SQLException SQLEx){
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();	
		}
		
	}
	//Function that returns the number of free seats on a given flight
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
			while(resultSet.next()){
				curSize = resultSet.getInt(1);
			}
		}
		catch(SQLException SQLEx){
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}	
		return cap - curSize;
	}
	
	//Function that returns you are able to enlarge the plane of a given flight to give more seats
	private boolean enlargePlaneBool(String flightNum) {
		String startType = null, newType = null;
		boolean planeFound = false;
		int rank;

		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT plane_type FROM Flight WHERE flight_number = '"+flightNum+"'");
			while(resultSet.next()) {
				startType = resultSet.getString(1);
			}
		}
		catch(SQLException SQLEx){
			System.out.println("poop");
		}
		try{
			resultSet = statement.executeQuery("SELECT plane_type FROM Plane ORDER BY plane_capacity ASC");
			while(resultSet.next()) {
				if(startType.compareTo(resultSet.getString(1)) == 0){
					planeFound = true; 
				} 
				else if(planeFound) {
					return true;
				}
			}
		}
		catch(SQLException SQLEx) {
			System.out.println("fjkjkafj");
		}
		return false;
	}
	
	//Create reservation based on information given by user (Task 6)
	private void addRes() {
		int leg = 1;
		int temp;
		String fname, lname, flightDateIn, flightNumberIn, resNum = "00000", cid = "000000000", cdate = "";
		boolean tryAgain, nameFound;
		
		try{ 
			statement = connection.createStatement();
			batchStatement = connection.createStatement();
		} catch(SQLException SQLEx) {
			System.out.println(SQLEx.toString()); 
			SQLEx.printStackTrace(); 
		}

		// generate reservation number
		try{		
			do{
				temp = (int)(Math.random() * ((99998) + 1));
				resNum = String.format("%05d",temp);
				resultSet = statement.executeQuery("SELECT COUNT(*) FROM Reservation WHERE Reservation_number = '"+resNum+"'");
				resultSet.next();
			} while(resultSet.getInt(1) >= 1);
			System.out.println();
		}catch(SQLException SQLEx) {
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}

		// get first and last name and return if they are not in the db
		in.nextLine();
	
		nameFound = false;
		System.out.printf("First Name: ");
		fname = in.nextLine();
		System.out.printf("Last Name: ");
		lname = in.nextLine();

		try {
			resultSet = statement.executeQuery("SELECT COUNT(*) FROM CUSTOMER WHERE first_name = '"+fname+"' AND last_name = '"+lname+"'");
			resultSet.next();
			if(resultSet.getInt(1) >= 1){
				nameFound = true;
			}
		}catch(SQLException SQLEx) {
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}

		if(!nameFound) {
			System.out.println("You are not a registered customer with PittAirways. Try the Add Customer function first!");
			return;
		}

		// grab the cid for the customer and the cdate
		try {
			resultSet = statement.executeQuery("SELECT cid FROM Customer WHERE first_name ='"+fname+"' AND last_name = '"+lname+"'");
			while(resultSet.next()) {
				cid = resultSet.getString(1);
				System.out.println("Customer Id: " + cid);
			}

			resultSet = statement.executeQuery("SELECT c_date FROM our_sys_time");
			while(resultSet.next()) {
				cdate = resultSet.getString(1);
				System.out.println("C_Date:      " + cdate);
			}
		}catch(SQLException SQLEx) {
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}

		do {
			tryAgain = false;
			System.out.printf("Leg: " + leg + "\n" + "Flight Number: ");
			flightNumberIn = in.nextLine();
			// make sure input is a valid flight number
			try {
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

			pattern = Pattern.compile(DATE_PATTERN);
			do{
				System.out.print("Flight Date (Format MM/DD/YYYY): ");
				flightDateIn = in.nextLine();
				matcher = pattern.matcher(flightDateIn);
			}while(matcher.matches() == false);

			try {
				batchStatement.addBatch("INSERT INTO Reservation_detail VALUES('"+resNum+"', '" +flightNumberIn+"', to_date('"+flightDateIn+"','MM/DD/YYYY'), "+leg+")");
			}catch(SQLException SQLEx) {
				System.out.println("SQL Exception ==> error adding to batch");
				System.out.println(SQLEx.toString());
				SQLEx.printStackTrace();
			}

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

		// insert the reservation and the reservation_detail info
		try{
			System.out.println("INSERT INTO Reservation(reservation_number, cid, reservation_date, ticketed) VALUES('"+resNum+"', '"+cid+"', to_date('"+cdate.substring(0,10)+"', 'YYYY-MM-DD'), '0')");
			connection.setAutoCommit(false);
			resultSet = statement.executeQuery("INSERT INTO Reservation(reservation_number, cid, reservation_date, ticketed) VALUES('"+resNum+"', '"+cid+"', to_date('"+cdate.substring(0,10)+"', 'YYYY-MM-DD'), '0')");

			batchStatement.executeBatch();
			connection.commit();
			connection.setAutoCommit(true);
		} catch(SQLException SQLEx){
				try {
				connection.rollback();
				connection.setAutoCommit(true);
				} catch(SQLException Ex) {
					System.out.println("SQL Exception");
					System.out.println(Ex.toString());
					SQLEx.printStackTrace();
				}
			System.out.println("There are no available seates for one of your flights and the plane could not be upgraded to a larger size");
			return;
		}

		// set the cost of the reservation
		try{
			cs = connection.prepareCall("{call setCost(?)}");
			cs.setString(1,resNum);
			cs.executeQuery();
		}catch(SQLException SQLEx) {
			System.out.println("SQL Exception ==> When trying to set cost");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}

		System.out.println("Reservation has been placed!");
		System.out.println("Reservation Number: " + resNum);
	}
	
	//Gives the user the high-low price of flights between given cities, and for round trip tickets (Task 3)
	private void findPrice() {

		String cityA, cityB;
		String AIRPORT_PATTERN = "((?:[A-Z][A-Z]+))";
		int highAB, highBA, lowAB, lowBA;
		highAB = highBA = lowAB = lowBA = 0;
		in.nextLine();
		pattern = Pattern.compile(AIRPORT_PATTERN);
		System.out.println();
		System.out.println("Input the cities airpot codes (Ex. PIT, JFK, etc.)");
		do{
		   System.out.printf("City 1: ");
		   cityA = in.nextLine();
		   matcher = pattern.matcher(cityA);
		}while(matcher.matches() == false);
		do{
		   System.out.printf("City 2: ");
		   cityB = in.nextLine();
		   matcher = pattern.matcher(cityB);
		}while(matcher.matches() == false);
		System.out.println();
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT high_price, low_price FROM Price WHERE departure_city = '" + cityA + "' AND arrival_city = '" + cityB + "'");
			while(resultSet.next()) {
				highAB = resultSet.getInt(1);
				lowAB = resultSet.getInt(2);
				System.out.println("Departure City: "+cityA+" Arrival City: " + cityB);
				System.out.println("	High(Same Day) Price: " + highAB + " Low(Multi-Day) Price: " + lowAB);
			}
			System.out.println();
			resultSet = statement.executeQuery("SELECT high_price, low_price FROM Price WHERE departure_city = '" + cityB + "' AND arrival_city = '" + cityA + "'");
			while(resultSet.next()) {
				highBA = resultSet.getInt(1);
				lowBA = resultSet.getInt(2);
				System.out.println("Departure City: "+cityB+" Arrival City: " + cityA);
				System.out.println("	High(Same Day) Price: "+highBA+" Low(Multi-Day) Price: " + lowBA);
			}
			System.out.println();
			System.out.println("Same-Day (High) Round-Trip Price: " + (highAB + highBA));
			System.out.println("Multi-Day (Low) Round-Trip Price:  " + (lowAB  + lowBA));
		}catch(SQLException SQLEx) {
			System.out.println("SQL Exception");
			System.out.println(SQLEx.toString());
			SQLEx.printStackTrace();
		}
	}	
}