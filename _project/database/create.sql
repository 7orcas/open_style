/********************************************************************************************************************************
 * Open Style Database Schema
 ********************************************************************************************************************************/



/********************************************************************************************************************************
 * CONTROL TABLES
 ********************************************************************************************************************************/
CREATE SCHEMA cntrl;

CREATE TABLE cntrl.company (
    id                bigint NOT NULL PRIMARY KEY,
    comp_nr           int NOT NULL UNIQUE,
    create_ts         timestamp with time zone default current_timestamp,
    create_id         bigint,
    update_ts         timestamp with time zone default current_timestamp,
    active            boolean default true,
    config            varchar,
    code              varchar(20) NOT NULL UNIQUE,
    customer_nr       int NOT NULL DEFAULT 0
);
COMMENT ON TABLE cntrl.company IS 'Companies, ie Maintainable';
COMMENT ON COLUMN  cntrl.company.config IS 'Unique configuration for company';
COMMENT ON COLUMN  cntrl.company.customer_nr IS 'ID to identify the actual customer';


/* Patch control */
CREATE TABLE cntrl.patch (
    id                bigint NOT NULL PRIMARY KEY,
    descr             varchar,
    create_ts         timestamp with time zone default current_timestamp
);
COMMENT ON TABLE cntrl.patch  IS 'Patches implemented in this database';


/* Userids */
CREATE TABLE cntrl._yzh_user (
    id                bigint NOT NULL PRIMARY KEY,
    comp_nr           int NOT NULL references cntrl.company(comp_nr),
    create_ts         timestamp with time zone default current_timestamp,
    create_id         bigint,
    update_ts         timestamp with time zone default current_timestamp,
    update_id         bigint,
    active            boolean default true,
    lang_code         varchar(2),
    _jhew             varchar(30) NOT NULL,
    _gghae            varchar,
    admin             boolean NOT NULL default false,
    comp_nrs          varchar,
    locked            boolean NOT NULL default false,
    trys              int NOT NULL default 0,
    site              varchar,
    groups            varchar,
    params            varchar,
    last_login        timestamp with time zone,
    config            varchar,
    last_logout       timestamp with time zone,
    UNIQUE (_jhew)
);
COMMENT ON TABLE cntrl._yzh_user IS 'Userids';
COMMENT ON COLUMN  cntrl._yzh_user._jhew IS 'Userid';
COMMENT ON COLUMN  cntrl._yzh_user._gghae IS 'MD5 Password';
COMMENT ON COLUMN  cntrl._yzh_user.trys IS 'Number of continuous false attempted logins';
COMMENT ON COLUMN  cntrl._yzh_user.comp_nr IS 'Primary login company number for the user';
COMMENT ON COLUMN  cntrl._yzh_user.comp_nrs IS 'Comma delimited list of all company numbers (or * for all) user can access';
CREATE UNIQUE INDEX yzh_user_uc ON cntrl._yzh_user (_jhew);


/* Roles */
CREATE TABLE cntrl._yzh_role (
    id                bigint NOT NULL PRIMARY KEY,
    comp_nr           int NOT NULL references cntrl.company(comp_nr),
    create_ts         timestamp with time zone default current_timestamp,
    create_id         bigint,
    update_ts         timestamp with time zone default current_timestamp,
    update_id         bigint,
    active            boolean default true,
    role_name         varchar NOT NULL,
    role_value        varchar NOT NULL,
    role_descr        varchar
);
COMMENT ON TABLE cntrl._yzh_role IS 'System roles';
CREATE UNIQUE INDEX yzh_role_uc ON cntrl._yzh_role (comp_nr, role_name, role_value);


/* User roles */
CREATE TABLE cntrl._yzh_user_role (
    id                bigint NOT NULL PRIMARY KEY,
    comp_nr           int NOT NULL references cntrl.company(comp_nr),
    create_ts         timestamp with time zone default current_timestamp,
    create_id         bigint,
    update_ts         timestamp with time zone default current_timestamp,
    update_id         bigint,
    active            boolean default true,
    _yzh_user_id      bigint NOT NULL references cntrl._yzh_user(id),
    _yzh_role_id      bigint NOT NULL references cntrl._yzh_role(id)
);
COMMENT ON TABLE cntrl._yzh_user_role IS 'Join table of users and roles';
CREATE UNIQUE INDEX yzh_user_role_uc ON cntrl._yzh_user_role (_yzh_user_id, _yzh_role_id);



/* Language Keys (field is called code to avoid sql problems with key) */
CREATE TABLE cntrl.langkey (
    id                bigint NOT NULL PRIMARY KEY,
    comp_nr           int NOT NULL default 0,
    create_ts         timestamp with time zone default current_timestamp,
    create_id         bigint,
    update_ts         timestamp with time zone default current_timestamp,
    update_id         bigint,
    active            boolean default true,
    code              varchar(20) UNIQUE,
    sets              varchar,
    client            boolean default true
);
COMMENT ON TABLE cntrl.langkey IS 'Language Keys';
CREATE UNIQUE INDEX langcode_uc ON cntrl.langkey (comp_nr, code);

/* Language Values */
CREATE TABLE cntrl.langvalue (
    id                bigint NOT NULL PRIMARY KEY,
    langkey_id        bigint NOT NULL references cntrl.langkey(id),
    comp_nr           int NOT NULL default 0,
    create_ts         timestamp with time zone default current_timestamp,
    create_id         bigint,
    update_ts         timestamp with time zone default current_timestamp,
    update_id         bigint,
    active            boolean default true,
    langcode          varchar(5),
    text              varchar
);
COMMENT ON TABLE cntrl.langvalue IS 'Language Values for specific languages';
CREATE UNIQUE INDEX langvalue_uc ON cntrl.langvalue (langkey_id, langcode);


/********************************************************************************************************************************
 * PUBLIC TABLES
 ********************************************************************************************************************************/
CREATE TABLE menu_type (
    id                bigint NOT NULL PRIMARY KEY,
    comp_nr           int NOT NULL references cntrl.company(comp_nr),
    create_ts         timestamp with time zone default current_timestamp,
    create_id         bigint,
    update_ts         timestamp with time zone default current_timestamp,
    active            boolean default true,
    
    code              varchar(20) NOT NULL,
    descr             varchar(50),
    sort              int NOT NULL
);
COMMENT ON TABLE menu_type IS 'Menu Item Type';
CREATE UNIQUE INDEX menu_type_u1 ON menu_type (comp_nr,code);
CREATE UNIQUE INDEX menu_type_u2 ON menu_type (comp_nr,sort);

CREATE TABLE menu (
    id                bigint NOT NULL PRIMARY KEY,
    comp_nr           int NOT NULL references cntrl.company(comp_nr),
    create_ts         timestamp with time zone default current_timestamp,
    create_id         bigint,
    update_ts         timestamp with time zone default current_timestamp,
    active            boolean default true,
    
    menu_type_id      bigint NOT NULL references menu_type(id),
    code_langkey      varchar(20) NOT NULL,
    descr             varchar(50),
    sort              int NOT NULL
);
COMMENT ON TABLE menu IS 'Menu Items';
CREATE UNIQUE INDEX menu_u1 ON menu (comp_nr,code_langkey);
CREATE UNIQUE INDEX menu_u2 ON menu (comp_nr,sort);




CREATE SEQUENCE cntrl.seq_id_language
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 10000
  CACHE 1;



CREATE SEQUENCE seq_id_entity
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 10000
  CACHE 1;



