import java.sql.*;
import java.util.*;
import java.io.*;

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
				System.out.printf("\nAdmin Interface\n1: Erase the database\n2: Load schedule information\n3: Load pricing information\n4: Load plane information\n5: Generate passenger manifest for specific flight on given day\n0: Exit\n");
				do{
					selection = in.nextInt();	
				}while(selection != 1 && selection != 2 && selection != 3 && selection != 4 && selection != 5 && selection != 0 && selection != 6 && selection != 7 && selection != 8);
			
				switch(selection){
					case 1: addCust();
						break;
					case 2: //showCustInfo();
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
		
	}
}