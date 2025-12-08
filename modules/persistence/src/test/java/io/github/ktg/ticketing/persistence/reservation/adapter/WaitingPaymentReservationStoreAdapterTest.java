package io.github.ktg.ticketing.persistence.reservation.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ktg.ticketing.domain.reservation.port.out.WaitingPaymentReservationStorePort;
import io.github.ktg.ticketing.persistence.TestContainerForRedis;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

class WaitingPaymentReservationStoreAdapterTest extends TestContainerForRedis {

    RedisTemplate<String, String> redisTemplate;
    WaitingPaymentReservationStorePort waitingPaymentReservationStorePort;

    static final String RESERVATION_WAITING_PAYMENT_KEY_PREFIX = "reservation:waiting_payment:";

    @BeforeEach
    void setUp() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
            REDIS_CONTAINER.getHost(),
            REDIS_CONTAINER.getMappedPort(6379)
        );
        connectionFactory.afterPropertiesSet();
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
        waitingPaymentReservationStorePort = new WaitingPaymentReservationStoreAdapter(redisTemplate);
    }

    @Test
    @DisplayName("결제 대기 예약 저장소 저장 테스트")
    void 결제_대기_예약_저장소_저장_테스트() {
        // given
        Long reservationId = 1L;
        long minutes = 60;
        // when
        waitingPaymentReservationStorePort.store(reservationId, Duration.ofMinutes(minutes));
        // then
        String key = RESERVATION_WAITING_PAYMENT_KEY_PREFIX + reservationId;
        String find = redisTemplate.opsForValue().get(key);
        assertThat(find).isNotNull();
    }

    @Test
    @DisplayName("결제 대기 예약 저장소 저장 시 TTL 테스트")
    void 결제_대기_예약_저장소_저장시_TTL_테스트() {
        // given
        Long reservationId = 1L;
        long seconds = 10;
        // when
        waitingPaymentReservationStorePort.store(reservationId, Duration.ofSeconds(seconds));
        // then
        String key = RESERVATION_WAITING_PAYMENT_KEY_PREFIX + reservationId;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        assertThat(ttl)
            .isNotNull()
            .isGreaterThan(0);
    }

    @Test
    @DisplayName("결제 대기 예약 저장소 존재 여부 테스트")
    void 결제_대기_예약_저장소_존재여부_테스트() {
        // given
        Long existsReservationId = 1L;
        Long notExistsReservationId = 2L;
        long minutes = 60;

        waitingPaymentReservationStorePort.store(existsReservationId, Duration.ofMinutes(minutes));
        // when
        boolean exists = waitingPaymentReservationStorePort.exists(existsReservationId);
        boolean notExists = waitingPaymentReservationStorePort.exists(notExistsReservationId);
        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("결제 대기 예약 저장소 삭제 테스트")
    void 결제_대기_예약_저장소_삭제_테스트() {
        // given
        Long reservationId = 1L;
        long minutes = 60;

        waitingPaymentReservationStorePort.store(reservationId, Duration.ofMinutes(minutes));

        // when
        waitingPaymentReservationStorePort.remove(reservationId);

        // then
        boolean exists = waitingPaymentReservationStorePort.exists(reservationId);
        assertThat(exists).isFalse();
    }

}