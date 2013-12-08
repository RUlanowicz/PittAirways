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
					case 3: //findPrice();
						break;
					case 4:	//findRoutes();
						break;
					case 5: //findRoutesWSeats();
						break;
					case 6: //addRes();
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
		
	}
}