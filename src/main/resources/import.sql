-- import.sql file

 -- parts
insert into part (sku, name, price) values ('A1', 'Abrikosai', 9.98);
insert into part (sku, name, price) values ('A2', 'Ananasai', 8.50);
insert into part (sku, name, price) values ('A3', 'Arbūzai', 0.55);
insert into part (sku, name, price) values ('A4', 'Agurkai', 12.34);
insert into part (sku, name, price) values ('A5', 'Agrastai', 9.25);

-- order #1
insert into `order` (id, date, no, version) values (default, '2018-11-01', 'ABC 0001', 1);

insert into `order_item` (id, order_id, part_id, quantity, price) values (default, 1, 1, 10, 9.98);
insert into `order_item` (id, order_id, part_id, quantity, price) values (default, 1, 2, 3, 8.50);
insert into `order_item` (id, order_id, part_id, quantity, price) values (default, 1, 3, 25, 0.55);

-- order #2
insert into `order` (id, date, no, version) values (default, '2018-11-02', 'ABC 0002', 1);

insert into `order_item` (id, order_id, part_id, quantity, price) values (default, 2, 1, 3, 9.98);
insert into `order_item` (id, order_id, part_id, quantity, price) values (default, 2, 4, 5, 12.34);
insert into `order_item` (id, order_id, part_id, quantity, price) values (default, 2, 5, 2, 9.25);
insert into `order_item` (id, order_id, part_id, quantity, price) values (default, 2, 5, 4, 8.99);

-- update orders items totals
update `order_item` set total = quantity * price;

-- update orders totals
update `order` o set total = (select sum(i.total) from `order_item` i where i.order_id = o.id);
