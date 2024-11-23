package com.kodnest.petclinic.PetClinic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UpdatePetTest {
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private Scanner mockScanner;

    @BeforeEach
    void setUp() throws Exception {
        // Create mocks
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockScanner = mock(Scanner.class);

        // Set up mock behavior for PreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate one row affected
    }

    @Test
    void testSuccessfulPetInsertion() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {

            // Mock the driver loading
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenReturn(null);

            // Mock the database connection
            mockDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            // Mock Scanner inputs
            when(mockScanner.nextInt())
                .thenReturn(1)    // id
                .thenReturn(3);   // age
            when(mockScanner.next())
                .thenReturn("Dog")           // petType
                .thenReturn("Max")           // petName
                .thenReturn("Brown")         // colour
                .thenReturn("John")          // guardianName
                .thenReturn("1234567890")    // phone (will be parsed as int)
                .thenReturn("2024-03-23")    // AppointmentDate
                .thenReturn("Vaccination");   // TreatmentFor

            // Execute the main method with our mocked Scanner
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.in).thenReturn(null); // Mock System.in
                // Note: System.out.println calls are mocked to avoid null pointer exceptions
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                UpdatePet.main(new String[]{});
            }

            // Verify PreparedStatement interactions
            verify(mockPreparedStatement).setInt(1, 1);
            verify(mockPreparedStatement).setString(2, "Dog");
            verify(mockPreparedStatement).setString(3, "Max");
            verify(mockPreparedStatement).setString(4, "Brown");
            verify(mockPreparedStatement).setInt(5, 3);
            verify(mockPreparedStatement).setString(6, "John");
            verify(mockPreparedStatement).setInt(7, 1234567890);
            verify(mockPreparedStatement).setString(8, "2024-03-23");
            verify(mockPreparedStatement).setString(9, "Vaccination");
            verify(mockPreparedStatement).executeUpdate();

            // Verify resource closing
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
                
                UpdatePet.main(new String[]{});
            }

            // Verify that no connection was established
            mockDriverManager.verify(() -> 
                DriverManager.getConnection(anyString()),
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
            mockDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            // Mock Scanner to throw exception
            when(mockScanner.nextInt()).thenThrow(new java.util.InputMismatchException());

            // Mock System.out to avoid null pointer exceptions
            try (MockedStatic<System> mockSystem = Mockito.mockStatic(System.class)) {
                mockSystem.when(() -> System.out.println(anyString())).then(invocation -> null);
                
                UpdatePet.main(new String[]{});
            }

            // Verify that resources are still closed properly
            verify(mockConnection, times(1)).close();
        }
    }
}
