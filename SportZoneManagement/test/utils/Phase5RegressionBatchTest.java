package utils;

import controller.CourtDetailServletTest;
import dao.Phase5BookingPersistenceTest;
import model.ModelContractTest;
import service.BookingServiceTest;
import service.MembershipServiceTest;

/**
 * Mot lenh chay regression Phase 3, Phase 4 va Phase 5.
 */
public final class Phase5RegressionBatchTest {

    private Phase5RegressionBatchTest() {
    }

    public static void main(String[] args)
            throws Exception {
        int failures = 0;

        failures += run(
                "ModelContractTest",
                () -> ModelContractTest.main(
                        new String[0]
                )
        );

        failures += run(
                "CourtDetailServletTest",
                () -> CourtDetailServletTest.main(
                        new String[0]
                )
        );

        failures += run(
                "Phase3WebContractTest",
                () -> Phase3WebContractTest.main(
                        new String[0]
                )
        );

        failures += run(
                "Phase3JspContractTest",
                () -> Phase3JspContractTest.main(
                        new String[0]
                )
        );

        failures += run(
                "Phase4WebBatchTest",
                () -> Phase4WebBatchTest.main(
                        new String[0]
                )
        );

        failures += run(
                "MembershipServiceTest",
                () -> MembershipServiceTest.main(
                        new String[0]
                )
        );

        failures += run(
                "Phase5MembershipContractTest",
                () -> Phase5MembershipContractTest.main(
                        new String[0]
                )
        );

        failures += run(
                "Phase5BookingPersistenceTest",
                () -> Phase5BookingPersistenceTest.main(
                        new String[0]
                )
        );

        failures += run(
                "BookingServiceTest",
                () -> BookingServiceTest.main(
                        new String[0]
                )
        );

        failures += run(
                "Phase5BookingWebContractTest",
                () -> Phase5BookingWebContractTest.main(
                        new String[0]
                )
        );

        if (failures > 0) {
            throw new AssertionError(
                    "Phase 5 regression failed: "
                    + failures
                    + " test group(s)"
            );
        }

        System.out.println(
                "Phase5RegressionBatchTest PASSED"
        );
    }

    private static int run(
            String name,
            CheckedTest test
    ) {
        System.out.println();
        System.out.println("=== " + name + " ===");

        try {
            test.run();
            return 0;
        } catch (Throwable failure) {
            System.out.println(name + " FAILED");
            failure.printStackTrace(System.out);
            return 1;
        }
    }

    private interface CheckedTest {

        void run() throws Exception;
    }
}