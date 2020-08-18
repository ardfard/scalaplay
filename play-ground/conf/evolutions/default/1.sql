# --- !Ups

CREATE TABLE account (
  id         UUID    NOT NULL PRIMARY KEY,
  full_name  VARCHAR,
  email      VARCHAR,
  activated  BOOLEAN NOT NULL,
  avatar_url VARCHAR
);

CREATE TABLE login_info (
  id           BIGSERIAL NOT NULL PRIMARY KEY,
  provider_id  VARCHAR,
  provider_key VARCHAR
);

CREATE TABLE account_login_info (
  account_id       UUID   NOT NULL,
  login_info_id BIGINT NOT NULL,
  CONSTRAINT account_login_info_account_id_fk FOREIGN KEY (account_id) REFERENCES account (id),
  CONSTRAINT account_login_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES login_info (id)
);

CREATE TABLE oauth1_info (
  id            BIGSERIAL NOT NULL PRIMARY KEY,
  token         VARCHAR   NOT NULL,
  secret        VARCHAR   NOT NULL,
  login_info_id BIGINT    NOT NULL,
  CONSTRAINT oauth1_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES login_info (id)
);

CREATE TABLE oauth2_info (
  id            BIGSERIAL NOT NULL PRIMARY KEY,
  access_token  VARCHAR   NOT NULL,
  token_type    VARCHAR,
  expires_in    INT,
  refresh_token VARCHAR,
  login_info_id BIGINT    NOT NULL,
  CONSTRAINT oauth2_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES login_info (id)
);

CREATE TABLE password_info (
  hasher        VARCHAR NOT NULL,
  password      VARCHAR NOT NULL,
  salt          VARCHAR,
  login_info_id BIGINT  NOT NULL,
  CONSTRAINT password_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES login_info (id)
);

CREATE TABLE token (
  id      UUID        NOT NULL PRIMARY KEY,
  account_id UUID        NOT NULL,
  expiry  TIMESTAMPTZ NOT NULL,
  CONSTRAINT token_account_id_fk FOREIGN KEY (account_id) REFERENCES account (id)
);

# --- !Downs

DROP TABLE token;
DROP TABLE password_info;
DROP TABLE oauth2_info;
DROP TABLE oauth1_info;
DROP TABLE account_login_info;
DROP TABLE login_info;
DROP TABLE account;
