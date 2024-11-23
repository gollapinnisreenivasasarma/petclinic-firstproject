package com.kodnest.petclinic.PetClinic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class IdBasedPetTest {
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private Scanner mockScanner;

    @BeforeEach
    void setUp() throws Exception {
        // Create mocks
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockScanner = mock(Scanner.class);

        // Set up mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testSuccessfulPetSearch() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {

            // Mock the driver loading
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenReturn(null);

            // Mock the database connection
            mockDriverManager.when(() -> DriverManager.getConnection(
                eq("jdbc:mysql://localhost:3306/pet"),
                eq("root"),
                eq("Nivas@987")
            )).thenReturn(mockConnection);

            // Mock user input (ID = 1)
            when(mockScanner.nextInt()).thenReturn(1);

            // Mock ResultSet data
            when(mockResultSet.next())
                .thenReturn(true)  // First call returns true
                .thenReturn(false); // Second call returns false to end loop

            // Set up mock result data
            when(mockResultSet.getInt(1)).thenReturn(1);
            when(mockResultSet.getString(2)).thenReturn("Dog");
            when(mockResultSet.getString(3)).thenReturn("Max");
            when(mockResultSet.getString(4)).thenReturn("Brown");
            when(mockResultSet.getInt(5)).thenReturn(3);
            when(mockResultSet.getString(6)).thenReturn("John");
            when(mockResultSet.getInt(7)).thenReturn(1234567890);
            when(mockResultSet.getString(8)).thenReturn("2024-03-23");
            when(mockResultSet.getString(9)).thenReturn("Vaccination");

            // Mock System.out to avoid null pointer exceptions
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                IdBasedPet.main(new String[]{});
            }

            // Verify interactions
            verify(mockPreparedStatement).setInt(1, 1);
            verify(mockPreparedStatement).executeQuery();
            verify(mockResultSet, times(2)).next(); // Called twice - once returns true, once false
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();
        }
    }

    @Test
    void testPetNotFound() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {

            // Mock the driver and connection setup
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenReturn(null);
            mockDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                           .thenReturn(mockConnection);

            // Mock user input (ID = 999 - non-existent)
            when(mockScanner.nextInt()).thenReturn(999);

            // Mock empty ResultSet
            when(mockResultSet.next()).thenReturn(false);

            // Mock System.out to avoid null pointer exceptions
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                IdBasedPet.main(new String[]{});
            }

            // Verify interactions
            verify(mockPreparedStatement).setInt(1, 999);
            verify(mockResultSet, times(1)).next(); // Called once, returns false
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();
        }
    }

    @Test
    void testDatabaseConnectionFailure() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {

            // Mock the driver loading to throw an exception
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver"))
                    .thenThrow(new ClassNotFoundException("Driver not found"));

            // Mock System.out to avoid null pointer exceptions
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                IdBasedPet.main(new String[]{});
            }

            // Verify no connection was attempted
            mockDriverManager.verify(() -> 
                DriverManager.getConnection(anyString(), anyString(), anyString()),
                never()
            );
        }
    }

    @Test
    void testInvalidUserInput() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {

            // Mock the driver and connection setup
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenReturn(null);
            mockDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                           .thenReturn(mockConnection);

            // Mock Scanner to throw exception for invalid input
            when(mockScanner.nextInt()).thenThrow(new java.util.InputMismatchException());

            // Mock System.out to avoid null pointer exceptions
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                IdBasedPet.main(new String[]{});
            }

            // Verify resources are properly closed even after input failure
            verify(mockConnection).close();
        }
    }
}
