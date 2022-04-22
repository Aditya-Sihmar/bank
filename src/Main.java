import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Console; 
import java.sql.SQLException;
import java.util.Scanner;

interface User{
	public void balance(Connection con);
	public void transfer(Connection con);
}
class Login{
	String uid, pwd;
	Scanner sc = new Scanner(System.in);
	java.io.Console console = System.console();
	public Object register(Connection con) {
//		java.io.Console console = System.console();
		System.out.println("Enter First Name");
		String fname = sc.nextLine();
		System.out.println("Enter Last Name");
		String lname = sc.nextLine();
		System.out.println("Enter User Name");
		uid = sc.nextLine();
		System.out.println("Enter Password");
        pwd = sc.nextLine();
        System.out.println("Enter Account Number");
        String acc = sc.nextLine();
        System.out.println("Enter Amount");
        String amount = sc.nextLine();
        try {
        PreparedStatement stmt = con.prepareStatement("insert into bank values(?,?,?,?,?,?)");
		stmt.setString(1, uid);
		stmt.setString(2, fname);
		stmt.setString(3, lname);
		stmt.setString(4, pwd);
		stmt.setString(5, acc);
		stmt.setString(6, amount);
		stmt.execute();
		Bank usr = new Bank();
		System.out.println("Registered Successfully");
		return usr;
        }
        catch(Exception e) {
        	System.out.println(e);
        	System.out.println("Please try again");
        	register(con);
        }
        return null;
	}
	public Object login(Connection con){
		System.out.println("Enter User Name");
		uid = sc.nextLine();
		System.out.println("Enter Password");
        pwd = sc.nextLine();
        try {
	        PreparedStatement pt = con.prepareStatement("select uid,pwd from bank where uid=?");
	
	        // process query results
	        pt.setString(1, uid);
	        ResultSet rs = pt.executeQuery();
	        String orgUname = "", orPass = "";
	        while (rs.next()) {
	        	
	            orgUname = rs.getString("uid");
	            orPass = rs.getString("pwd");
	        } //end while
	        if (orPass.equals(pwd)) {
	        	Bank usr = new Bank();
	        	System.out.println("Login Successfully");
	    		return usr;
	        } else {
	        	System.out.println("---------------------------------------");
	        	System.out.println("No record found with this credentials");
	        	System.out.println("1. Try again");
	        	System.out.println("2. Register");
	        	Scanner sc = new Scanner(System.in);
	        	int choice= sc.nextInt();
	        	switch(choice) {
	        	case 1:
	        		login(con);
	        	case 2:
	        		register(con);
	        	}
	        }
	        rs.close();
        }
        catch(Exception e) {
        	
        	System.out.println(e);
        }
        return null;
	}
	private class Bank implements User{
		public void transfer(Connection con) {
			try {
				System.out.println("--------------------------");
				System.out.println("Enter the amount");
				String amount = sc.nextLine();
				PreparedStatement prestm = con.prepareStatement("update bank set amount = amount-(?) where uid =?");
				prestm.setString(1, amount);
				prestm.setString(2, uid);
				prestm.executeUpdate();
				PreparedStatement prestm1 = con.prepareStatement("update bank set amount = amount+(?) where accno =?");
				prestm1.setString(1, amount);
				System.out.println("Enter the receiver's Account Number: ");
				String raccno = sc.nextLine();
				prestm1.setString(2, raccno);
				
				prestm1.executeUpdate();
				System.out.println("Transfer Successful");
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
		public void balance(Connection con) {
			try {
				PreparedStatement prestm = con.prepareStatement("select amount from bank where uid= (?)");
				prestm.setString(1, uid);
				//java.sql.Statement st =  con.createStatement();
				ResultSet rs = prestm.executeQuery();
				while(rs.next())
				{
					System.out.println("Balance : "+rs.getString(1));
				}
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
	}
}

public class Main {
	public static void main(String args[]) throws SQLException {
		System.out.println("1. Login");
		System.out.println("2. Register");
		Scanner sc = new Scanner(System.in);
		int choice = sc.nextInt();
		Login usr = new Login();
		User u;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","Pass@123");
			switch(choice){
			case 1:
				u = (User) usr.login(con);
				break;
			case 2:
				u = (User) usr.register(con);
				break;
			default:
				u = (User) usr.register(con);
				break;
			}
			System.out.println("1. Transfer");
			System.out.println("2. Balance check");
			choice = sc.nextInt();
			switch(choice) {
			case 1:
				u.transfer(con);
				break;
			case 2:
				u.balance(con);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
