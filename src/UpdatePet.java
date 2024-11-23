package com.kodnest.petclinic.PetClinic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class UpdatePet {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		Connection con = null;
		PreparedStatement ps = null;
		try {
			// Load the Driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver Loaded");
			//  Establish the Connection
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pet?user=root&password=Nivas@987");
			System.out.println("Connection Established");
			//Create Statement Medium
			String sql = "insert into petclinic values(?,?,?,?,?,?,?,?,?)";
			System.out.println("Enter id | petType | petName | Colour | Age | GuardianName | Phone | AppoinmentDate | Treatmentfor");
			int id = scan.nextInt();
			String petType = scan.next();
			String petName = scan.next();
			String colour = scan.next();
			int age = scan.nextInt();
			String guardianName = scan.next();
			int phone = scan.nextInt();
			String AppointmentDate = scan.next();
			String TreatmentFor = scan.next();
			System.out.println("===============================");
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ps.setString(2, petType);
			ps.setString(3, petName);
			ps.setString(4, colour);
			ps.setInt(5, age);
			ps.setString(6, guardianName);
			ps.setInt(7, phone);
			ps.setString(8, AppointmentDate);
			ps.setString(9, TreatmentFor);
			// Query
			int nora = ps.executeUpdate();
			System.out.println("Got Result");
            // Display Result
			System.out.println(nora + " ROWS AFFECTED");
			// close all resources
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				con.close();
				ps.close();
				scan.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
