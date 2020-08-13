package com.reason.restapi.events;

import com.reason.restapi.accounts.Account;
import com.reason.restapi.accounts.CurrentUser;
import com.reason.restapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE) // 해당 컨트롤러 모든 응답에 대해 해당 형식 produce
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    // autowired 생략 가능
    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody @Valid EventDto eventDto,
                                         Errors errors,
                                         @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update(); // 비즈니스 로직에 따라 field 값 보충
        event.setManager(currentUser);
        Event newEvent = this.eventRepository.save(event);

        // response 생성
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EntityModel<Event> eventResource = EventResource.getEventResource(event);
        // link를 type safe하게 만들 수 있음
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity<?> queryEvents(Pageable pageable,
                                         PagedResourcesAssembler<Event> assembler,
                                         @CurrentUser Account account){
        // ContextHolder 로부터 인증 유저를 주입받을 수 있음
        Page<Event> page = this.eventRepository.findAll(pageable);
        // entity를 event resource로 변환하여 받아옴으로써 이벤트마다 self description 달성
        var pagedModels = assembler.toModel(page, entity -> EventResource.getEventResource(entity));
        if(account != null) {
            pagedModels.add(linkTo(EventController.class).withRel("create-event"));
        }
        // Model로 변경 시 링크 추가 가능한 메서드가 생긴다.
        pagedModels.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Integer id,
                                      @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EntityModel<Event> eventResource = EventResource.getEventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
        if(event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Integer id,
                                         @RequestBody @Valid EventDto eventDto,
                                         Errors errors,
                                         @CurrentUser Account currentUser) {
        // 수정하려는 아이템의 아이디가 존재하는가?
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        // 수정하려는 값이 정상적인가? dto 제약조건 만족?
        if (errors.hasErrors()){
            return badRequest(errors); // 400
        }

        // 들어온 값이 비즈니스 로직을 만족하는가?
        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()){
            return badRequest(errors); // 400
        }

        Event existingEvent = optionalEvent.get();
        if (!existingEvent.getManager().equals(currentUser)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        this.modelMapper.map(eventDto, existingEvent); // 기존 이벤트에 덮어쓰기
        Event savedEvent = this.eventRepository.save(existingEvent);
        EntityModel<Event> eventResource = EventResource.getEventResource(savedEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity<?> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorsResource.getErrorResource(errors));
    }
}