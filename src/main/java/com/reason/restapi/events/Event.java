package com.reason.restapi.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.reason.restapi.accounts.Account;
import com.reason.restapi.accounts.AccountSerializer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id") // 상호참조로 인해 stack over flow 발생 가능함, 웬만하면 id로 해시코드만들것
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; //(optional) 이게 없으면 온라인 모임
    private int basePrice; //(optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT; // 기본적으로 DRAFT로 생성
    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager; // 이벤트 조회에서 account 정보가 구체적으로 들어갈 필요가 없음

    public void update() {
        this.free = this.basePrice == 0 && this.maxPrice == 0;
        this.offline = this.location != null && !this.location.isBlank();
    }
}
