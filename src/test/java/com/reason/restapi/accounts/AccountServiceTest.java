package com.reason.restapi.accounts;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {
    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() {
        // Given
        String username = "hyuk@mail.com";
        String password = "yoo";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(account);
        // When
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
    }

    @Test
    public void findByUsernameFail() {
        // Given
        String username = "fail@fail.com";

        Exception exception = Assertions.assertThrows(UsernameNotFoundException.class, () ->{
            accountService.loadUserByUsername(username);
        });
        assertTrue(exception.getMessage().contains(username));

        // 정통 방식
//        try {
//            accountService.loadUserByUsername(username);
//            // load에서 catch구문으로 넘어가지 않은 경우에는 테스트 실패 메세지
//            fail("supposed to be failed");
//        } catch (UsernameNotFoundException e) {
//            // 에러 객체로부터 메세지에 유저이름 포함 여부 확인
//            assertThat(e.getMessage()).containsSequence(username);
//        }
    }
}