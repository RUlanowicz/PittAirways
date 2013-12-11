/*
Written By
Zach Liss - zll1@pitt.edu
Ryan Ulanowicz - rru3@pitt.edu
12/10/2013
*/

import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class PittAirway{
	private Connection connection; //used to hold the jdbc connection to the DB
	private String username = ""; //This is your username in oracle
	private String password = ""; //This is your password in oracle
	
	public PittAirway(){
		Scanner in = new Scanner(System.in);
		int userType;
		try{
			//Register the oracle driver.  This needs the oracle files provided
			//in the oracle.zip file, unzipped into the local directory and 
			//the class path set to include the local directory
			DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
			String url = "jdbc:oracle:thin:@:dbclass"; //This is the location of the database.  This is the database in oracle provided to the class
			connection = DriverManager.getConnection(url, username, password); 
			//create a connection to DB on db10.cs.pitt.edu
		}
		catch(Exception Ex)  //What to do with any exceptions
		{
			System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
			Ex.printStackTrace();
		}
		System.out.println("Welcome to PittAirways! The Dopest of the Pretend College Airways");
		do{
			System.out.println("Customer [1] or Administrator [2]");
			userType = in.nextInt();
		}while(userType != 1 && userType !=2);
		if(userType == 2){ 
			AdminInt admin_int = new AdminInt(connection);
		}
		else if(userType == 1){
			CustInt cust_int = new CustInt(connection);
		}
		try{
			connection.close();
		}
		catch(Exception Ex){
			System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
			Ex.printStackTrace();
		}
			
	}
	public static void main(String[] args){	
		PittAirway pittAirway = new PittAirway();
		System.out.println(".\"\".");
   		System.out.println("|  |");
		System.out.println("|  |");
		System.out.println("|  |");
		System.out.println("|  |--.--._ ");
		System.out.println("|  | _|  | `|");
		System.out.println("|  /` )  |  |");
		System.out.println("| /  /'--:__/");
		System.out.println("|/  /       |");
		System.out.println("(  ' \\      |");
    	System.out.println("\\    `.   /");
     	System.out.println(" |       |");
	 	System.out.println(" |       |");
	}
}