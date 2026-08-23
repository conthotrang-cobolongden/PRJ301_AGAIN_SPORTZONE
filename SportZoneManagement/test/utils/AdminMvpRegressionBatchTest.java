package utils;

import controller.AuthServletTest;
import filter.FrozenAccountFilterTest;

public final class AdminMvpRegressionBatchTest {

    private AdminMvpRegressionBatchTest() { }

    public static void main(String[] args) throws Exception {
        Phase5RegressionBatchTest.main(new String[0]);
        AuthServletTest.main(new String[0]);
        FrozenAccountFilterTest.main(new String[0]);
        AdminMvpDomainBatchTest.main(new String[0]);
        AdminMvpWebContractTest.main(new String[0]);
        UiRedesignContractTest.main(new String[0]);
        System.out.println("AdminMvpRegressionBatchTest PASSED");
    }
}
