package com.gate.inilink.gateway.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gate.inilink.gateway.model.Routes;
import com.gate.inilink.gateway.repository.RouteRepository;

import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
public class RouteConfigLoader {
	
	@Autowired
    private RouteRepository routeRepository;
  
    public Flux<Routes> loadRoutes() {
        return routeRepository.findAll();
    }

	/*
    private static final String ROUTES_FILE = "routes.json";

    public List<Map<String, Object>> loadRoutes() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ROUTES_FILE)) {
            if (inputStream == null) {
                throw new IOException("File not found: " + ROUTES_FILE);
            }
            return objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
        }
    }
    */
}
