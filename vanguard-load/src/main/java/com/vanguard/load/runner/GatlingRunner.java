package com.vanguard.load.runner;

import com.vanguard.core.config.Endpoints;
import com.vanguard.load.profile.LoadProfile;
import com.vanguard.load.simulation.ProfiledSimulation;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Runs a Gatling simulation in a SEPARATE JVM (so load generation never competes with the test runner)
 * and turns Gatling's report into a {@link LoadResult}.
 *
 * <p>Verified against Gatling 3.16: entry point {@code io.gatling.app.Gatling} with
 * {@code --simulation <class> --results-folder <dir>}; numbers come from the console "Global Information"
 * summary (see {@link LoadResult#fromReport}); the HTML report is in {@code <results-folder>/<simulation>-<time>/}.
 */
@Slf4j
public class GatlingRunner {

    private static final String SIMULATION_PACKAGE = "com.vanguard.load.simulation.";
    private static final long EXTRA_TIMEOUT_SECONDS = 120;

    /**
     * @param simulationName simple class name, e.g. {@code ApiBrowseSimulation}, or a fully qualified one
     * @param profile        the load to generate
     */
    public LoadResult run(String simulationName, LoadProfile profile) {
        String simulationClass = simulationName.contains(".") ? simulationName : SIMULATION_PACKAGE + simulationName;
        verifySimulationExists(simulationClass);
        Path resultsRoot = Path.of("target", "gatling", profile.name() + "-" + System.currentTimeMillis()).toAbsolutePath();
        Path consoleLog = resultsRoot.resolve("gatling-console.log");
        try {
            Files.createDirectories(resultsRoot);
            Path profileFile = Files.writeString(resultsRoot.resolve("profile.json"), profile.toJson());
            List<String> command = new ArrayList<>(List.of(
                    javaExecutable(),
                    // WHY: Gatling 3.16 needs this (the official gatling-maven-plugin adds it too).
                    "--add-opens=java.base/java.lang=ALL-UNNAMED", "-Xmx1g",
                    "-cp", System.getProperty("java.class.path"),
                    "-D" + LoadProfile.SYSTEM_PROPERTY + "=" + profileFile,
                    "-D" + ProfiledSimulation.BASE_URL_PROPERTY + "=" + Endpoints.loadBaseUrl(),
                    "io.gatling.app.Gatling",
                    "--simulation", simulationClass,
                    "--results-folder", resultsRoot.toString(),
                    "--launcher", "vanguard"));
            log.info("Starting load test '{}' ({}): {} users, ramp {}s, hold {}s", profile.name(), simulationClass,
                    profile.users(), profile.rampUpSeconds(), profile.durationSeconds());
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .redirectOutput(consoleLog.toFile())
                    .start();
            long timeout = profile.rampUpSeconds() + profile.durationSeconds() + EXTRA_TIMEOUT_SECONDS;
            if (!process.waitFor(timeout, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IllegalStateException("Load test did not finish within " + timeout + "s. See " + consoleLog);
            }
            Path reportDir = newestReportDir(resultsRoot);
            if (process.exitValue() != 0 && reportDir == null) {
                throw new IllegalStateException("Gatling failed (exit " + process.exitValue() + "). Last output:\n"
                        + tail(consoleLog));
            }
            return LoadResult.fromReport(reportDir, consoleLog);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot run Gatling", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for Gatling", e);
        }
    }

    private static void verifySimulationExists(String simulationClass) {
        try {
            Class.forName(simulationClass, false, GatlingRunner.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("No simulation class " + simulationClass
                    + ". Simulations live in vanguard-load/src/main/java/com/vanguard/load/simulation.", e);
        }
    }

    private static Path newestReportDir(Path resultsRoot) throws IOException {
        try (Stream<Path> dirs = Files.list(resultsRoot)) {
            return dirs.filter(Files::isDirectory)
                    .max(Comparator.comparingLong(p -> p.toFile().lastModified()))
                    .orElse(null);
        }
    }

    private static String javaExecutable() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }

    private static String tail(Path file) throws IOException {
        if (!Files.exists(file)) {
            return "(no output)";
        }
        List<String> lines = Files.readAllLines(file);
        return String.join(File.separator.equals("\\") ? "\r\n" : "\n",
                lines.subList(Math.max(0, lines.size() - 30), lines.size()));
    }
}
