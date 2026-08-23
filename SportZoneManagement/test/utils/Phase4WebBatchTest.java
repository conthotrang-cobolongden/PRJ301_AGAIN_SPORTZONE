package utils;

import controller.Phase4ServletTest;

/** Runs the complete Phase 4 web test batch through one main class. */
public final class Phase4WebBatchTest {

    private Phase4WebBatchTest() {
    }

    public static void main(String[] args) throws Exception {
        int failures = 0;
        failures += run("Phase4WebContractTest",
                () -> Phase4WebContractTest.main(new String[0]));
        failures += run("Phase4JspContractTest",
                () -> Phase4JspContractTest.main(new String[0]));
        failures += run("Phase4ServletTest",
                () -> Phase4ServletTest.main(new String[0]));

        if (failures > 0) {
            throw new AssertionError(
                    "Phase 4 web batch failed: " + failures + " test group(s)");
        }
        System.out.println("Phase4WebBatchTest PASSED");
    }

    private static int run(String name, CheckedTest test) {
        try {
            test.run();
            return 0;
        } catch (Throwable failure) {
            System.out.println(name + " FAILED: " + failure);
            return 1;
        }
    }

    private interface CheckedTest {

        void run() throws Exception;
    }
}
