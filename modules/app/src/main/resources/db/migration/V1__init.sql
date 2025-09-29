-- 유저
CREATE TABLE users (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    created_by VARCHAR(40),
    updated_by VARCHAR(40),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 이벤트
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    poster_url VARCHAR(255),
    place VARCHAR(255),
    event_info MEDIUMTEXT,
    ticket_open_at DATETIME,
    ticket_close_at DATETIME,
    created_by VARCHAR(40),
    updated_by VARCHAR(40),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 이벤트 일정
CREATE TABLE event_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    event_start_at DATETIME,
    user_booking_limit INT,
    created_by VARCHAR(40),
    updated_by VARCHAR(40),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 이벤트 좌석
CREATE TABLE event_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_schedule_id BIGINT NOT NULL,
    status VARCHAR(255),
    zone VARCHAR(10),
    no INT,
    price INT,
    created_by VARCHAR(40),
    updated_by VARCHAR(40),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 예약
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(40) NOT NULL,
    status VARCHAR(10),
    created_by VARCHAR(40),
    updated_by VARCHAR(40),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 예약-좌석 매핑
CREATE TABLE reservation_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    price INT
);

-- 결제
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    user_id VARCHAR(40) NOT NULL,
    amount INT,
    status VARCHAR(10),
    payment_at DATETIME,
    created_by VARCHAR(40),
    updated_by VARCHAR(40),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 결제 이력
CREATE TABLE payment_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    amount INT,
    status VARCHAR(10),
    payment_at DATETIME,
    fail_reason VARCHAR(255),
    created_by VARCHAR(40),
    updated_by VARCHAR(40),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);