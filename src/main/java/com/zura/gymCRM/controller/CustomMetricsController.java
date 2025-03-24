package com.zura.gymCRM.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class CustomMetricsController {

    private final Counter apiCallCounter;

    public CustomMetricsController(MeterRegistry meterRegistry) {
        this.apiCallCounter = meterRegistry.counter("custom_api_calls", "endpoint", "/metrics/counter");
    }

    @GetMapping("/counter")
    public String incrementCounter() {
        apiCallCounter.increment();
        return "API Call Count Incremented! Total Calls: " + apiCallCounter.count();
    }
}
