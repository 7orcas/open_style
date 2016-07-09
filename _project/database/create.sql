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
    update_id         bigint,
    active            boolean default true,
    config            varchar,
    code              varchar(20) UNIQUE,
    codeId            varchar(20) NOT NULL,
    customer_nr       int NOT NULL DEFAULT 0
);
COMMENT ON TABLE cntrl.company IS 'Companies, ie Maintable';
COMMENT ON COLUMN  cntrl.company.config IS 'Unique configuration for company';
COMMENT ON COLUMN  cntrl.company.codeId IS 'IDs to identify special processing requirements';
COMMENT ON COLUMN  cntrl.company.customer_nr IS 'ID to identify the actual customer';


/* Patch control */
CREATE TABLE cntrl.patch (
    id                bigint NOT NULL PRIMARY KEY,
    descr             varchar,
    create_ts         timestamp with time zone default current_timestamp
);
COMMENT ON TABLE cntrl.patch  IS 'Patches implemented in this database';



/********************************************************************************************************************************
 * PUBLIC TABLES
 ********************************************************************************************************************************/
CREATE TABLE menu_type (
    id                bigint NOT NULL PRIMARY KEY,
    comp_nr           int NOT NULL references cntrl.company(comp_nr),
    create_ts         timestamp with time zone default current_timestamp,
    create_id         bigint,
    update_ts         timestamp with time zone default current_timestamp,
    update_id         bigint,
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
    update_id         bigint,
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



