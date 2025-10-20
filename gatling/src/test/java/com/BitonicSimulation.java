package com;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;
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
                .maxConnectionsPerHost(5000);

        int n = 5;
        int l = 1;
        int r = 4;

        String path = String.format("/bitonic?n=%d&l=%d&r=%d", n, l, r);
        System.out.println(path);
        ScenarioBuilder scenario = scenario(scenarioName)
                .exec(
                        http("Bitonic Request").get(path)
                );

        setUp(scenario.
                injectOpen(
                        rampUsers(10000).during(Duration.ofSeconds(30))
                )
        ).protocols(protocol);
    }
}