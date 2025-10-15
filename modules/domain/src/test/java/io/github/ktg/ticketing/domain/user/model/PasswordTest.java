package io.github.ktg.ticketing.domain.user.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ktg.ticketing.domain.user.exception.PasswordNotValidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordTest {

    @Test
    @DisplayName("패스워드는 null 미허용")
    void 패스워드는_not_null() {
        // given
        // when
        // then
        assertThatThrownBy(() -> new Password(null))
            .isInstanceOf(PasswordNotValidException.class);
    }

    @Test
    @DisplayName("패스워드는 빈 값 미허용")
    void 패스워드는_not_empty() {
        // given
        // when
        // then
        assertThatThrownBy(() -> new Password(""))
            .isInstanceOf(PasswordNotValidException.class);
    }

    @Test
    @DisplayName("패스숴드는 8글자 이상")
    void 패스워드는_8글자_이상() {
        // given
        String input = "123456";
        // when
        // then
        assertThatThrownBy(() -> new Password(input))
            .isInstanceOf(PasswordNotValidException.class);
    }

    @Test
    @DisplayName("패스워드는 24글자 이하")
    void 패스워드는_24글자_이하() {
        // given
        String input = "11111_11111_11111_11111_11111_11111";
        // when
        // then
        assertThatThrownBy(() -> new Password(input))
            .isInstanceOf(PasswordNotValidException.class);
    }

    @Test
    @DisplayName("패스워드는 영문자 + 숫자 조합 (숫자만 입력)")
    void 패스워드는_영문자_숫자_조합_1() {
        // given
        String input = "12345678";
        // when
        // then
        assertThatThrownBy(() -> new Password(input))
            .isInstanceOf(PasswordNotValidException.class);
    }

    @Test
    @DisplayName("패스워드는 영문자 + 숫자 조합 (영문자만 입력)")
    void 패스워드는_영문자_숫자_조합_2() {
        // given
        String input = "abcdefghijklmnop";
        // when
        // then
        assertThatThrownBy(() -> new Password(input))
            .isInstanceOf(PasswordNotValidException.class);
    }

    @Test
    @DisplayName("패스워드는 영문자 + 숫자 조합 (영문자 + 숫자 + 특수문자 입력)")
    void 패스워드는_영문자_숫자_조합_3() {
        // given
        String input = "1111aaaa!@#";
        // when
        Password password = new Password(input);
        // then
        String value = password.value();
        assertThat(value).isEqualTo(input);
    }

    @Test
    @DisplayName("패스워드는 공백 미포함")
    void 패스워드는_공백을_포함하지_않음() {
        // given
        String input = "123 456as2";
        // when
        // then
        assertThatThrownBy(() -> new Password(input))
            .isInstanceOf(PasswordNotValidException.class);
    }
    @Test
    @DisplayName("패스워드는 개행 문자 미포함")
    void 패스워드는_개행_문자를_포함하지_않음() {
        // given
        String input = "123\n\t456as2";
        // when
        // then
        assertThatThrownBy(() -> new Password(input))
            .isInstanceOf(PasswordNotValidException.class);
    }

}