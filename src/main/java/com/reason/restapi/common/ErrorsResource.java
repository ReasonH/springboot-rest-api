package com.reason.restapi.common;

import com.reason.restapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    public static EntityModel<Errors> getErrorResource(Errors content, Link... links){
        // error resource (error entity)는 항상 index에 대한 relation link를 포함해야 한다.
        return of(content, links).add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}