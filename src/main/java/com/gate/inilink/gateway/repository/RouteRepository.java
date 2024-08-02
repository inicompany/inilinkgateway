package com.gate.inilink.gateway.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.gate.inilink.gateway.model.Routes;

@Repository
public interface RouteRepository extends ReactiveMongoRepository<Routes, String> {
}
