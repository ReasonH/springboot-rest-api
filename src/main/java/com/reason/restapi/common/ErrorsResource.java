package com.reason.restapi.common;

import com.reason.restapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    public static EntityModel<Errors> getErrorResource(Errors content, Link... links){
        return of(content, links).add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
