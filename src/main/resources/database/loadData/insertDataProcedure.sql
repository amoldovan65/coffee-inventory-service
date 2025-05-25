create procedure insert_data()
    language plpgsql
as
$$
BEGIN
FOR i IN 1..1291 LOOP
        INSERT INTO inventory.inventory (bean_id, quantity, price) VALUES (i, floor(random() * 1000 + 10), floor(random() * 100 + 5));
END LOOP;
END;
$$;