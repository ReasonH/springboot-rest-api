package com.reason.restapi.events;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {
    // 이벤트 리소스를 만들어주는 클래스, EntityModel형태로 반환해준다.

    public static EntityModel<Event> getEventResource(Event event, Link... links){
        // self relation link는 팩토리 메서드를 사용할때 함께 사용해 서 반환한다.
        return of(event, links).add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
