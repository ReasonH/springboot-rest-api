package com.reason.restapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0){
            errors.reject("wrongPrices", "Values for prices are wrong");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
        endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())){
            errors.rejectValue("endEventDateTime", "wrongValue", "eventDateTime is wrong");
            // 첫번째 파라미터는 프로퍼티 이름을 참조한다.
        }

        // TODO BeginEventDateTime
        // TODO CloseEnrollmentDateTime
    }
}
