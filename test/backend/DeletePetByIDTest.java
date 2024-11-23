package com.kodnest.petclinic.PetClinic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DeletePetByIDTest {
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private Scanner mockScanner;

    @BeforeEach
    void setUp() throws Exception {
        // Create mocks
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockScanner = mock(Scanner.class);

        // Set up mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @Test
    void testSuccessfulPetDeletion() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {

            // Mock the driver loading
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenReturn(null);

            // Mock the database connection
            mockDriverManager.when(() -> DriverManager.getConnection(
                eq("jdbc:mysql://localhost:3306/pet?user=root&password=Nivas@987")
            )).thenReturn(mockConnection);

            // Mock user input (ID = 1)
            when(mockScanner.nextInt()).thenReturn(1);

            // Mock successful deletion (1 row affected)
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Mock System.out to avoid null pointer exceptions
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                DeletePetByID.main(new String[]{});
            }

            // Verify interactions
            verify(mockPreparedStatement).setInt(1, 1);
            verify(mockPreparedStatement).executeUpdate();
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
        }
    }

    @Test
    void testPetNotFound() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {

            // Mock the driver and connection setup
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenReturn(null);
            mockDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            // Mock user input (ID = 999 - non-existent)
            when(mockScanner.nextInt()).thenReturn(999);

            // Mock no rows affected
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            // Mock System.out to avoid null pointer exceptions
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                DeletePetByID.main(new String[]{});
            }

            // Verify interactions
            verify(mockPreparedStatement).setInt(1, 999);
            verify(mockPreparedStatement).executeUpdate();
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
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
                
                DeletePetByID.main(new String[]{});
            }

            // Verify no connection was attempted
            mockDriverManager.verify(() -> 
                DriverManager.getConnection(anyString()),
                never()
            );
        }
    }

    @Test
    void testDatabaseError() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {

            // Mock the driver and connection setup
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenReturn(null);
            mockDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            // Mock user input
            when(mockScanner.nextInt()).thenReturn(1);

            // Mock database error
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

            // Mock System.out to avoid null pointer exceptions
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                DeletePetByID.main(new String[]{});
            }

            // Verify resources are properly closed even after database error
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
        }
    }

    @Test
    void testInvalidUserInput() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {

            // Mock the driver and connection setup
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenReturn(null);
            mockDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            // Mock Scanner to throw exception for invalid input
            when(mockScanner.nextInt()).thenThrow(new java.util.InputMismatchException());

            // Mock System.out to avoid null pointer exceptions
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                DeletePetByID.main(new String[]{});
            }

            // Verify resources are properly closed even after input failure
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
        }
    }
}
