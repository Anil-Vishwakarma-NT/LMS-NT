package com.nt.course_service_lms.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.h2.tools.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Profile("test")
@Configuration  // Changed from @TestConfiguration
public class H2ServerTestConfig {  // Renamed for clarity

    private Server h2Server;

    @PostConstruct  // Changed from @EventListener
    public void startH2Server() {
        try {
            // Check if server is already running
            if (!isH2ServerRunning()) {
                h2Server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
                System.out.println("H2 Server started on port 9092");
            } else {
                System.out.println("H2 Server already running on port 9092");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 server", e);
        }
    }

    @PreDestroy
    public void stopH2Server() {
        if (h2Server != null && h2Server.isRunning(false)) {
            h2Server.stop();
            System.out.println("H2 Server stopped");
        }
    }

    private boolean isH2ServerRunning() {
        try {
            // Try to connect to see if server is already running
            Connection conn = DriverManager.getConnection(
                    "jdbc:h2:tcp://localhost:9092/mem:shareddb", "sa", "");
            conn.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
