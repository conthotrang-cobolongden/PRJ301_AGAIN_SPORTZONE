package utils;

/** Kiem tra DB, schema va hash seed cua endpoint health. */
public final class HealthCheckTest {
    private HealthCheckTest() { }

    public static void main(String[] args) throws Exception {
        HealthCheck.Result result = HealthCheck.run();
        require(result.isDatabaseOk(), "Database must be reachable");
        require(result.getTableCount() == 9, "SportZoneDB must contain 9 tables");
        require(result.isPasswordHashOk(), "Seed password hash must match");
        System.out.println("HealthCheckTest PASSED");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
