import java.sql.*;
import java.util.*;
import java.io.*;

public class AdminInt{
		private Statement statement; //used to create an instance of the connection
		private ResultSet resultSet; //used to hold the result of your query (if one exists)
		private String query;  //this will hold the query we are using
		private Connection connection;
		private Scanner in;
		
		public AdminInt(Connection connectionc) {
			connection = connectionc;
			in = new Scanner(System.in);
			int selection;
			do{
				System.out.printf("\nAdmin Interface\n1: Erase the database\n2: Load schedule information\n3: Load pricing information\n4: Load plane information\n5: Generate passenger manifest for specific flight on given day\n0: Exit\n");
				do{
					selection = in.nextInt();	
				}while(selection != 1 && selection != 2 && selection != 3 && selection != 4 && selection != 5 && selection != 0);
			
				switch(selection){
					case 1:
						eraseDB();
						break;
					case 2: loadSchedule();
						break;
					case 3: loadPrice();
						break;
					case 4:	loadPlane();
						break;
					case 5: generateManifest();
						break;
					case 0: System.out.println("Good Bye!");
						break;
				}
			}while(selection != 0);
		}
		
		
		private void eraseDB(){
			int dec;
			do{
				System.out.println("Are you sure that you want to delete ALL data, enter 1 to DELETE press 0 for NO");
				dec = in.nextInt();
			}while(dec != 0 && dec != 1);
			//System.out.println("HEY");
			if(dec == 0) return;
			try{
				statement = connection.createStatement();
				statement.addBatch("DELETE FROM CDate");
				statement.addBatch("DELETE FROM Reservation_detail");
				statement.addBatch("DELETE FROM Reservation");
				statement.addBatch("DELETE FROM Flight");
				statement.addBatch("DELETE FROM Price");
				statement.addBatch("DELETE FROM Plane");
				statement.addBatch("DELETE FROM Customer");
				statement.executeBatch();
				statement.clearBatch();
				System.out.println("Database Deleted");
			}
			catch(Exception Ex){
				Ex.toString();
				Ex.printStackTrace();
			}
			
			
		}
		
		private void loadSchedule(){
			String fileName;
			System.out.println("What is the name of the file containing the schedule?");
			fileName = in.next();
			try{
				String fline;
				File fp = new File(fileName);
				Scanner fn = new Scanner(fp);
				statement = connection.createStatement();
				while(fn.hasNextLine()){
					fline = fn.nextLine();
					if(fline.length() == 0) break;
					String[] broken = fline.split(" ");
					//System.out.println(Arrays.toString(broken));
					System.out.println("INSERT INTO Flight VALUES('"+broken[0]+"','"+broken[1]+"','"+broken[2]+"','"+broken[3]+"','"+broken[4]+"','"+broken[5]+"','"+broken[6]+"')");
					statement.addBatch("INSERT INTO Flight VALUES('"+broken[0]+"','"+broken[1]+"','"+broken[2]+"','"+broken[3]+"','"+broken[4]+"','"+broken[5]+"','"+broken[6]+"')");
				}
				statement.executeBatch();
				statement.clearBatch();
				System.out.println("Schedule from "+fileName+" successfully added to database");
			}
			catch(IOException IOEx){
				System.out.println("There's an issue with reading the file");
				System.out.println(IOEx.toString());
				IOEx.printStackTrace();
			}
			catch(SQLException SQLEx){
				System.out.println("SQL Exception - You may need to insert Plane data first!");
				System.out.println(SQLEx.toString());
				SQLEx.printStackTrace();			
			}
		}
		
		private void loadPrice(){
			String fileName;
			System.out.println("What is the name of the file containing the price data?");
			fileName = in.next();
			try{
				String fline;
				File fp = new File(fileName);
				Scanner fn = new Scanner(fp);
				statement = connection.createStatement();
				while(fn.hasNextLine()){
					fline = fn.nextLine();
					if(fline.length() == 0) break;
					String[] broken = fline.split(" ");
					//System.out.println(Arrays.toString(broken));
					System.out.println("INSERT INTO Price VALUES('"+broken[0]+"','"+broken[1]+"','"+broken[2]+"','"+broken[3]+"')");
					statement.addBatch("INSERT INTO Price VALUES('"+broken[0]+"','"+broken[1]+"','"+broken[2]+"','"+broken[3]+"')");
				}
				statement.executeBatch();
				statement.clearBatch();
				System.out.println("Prices from "+fileName+" successfully added to database");
			}
			catch(IOException IOEx){
				System.out.println("There's an issue with reading the file");
				System.out.println(IOEx.toString());
				IOEx.printStackTrace();
			}
			catch(SQLException SQLEx){
				System.out.println("SQL Exception - You may need to insert Schedule data first!");
				System.out.println(SQLEx.toString());
				SQLEx.printStackTrace();			
			}
		}
		
		private void loadPlane(){
			String fileName;
			System.out.println("What is the name of the file containing the plane data?");
			fileName = in.next();
			try{
				String fline;
				File fp = new File(fileName);
				Scanner fn = new Scanner(fp);
				statement = connection.createStatement();
				//java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/DD/YYYY");
				while(fn.hasNextLine()){
					fline = fn.nextLine();
					if(fline.length() == 0) break;
					String[] broken = fline.split(" ");
					//System.out.println(Arrays.toString(broken));
					System.out.println("INSERT INTO Plane VALUES('"+broken[0]+"','"+broken[1]+"','"+broken[2]+"', to_date('"+broken[3]+"','MM/DD/YYYY'),'"+broken[4]+"')");
					statement.addBatch("INSERT INTO Plane VALUES('"+broken[0]+"','"+broken[1]+"','"+broken[2]+"', to_date('"+broken[3]+"','MM/DD/YYYY'),'"+broken[4]+"')");
				}
				statement.executeBatch();
				statement.clearBatch();
				System.out.println("Planes from "+fileName+" successfully added to database");
			}
			catch(IOException IOEx){
				System.out.println("There's an issue with reading the file");
				System.out.println(IOEx.toString());
				IOEx.printStackTrace();
			}
			catch(SQLException SQLEx){
				System.out.println("SQL Exception");
				System.out.println(SQLEx.toString());
				SQLEx.printStackTrace();			
			}
		}
		
		private void generateManifest(){
			int flightNum;
			String flightDate;
			int counter = 1;
			System.out.print("To generate a manifest we need a flight number: ");
			flightNum = in.nextInt();
			System.out.print("And a date: ");
			flightDate = in.next();
			try{
				statement = connection.createStatement();
			
				query = "SELECT salutation, first_name, last_name FROM (Reservation R NATURAL JOIN Reservation_detail D) NATURAL JOIN Customer C WHERE D.flight_number = "+flightNum+" AND D.flight_date = to_date('"+flightDate+"','MM/DD/YYYY') AND R.ticketed = '1'";
				resultSet = statement.executeQuery(query);
				while(resultSet.next()){
					System.out.println("Record ["+counter+"]: "+resultSet.getString(1)+" "+resultSet.getString(2)+" "+resultSet.getString(3));
					counter++;
				}
			}
			catch(SQLException SQLEx){
				System.out.println("SQL Exception");
				System.out.println(SQLEx.toString());
				SQLEx.printStackTrace();			
			}
		}
		
}