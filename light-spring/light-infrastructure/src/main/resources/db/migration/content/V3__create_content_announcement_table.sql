CREATE TABLE light_content_announcement
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    title      VARCHAR(120) NOT NULL,
    summary    VARCHAR(500) NULL,
    content    LONGTEXT     NOT NULL,
    type       VARCHAR(32)  NOT NULL,
    priority   VARCHAR(16)  NOT NULL,
    status     VARCHAR(16)  NOT NULL DEFAULT 'DRAFT',
    publish_at DATETIME(3)  NULL,
    expire_at  DATETIME(3)  NULL,
    created_by BIGINT       NOT NULL,
    updated_by BIGINT       NOT NULL,
    created_at DATETIME(3)  NOT NULL,
    updated_at DATETIME(3)  NOT NULL,
    PRIMARY KEY (id),
    KEY idx_light_content_announcement_status_publish (status, publish_at),
    KEY idx_light_content_announcement_type (type),
    KEY idx_light_content_announcement_priority (priority)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
