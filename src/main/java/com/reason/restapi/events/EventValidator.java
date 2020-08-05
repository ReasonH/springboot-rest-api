package com.reason.restapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object object, Errors errors) {
        EventDto eventDto = (EventDto) object;
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0){
            errors.reject("wrongPrices", "Values for prices are wrong");
            // global error code 추가
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
        endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())){
            errors.rejectValue("endEventDateTime", "wrongValue", "eventDateTime is wrong");
            // 필드에 대한 에러코드 추가
        }

        // TODO BeginEventDateTime
        // TODO CloseEnrollmentDateTime
    }
}
