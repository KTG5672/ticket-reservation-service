package io.github.ktg.ticketing.domain.user.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ktg.ticketing.domain.user.exception.EmailNotValidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    @DisplayName("이메일 입력 값은 trim + lowercase 처리 해준다.")
    void 이메일은_trim_lowercase_처리() {
        // given
        String emailStr = "   teSt@teSt.com  ";
        // when
        Email email = new Email(emailStr);
        // then
        assertThat(email.value()).isEqualTo(emailStr.trim().toLowerCase());
    }

    @Test
    @DisplayName("이메일은 null 미허용")
    void 이메일은_null_미허용() {
        // given
        String email = null;
        // when
        // then
        assertThatThrownBy(() -> new Email(email))
            .isInstanceOf(EmailNotValidException.class);
    }

    @Test
    @DisplayName("이메일은 빈 값 미허용")
    void 이메일은_빈값_미허용() {
        // given
        String email = "   ";
        // when
        // then
        assertThatThrownBy(() -> new Email(email))
            .isInstanceOf(EmailNotValidException.class);
    }

    @Test
    @DisplayName("이메일은 이메일 형식 (맞게 입력)")
    void 이메일은_이메일_형식() {
        // given
        String emailStr = "test@test.com";
        // when
        Email email = new Email(emailStr);
        // then
        assertThat(email.value()).isEqualTo(emailStr);
    }

    @Test
    @DisplayName("이메일은 @가 필수여야 한다.")
    void 이메일은_골뱅이_필수() {
        // given
        String emailStr = "test";
        // when
        // then
        assertThatThrownBy(() -> new Email(emailStr))
            .isInstanceOf(EmailNotValidException.class);
    }

    @Test
    @DisplayName("이메일의 도메인은 (.)이 필수여야 한다.")
    void 이메일_도메인에_점_필수() {
        // given
        String emailStr = "test@asdd";
        // when
        // then
        assertThatThrownBy(() -> new Email(emailStr))
            .isInstanceOf(EmailNotValidException.class);
    }

    @Test
    @DisplayName("이메일은 TLD가 필수여야 한다.")
    void 이메일_TLD_필수() {
        // given
        String emailStr = "test@asdd.";
        // when
        // then
        assertThatThrownBy(() -> new Email(emailStr))
            .isInstanceOf(EmailNotValidException.class);
    }

    @Test
    @DisplayName("이메일 TLD는 2글자 이상이어야 한다.")
    void 이메일_TLD는_2글자_이상() {
        // given
        String emailStr = "test@asdd.a";
        // when
        // then
        assertThatThrownBy(() -> new Email(emailStr))
            .isInstanceOf(EmailNotValidException.class);
    }


}