package eionet.xmlconv.qa.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 *
 *
 */
@RestController("/test")
public class StatusController {

    @GetMapping("/")
    public Mono<String> test() {
        return Mono.just("test");
    }
}
