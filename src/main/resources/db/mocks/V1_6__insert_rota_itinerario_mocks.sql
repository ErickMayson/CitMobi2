-- Routes for Linha 3 (9051) and Linha 5 (372F)
INSERT INTO T_LIN_ROTA (LIN_ROTA_ID, LIN_LINHA_ID, LIN_ROTA_PREFIXO, LIN_ROTA_SENTIDO)
VALUES (1, 3, 'PÇA. RAMOS DE AZEVEDO', 'IDA');

INSERT INTO T_LIN_ROTA (LIN_ROTA_ID, LIN_LINHA_ID, LIN_ROTA_PREFIXO, LIN_ROTA_SENTIDO)
VALUES (2, 3, 'TERM. LAPA', 'VOLTA');

INSERT INTO T_LIN_ROTA (LIN_ROTA_ID, LIN_LINHA_ID, LIN_ROTA_PREFIXO, LIN_ROTA_SENTIDO)
VALUES (3, 5, 'UNIVERSIDADE SAO JUDAS TADEU', 'IDA');

INSERT INTO T_LIN_ROTA (LIN_ROTA_ID, LIN_LINHA_ID, LIN_ROTA_PREFIXO, LIN_ROTA_SENTIDO)
VALUES (4, 5, 'METRO BRESSER', 'VOLTA');

-- Synchronize rota sequence
SELECT setval(pg_get_serial_sequence('T_LIN_ROTA', 'lin_rota_id'), coalesce(max(lin_rota_id), 1)) FROM T_LIN_ROTA;

-- Mock for 372F Itinerario (VOLTA route -> LIN_ROTA_ID = 4)
INSERT INTO T_LIN_ITINERARIO (LIN_ITINERARIO_ID, LIN_ROTA_ID, LIN_PARADA_ID, LIN_ITINERARIO_SEQUENCIA)
VALUES
(1, 4, 8, 1), -- R. Bresser, 2695 (Parada 8)
(2, 4, 7, 2), -- R. Taquari, 556 (Parada 7)
(3, 4, 6, 3), -- R. Jaibaras, 299 (Parada 6)
(4, 4, 5, 4), -- Av. Alcantara Machado, 2576 (Parada 5)
(5, 4, 4, 5), -- R. Bresser, 1954 (Parada 4)
(6, 4, 3, 6); -- R. Ipanema, 686 (Parada 3)

-- Synchronize itinerario sequence
SELECT setval(pg_get_serial_sequence('T_LIN_ITINERARIO', 'lin_itinerario_id'), coalesce(max(lin_itinerario_id), 1)) FROM T_LIN_ITINERARIO;

