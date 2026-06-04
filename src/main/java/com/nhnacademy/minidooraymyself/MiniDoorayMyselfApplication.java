package com.nhnacademy.minidooraymyself;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class MiniDoorayMyselfApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniDoorayMyselfApplication.class, args);
    }

}

@Component
@ConditionalOnProperty(name = "mini-dooray.launcher.enabled", havingValue = "true")
class MiniDoorayServiceLauncher implements ApplicationRunner {

    private static final List<ServiceCommand> SERVICES = List.of(
            new ServiceCommand("eureka-server", "eureka-server", null, 8761, 5000),
            new ServiceCommand("account-api", "account-api", "local", 8081, 5000),
            new ServiceCommand("task-api", "task-api", "local", 8082, 5000),
            new ServiceCommand("api-gateway", "api-gateway", null, 8000, 5000),
            new ServiceCommand("front-gateway", "front-gateway", null, 8080, 0)
    );

    private final List<Process> processes = new ArrayList<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File root = new File(System.getProperty("user.dir"));

        Runtime.getRuntime().addShutdownHook(new Thread(this::stopAll));

        for (ServiceCommand service : SERVICES) {
            startService(root, service);
            waitForPort(service.name(), service.port(), Duration.ofSeconds(90));
            if (service.delayAfterStartMillis() > 0) {
                Thread.sleep(service.delayAfterStartMillis());
            }
        }

        System.out.println("""

                Mini Dooray services started.
                - front-gateway: http://localhost:8080
                - api-gateway:   http://localhost:8000
                - eureka-server: http://localhost:8761

                Press Ctrl+C to stop all services.
                """);

        Thread.currentThread().join();
    }

    private void startService(File root, ServiceCommand service) throws IOException {
        File moduleDir = new File(root, service.moduleDir());
        StringBuilder launchScript = new StringBuilder("trap '' INT; exec ./mvnw spring-boot:run");
        if (service.profile() != null) {
            launchScript.append(" -Dspring-boot.run.profiles=").append(service.profile());
        }

        System.out.printf("Starting %s...%n", service.name());
        Process process = new ProcessBuilder("bash", "-lc", launchScript.toString())
                .directory(moduleDir)
                .inheritIO()
                .start();
        processes.add(process);
    }

    private void waitForPort(String serviceName, int port, Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", port), 1000);
                System.out.printf("%s is ready on port %d.%n", serviceName, port);
                return;
            } catch (IOException ignored) {
                Thread.sleep(1000);
            }
        }

        throw new IllegalStateException(serviceName + " did not open port " + port + " within " + timeout.toSeconds() + " seconds.");
    }

    private void stopAll() {
        for (Process process : processes.reversed()) {
            if (process.isAlive()) {
                process.destroy();
                try {
                    if (!process.waitFor(10, TimeUnit.SECONDS)) {
                        process.destroyForcibly();
                        process.waitFor(5, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    process.destroyForcibly();
                }
            }
        }
    }

    private record ServiceCommand(String name, String moduleDir, String profile, int port, long delayAfterStartMillis) {
    }
}
