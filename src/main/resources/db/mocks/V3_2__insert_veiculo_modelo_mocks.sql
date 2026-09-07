INSERT INTO T_VEI_VEICULOMODELO (VEI_VEICULOMODELO_ID, VEI_VEICULO_MARCA, VEI_VEICULO_MODELO, VEI_VEICULO_EIXO, VEI_VEICULO_TIPO)
VALUES 
(1, 'CAIO', 'MILLENIUM III', 2, 'Padrão'),
(2, 'CAIO', 'MILLENIUM III', 3, 'Articulado'),
(3, 'CAIO', 'APACHE VIP V', 2, 'BRT');

-- Synchronize sequence
SELECT setval(pg_get_serial_sequence('T_VEI_VEICULOMODELO', 'vei_veiculomodelo_id'), coalesce(max(vei_veiculomodelo_id), 1)) FROM T_VEI_VEICULOMODELO;

