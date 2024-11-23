package com.kodnest.petclinic.PetClinic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class IdBasedPet {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		try {
			// load the driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver Loaded");
			// Establish Connection
		    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pet","root","Nivas@987");
		    System.out.println("Connection Established");
		    // Create Statement Medium
		    String sql = "Select * from petclinic where id=?";
		    PreparedStatement ps = con.prepareStatement(sql);
		    System.out.println("Enter the ID");
		    int ID = scan.nextInt();
		    ps.setInt(1, ID);
		    System.out.println("Prepared Statement Medium Created");
		    //Display the Result
		    ResultSet rs = ps.executeQuery();
		    while(rs.next() == true) {
		    	System.out.println(rs.getInt(1)+ " | " + rs.getString(2)+ " | " +rs.getString(3)+ " | " +rs.getString(4)+ " | " +rs.getInt(5)+ " | " +rs.getString(6)+ " | " +rs.getInt(7)+ " | " +rs.getString(8)+ " | " +rs.getString(9));
		    }
		    // close all resources
		    con.close();
		    ps.close();
		    rs.close();
		    
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
