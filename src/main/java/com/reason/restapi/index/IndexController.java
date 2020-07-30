package com.reason.restapi.index;

import com.reason.restapi.events.EventController;
import com.reason.restapi.events.EventResource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.server.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class IndexController {

    @GetMapping("/api")
    public EventResource index() {
        var index = new EventResource();
        index.add(linkTo(EventController.class).withRel("events"));
        return index;
    }
}
