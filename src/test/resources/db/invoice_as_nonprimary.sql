INSERT INTO team (id, name) VALUES ('00000000000000000000000000000003', 'teamName');
INSERT INTO invoice(id, company, address, difference) VALUES (123456790, 'companyName', 'address, city, state ZIP', 0);
INSERT INTO product (id, name, price) VALUES (2, 'product', 10);
INSERT INTO invoice_product (id, invoice_id, product_id, qty) VALUES ('00000000000000000000000000000004', 123456790, 2, 10);
INSERT INTO team_invoice (id, team_id, invoice_id, first_task) VALUES ('00000000000000000000000000000005', '00000000000000000000000000000003', 123456790, 0);
