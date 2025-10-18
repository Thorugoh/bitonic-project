package com;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.random.RandomGenerator;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class BitonicSimulation extends Simulation {

    {
        String scenarioName = "BitonicSimulation";
        String endpoint = "http://localhost:3000";
        
        HttpProtocolBuilder protocol = http
                .baseUrl(endpoint)
                .acceptHeader("application/json")
                .shareConnections()
                .maxConnectionsPerHost(1000);

        RandomGenerator random = RandomGenerator.getDefault();
        int n = random.nextInt(1, 11);
        int l = random.nextInt(1, 11);
        int r = random.nextInt(1, 11);

        String path = String.format("/bitonic?n=%d&l=%d&r=%d", n, l, r);
        System.out.println(path);
        ScenarioBuilder scenario = scenario(scenarioName)
                .exec(
                        http("Bitonic Request").get(path)
                );

        setUp(scenario.
                injectOpen(
                        atOnceUsers(10000)
                )
        ).protocols(protocol);
    }
}