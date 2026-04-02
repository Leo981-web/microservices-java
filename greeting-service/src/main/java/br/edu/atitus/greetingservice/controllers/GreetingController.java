package br.edu.atitus.greetingservice.controllers;

import br.edu.atitus.greetingservice.configs.GreetingConfig;
import br.edu.atitus.greetingservice.dto.GreetingRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/greeting")
public class GreetingController {

    //  Variáveis de 'application.properties':
//    @Value("${greeting-service.greeting}")
//    private String greeting;
//
//    @Value("${greeting-service.default-name}")
//    private String defaultName;

    private final GreetingConfig config;

    // Injeção de dependência
    public GreetingController(GreetingConfig config) {
        this.config = config;
    }

    @GetMapping({"", "/"})
    public String getGreeting(
            @RequestParam(required = false) String name) {

        if (name == null || name.isEmpty()) {
            name = config.getDefaultName();
        }

        String greetingReturn = String.format("%s %s!!!", config.getGreeting(), name);

        return greetingReturn;
    }

    @GetMapping("/{name}")
    public String getGreetingWithPath(@PathVariable String name) {

        String greetingReturn = String.format("%s %s!!!", config.getGreeting(), name);

        return greetingReturn;
    }

    @PostMapping
    public String postGreeting(@RequestBody GreetingRequest requestBody ) {
        // Extração do objeto que foi criado a partir do JSON
        String name = requestBody.getName();

        //Monta a mensagem
        String greetingReturn = String.format("%s %s!!!", config.getGreeting(), name);

        return greetingReturn;
    }
}



// Metodos

//Get, Post, Delete, Put, Post