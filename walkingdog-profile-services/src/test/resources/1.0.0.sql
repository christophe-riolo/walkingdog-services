/**
 * Author:  paoesco
 * Created: 12 déc. 2016
 */
CREATE TABLE T_USER (
    UUID varchar(255) PRIMARY KEY,
    EMAIL varchar(255) NOT NULL UNIQUE,
    PASSWORD varchar(255) NOT NULL,
    PHONE_UUID varchar(255),
    ENABLED boolean DEFAULT false NOT NULL,
    TOKEN varchar(255) NOT NULL
);

CREATE TABLE T_DOG (
    UUID varchar(255) PRIMARY KEY,
    NAME varchar(255) NOT NULL,
    BASE64IMAGE varchar(1000) NOT NULL,
    GENDER varchar(255) NOT NULL,
    BREED varchar (255) NOT NULL,
    BIRTHDATE date,
    USER_UUID varchar(255) NOT NULL REFERENCES T_USER (UUID)
);
