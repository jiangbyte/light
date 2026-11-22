ALTER TABLE light_admin_user
    ADD COLUMN token_version INT NOT NULL DEFAULT 0 AFTER status;

ALTER TABLE light_portal_user
    ADD COLUMN token_version INT NOT NULL DEFAULT 0 AFTER status;
