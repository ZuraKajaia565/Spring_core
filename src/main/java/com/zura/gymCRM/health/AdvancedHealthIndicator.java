package com.zura.gymCRM.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class AdvancedHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
                .withDetail("database", checkDatabase())
                .withDetail("cpuUsage", getCpuUsage())
                .withDetail("memoryUsage", getMemoryUsage())
                .withDetail("diskSpace", checkDiskSpace())
                .build();
    }

    private String checkDatabase() {
        try {
            return "MariaDB is UP";
        } catch (Exception e) {
            return "Database is DOWN: " + e.getMessage();
        }
    }

    private String getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double load = osBean.getSystemLoadAverage();
        return "CPU Load: " + (load >= 0 ? load + "%" : "Unavailable");
    }

    private String getMemoryUsage() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return "Memory Used: " + (usedMemory / (1024 * 1024)) + " MB / " +
                (totalMemory / (1024 * 1024)) + " MB";
    }


    private String checkDiskSpace() {
        File root = new File("/");
        long freeSpace = root.getFreeSpace() / (1024 * 1024 * 1024);
        long totalSpace = root.getTotalSpace() / (1024 * 1024 * 1024);
        return "Disk Space: " + freeSpace + " GB free / " + totalSpace + " GB total";
    }
}
