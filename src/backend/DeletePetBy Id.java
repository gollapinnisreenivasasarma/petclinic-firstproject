package com.kodnest.petclinic.PetClinic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class DeletePetByID {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		Connection con = null;
		PreparedStatement ps = null;
		try {
			// Load the Driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver Loaded");
			// Establish the Connection
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pet?user=root&password=Nivas@987");
			System.out.println("Connection Established");
			// Create Statement Medium
			String sql = "Delete from petclinic where id=?";
			ps = con.prepareStatement(sql);
			System.out.println("Enter id");
			int id = scan.nextInt();
			ps.setInt(1, id);
			int naro = ps.executeUpdate();
			//Result display
			System.out.println(naro + " Rows Deleted");
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
