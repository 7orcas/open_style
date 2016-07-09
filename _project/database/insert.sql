
INSERT INTO cntrl.company (id,comp_nr,code,codeId,config) VALUES (0,0,'Processing','','');
INSERT INTO cntrl.company (id,comp_nr,code,codeId,config) VALUES (1,1,'Main','','');


/*-**************************************************************************
 * Menu Types
 ****************************************************************************/    
INSERT INTO menu_type (id,create_id,comp_nr,code,descr,sort) SELECT 10001 AS id,0,1,'Main Item','Main menu item', 1;    
INSERT INTO menu_type (id,create_id,comp_nr,code,descr,sort) SELECT 10002 AS id,0,1,'Sub Item 1','First sub-menu item', 2;
INSERT INTO menu_type (id,create_id,comp_nr,code,descr,sort) SELECT 10003 AS id,0,1,'Sub Item 2','Second sub-menu item', 3;


/*-**************************************************************************
 * Menu Types
 ****************************************************************************/    
INSERT INTO menu (id,create_id,comp_nr,menu_type_id,code_langkey,descr,sort) SELECT NEXTVAL('seq_id_entity') AS id,0,1,10001,'Main Item 1','Main menu item 1', 1;
INSERT INTO menu (id,create_id,comp_nr,menu_type_id,code_langkey,descr,sort) SELECT NEXTVAL('seq_id_entity') AS id,0,1,10001,'Main Item 2','Main menu item 2', 2;
INSERT INTO menu (id,create_id,comp_nr,menu_type_id,code_langkey,descr,sort) SELECT NEXTVAL('seq_id_entity') AS id,0,1,10001,'Main Item 3','Main menu item 3', 3;
