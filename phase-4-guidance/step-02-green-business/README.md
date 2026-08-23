# Phase 4 — Step 02 GREEN business

Copy the complete files to these project paths:

1. `src/java/dao/AccountDAO.java`
   - Replace the current file completely with `src/java/dao/AccountDAO.java` from this folder.
2. `src/java/service/ProfileService.java`
   - Create the new file using `src/java/service/ProfileService.java` from this folder.

Do not copy this guidance folder into `src/java`.

After copying, run both main-method tests from the `SportZoneManagement` directory:

```powershell
& "C:\Program Files\Apache NetBeans\extide\ant\bin\ant.bat" "-Djavac.includes=service/ProfileServiceTest.java" "-Drun.class=service.ProfileServiceTest" run-test-with-main

& "C:\Program Files\Apache NetBeans\extide\ant\bin\ant.bat" "-Djavac.includes=dao/ProfileAccountDAOContractTest.java" "-Drun.class=dao.ProfileAccountDAOContractTest" run-test-with-main
```

Expected runtime output:

```text
ProfileServiceTest PASSED
ProfileAccountDAOContractTest PASSED
```

NetBeans Ant may still print `BUILD SUCCESSFUL` when a main class exits with `Java Result: 1`.
Treat a test as passed only when its own `... PASSED` line appears and there is no exception.
