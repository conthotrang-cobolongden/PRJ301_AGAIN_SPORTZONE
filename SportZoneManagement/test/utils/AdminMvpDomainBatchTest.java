package utils;

import model.ModelContractTest;
import service.AdminMvpDomainTest;

public final class AdminMvpDomainBatchTest {

    private AdminMvpDomainBatchTest() { }

    public static void main(String[] args) throws Exception {
        ModelContractTest.main(new String[0]);
        DatabaseScriptContractTest.main(new String[0]);
        AdminMvpDomainTest.main(new String[0]);
        System.out.println("AdminMvpDomainBatchTest PASSED");
    }
}
