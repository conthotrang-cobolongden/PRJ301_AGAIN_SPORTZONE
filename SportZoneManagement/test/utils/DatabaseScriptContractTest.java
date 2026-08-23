package utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DatabaseScriptContractTest {
    private DatabaseScriptContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Path sqlDirectory = Paths.get("..", "sql");
        String schema = new String(Files.readAllBytes(
                sqlDirectory.resolve("01_SportZoneDB_Schema.sql")),
                StandardCharsets.UTF_8);
        String seed = new String(Files.readAllBytes(
                sqlDirectory.resolve("02_SportZoneDB_Seed.sql")),
                StandardCharsets.UTF_8);

        int bookingStart = schema.indexOf("CREATE TABLE Bookings (");
        int bookingEnd = schema.indexOf("CREATE TABLE BookingServices (");
        require(bookingStart >= 0 && bookingEnd > bookingStart,
                "Schema must define Bookings before BookingServices");
        String bookingTable = schema.substring(bookingStart, bookingEnd);

        require(bookingTable.matches("(?s).*\\n\\s*TimeSlotID\\s+INT.*")
                && !bookingTable.matches("(?s).*\\n\\s*CourtID\\s+INT.*"),
                "Bookings must derive court through TimeSlotID only");
        require(schema.contains("SCHEMA_CONTRACT_BOOKINGS_V3"),
                "Schema must assert the canonical Bookings contract");
        require(seed.contains("SCHEMA_CONTRACT_BOOKINGS_V3"),
                "Seed must reject a stale Bookings schema before inserting data");

        System.out.println("DatabaseScriptContractTest PASSED");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
