package io.github.ktg.ticketing.persistence.reservation.adapter;

import io.github.ktg.ticketing.domain.reservation.port.out.WaitingPaymentReservationStorePort;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * WaitingPaymentReservationStorePort 구현 Adapter
 * - 결제 대기 예약 관리를 위한 저장소 기능 구현 (저장/조회/삭제)
 */
@Component
@RequiredArgsConstructor
public class WaitingPaymentReservationStoreAdapter implements WaitingPaymentReservationStorePort {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String RESERVATION_WAITING_PAYMENT_KEY_PREFIX = "reservation:waiting_payment:";

    private static String buildKey(Long reservationId) {
        return RESERVATION_WAITING_PAYMENT_KEY_PREFIX.concat(reservationId.toString());
    }

    /**
     * 결제 대기 예약 저장
     * - 만료 시간 이 지나면 저장소에서 제외
     * @param reservationId 예약 식별자
     * @param ttl 만료 시간
     */
    @Override
    public void store(Long reservationId, Duration ttl) {
        String key = buildKey(reservationId);
        redisTemplate.opsForValue().set(key, reservationId.toString(), ttl);
    }

    /**
     * 결제 대기 예약 존재 여부
     * @param reservationId 예약 식별자
     * @return Boolean 존재 여부
     */
    @Override
    public boolean exists(Long reservationId) {
        return redisTemplate.hasKey(buildKey(reservationId));
    }

    /**
     * 결제 대기 상태 예약 삭제 (결제 만료,완료,취소)
     * @param reservationId 예약 식별자
     */
    @Override
    public void remove(Long reservationId) {
        redisTemplate.delete(buildKey(reservationId));
    }
}
