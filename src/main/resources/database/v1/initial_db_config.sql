-- changeset andrei:createTables
CREATE TABLE inventory(
    bean_id int primary key,
    quantity numeric not null,
    price numeric not null
);

CREATE TABLE bean_order(
    id serial primary key,
    status varchar(20) not null,
    client_id int not null
);

CREATE TABLE order_item(
    bean_id int references inventory(bean_id) not null,
    order_id int references bean_order(id) not null,
    quantity numeric not null,
    PRIMARY KEY (bean_id, order_id)
);
--rollback DROP TABLE order_item;
--rollback DROP TABLE bean_order;
--rollback DROP TABLE inventory;

--changeset andrei:grants
GRANT USAGE ON SCHEMA inventory TO inventory;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA inventory TO inventory;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA inventory TO inventory;
--rollback REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA inventory FROM inventory;
--rollback REVOKE ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA inventory FROM inventory;
--rollback REVOKE USAGE ON SCHEMA inventory FROM inventory;