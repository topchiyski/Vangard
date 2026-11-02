# vanguard-reporting

Runs **last** in every build. It:
1. collects every module's Allure results into `target/allure-results/` (at the repository root);
2. writes the Allure *Environment* panel (Java, OS, env, modes);
3. merges every `cucumber.json` into `target/cucumber-reports/cucumber.json`, keeping only the final attempt of retried scenarios;
4. uploads to Jira Xray when `-Dxray.upload.enabled=true` (one Test Execution per run);
5. prints the results table and **fails the build if any scenario failed**.

That's why module commands always include it: `mvn verify -pl <module>,vanguard-reporting -am`.

In CI it runs on downloaded artifacts:
`mvn exec:java -pl vanguard-reporting -Dexec.mainClass=com.vanguard.reporting.ReportAggregator "-Dexec.args=--root . --input downloaded-results"`
