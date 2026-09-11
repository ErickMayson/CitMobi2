INSERT INTO T_VEI_GARAGEM (VEI_GARAGEM_ID, VEI_GARAGEM_DESCRICAO, GLB_MUNICIPIO_COD, GLB_OPERADOR_ID, VEI_GARAGEM_LOGRADOURO, VEI_GARAGEM_NUMERO, VEI_GARAGEM_CEP)
VALUES 
(1, 'Garagem Central', 3550308, 3, 'Av. Celso Garcia', '1000', '03064000'),
(2, 'Garagem Norte', 3550308, 3, 'Av. Cruzeiro do Sul', '500', '02030000'),
(3, 'Garagem Sul', 3550308, 3, 'Av. Santo Amaro', '4000', '04556200'),
(4, 'Garagem Leste', 3550308, 3, 'Av. Imperador', '2970', '03694000'),
(5, 'Garagem Oeste', 3550308, 3, 'Av. Corifeu de Azevedo Marques', '3000', '05339000'),
(6, 'Garagem Metrópole Brás', 3550308, 1, 'Av. Rangel Pestana', '1500', '03001000'),
(7, 'Garagem Metrópole Santo Amaro', 3550308, 1, 'Av. Guido Caloi', '2000', '05802140'),
(8, 'Garagem Cit Mobi Matriz', 3550308, 9, 'Av. Paulista', '1000', '01310100');

-- Synchronize sequence
SELECT setval(pg_get_serial_sequence('T_VEI_GARAGEM', 'vei_garagem_id'), coalesce(max(vei_garagem_id), 1)) FROM T_VEI_GARAGEM;
