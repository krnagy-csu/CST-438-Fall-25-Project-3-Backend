package com.example.CST438_P3;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class TestH2Controller {
    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "OK", "message", "Application is running");
    }
    
}
