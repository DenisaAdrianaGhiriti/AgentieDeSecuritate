package com.securitate.agentie.backend; // Asigura-te ca pachetul este cel corect

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Spune Spring-ului ca aceasta clasa gestioneaza cereri REST
public class TestController {

    // Definim un endpoint care raspunde la GET pe calea /api/test
    @GetMapping("/api/test")
    public String getTestMessage() {
        return "Backend-ul Spring Boot functioneaza si API-ul e gata!";
    }
}