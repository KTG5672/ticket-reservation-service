package io.github.ktg.ticketing.domain.reservation.port.out;

/**
 * 결제 대기(WAITING_PAYMENT) 예약을 외부 저장소(예: Redis)에 보관/조회/삭제하기 위한 Port
 */
public interface WaitingPaymentReservationStorePort {

    /**
     * 결제 대기 예약을 저장소에 등록
     * @param reservationId 예약 식별자
     * @param minutes 만료 시각(분)
     */
    void store(Long reservationId, long minutes);

    /**
     * 결제 대기 예약이 존재 여부 확인
     * @param reservationId 예약 식별자
     * @return Boolean 존재 여부
     */
    boolean exists(Long reservationId);

    /**
     * 결제 만료,완료,취소 시 저장소에서 제거
     * @param reservationId 예약 식별자
     */
    void remove(Long reservationId);
}
