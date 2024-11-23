import static org.mockito.Mockito.*;

class PetTableTest {
    private Connection mockConnection;
    private Statement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        // Create mocks
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockResultSet = mock(ResultSet.class);

        // Set up mock behavior
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
    }

    @Test
    void testDatabaseConnection() throws Exception {
        try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class);
             MockedStatic<Class> mockClass = Mockito.mockStatic(Class.class)) {
            
            // Mock the driver loading
            mockClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenReturn(null);
            
            // Mock the connection
            mockDriverManager.when(() -> DriverManager.getConnection(
                eq("jdbc:mysql://localhost:3306/pet"),
                eq("root"),
                eq("Nivas@987")
            )).thenReturn(mockConnection);

            // Mock ResultSet data
            when(mockResultSet.next())
                .thenReturn(true)  // First call returns true
                .thenReturn(false); // Second call returns false to end the loop
            
            when(mockResultSet.getInt(1)).thenReturn(1);
            when(mockResultSet.getString(2)).thenReturn("TestPet");
            when(mockResultSet.getString(3)).thenReturn("TestBreed");
            when(mockResultSet.getString(4)).thenReturn("TestColor");
            when(mockResultSet.getInt(5)).thenReturn(2);
            when(mockResultSet.getString(6)).thenReturn("TestOwner");
            when(mockResultSet.getInt(7)).thenReturn(1234567890);
            when(mockResultSet.getString(8)).thenReturn("TestAddress");
            when(mockResultSet.getString(9)).thenReturn("TestEmail");

            // Execute the main method
            PetTable.main(new String[]{});

            // Verify interactions
            verify(mockStatement).executeQuery("select * from petclinic");
            verify(mockResultSet, times(2)).next(); // Called twice - once returns true, once returns false
            verify(mockConnection).close();
            verify(mockStatement).close();
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

            // Execute the main method
            PetTable.main(new String[]{});

            // Verify that no connection was established
            mockDriverManager.verify(() -> 
                DriverManager.getConnection(anyString(), anyString(), anyString()),
                never()
            );
        }
    }
}
