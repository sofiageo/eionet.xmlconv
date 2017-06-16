package eionet.xmlconv.qa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 *
 */
@RestController
public class Example {

    @GetMapping("/test")
    Flux<Persona> list(ServerRequest request, ServerResponse response) {
        Persona test = new Persona();
        test.setId("ok");
        /*Flux<Persona> result = request.bodyToFlux(Persona.class);*/
        /*response.
        result.
        return result;*/
        return null;
    }
}
