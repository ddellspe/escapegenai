INSERT INTO team (id, name) VALUES ('00000000000000000000000000000000', 'teamName');
INSERT INTO invoice(id, company, address, difference) VALUES (123456789, 'companyName', 'address, city, state ZIP', 0);
INSERT INTO product (id, name, price) VALUES (1, 'product', 10);
INSERT INTO invoice_product (id, invoice_id, product_id, qty) VALUES ('00000000000000000000000000000001', 123456789, 1, 10);
INSERT INTO team_invoice (id, team_id, invoice_id, first_task) VALUES ('00000000000000000000000000000002', '00000000000000000000000000000000', 123456789, 1);
