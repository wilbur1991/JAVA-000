package io.github.kimmking.gateway.outbound.okhttp;

import io.github.kimmking.gateway.router.HttpEndpointRouter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomRouter implements HttpEndpointRouter {


    @Override
    public String route(List<String> endpoints) {
        int index = ThreadLocalRandom.current().nextInt(endpoints.size());
        return endpoints.get(index);
    }
}
