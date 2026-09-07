INSERT INTO T_VEI_VEICULO (
    VEI_VEICULO_ID,
    VEI_VEICULO_PLACA,
    VEI_VEICULO_CODIGO,
    GLB_OPERADOR_ID,
    VEI_VEICULOMODELO_ID,
    VEI_VEICULO_CAPACIDADE,
    VEI_VEICULO_ANOFABRICACAO,
    VEI_GARAGEM_ID,
    VEI_VEICULO_STATUS
) VALUES 
(
    1,
    'ABC-1234',
    'V001',
    3, -- Viacao Gato Preto
    1, -- CAIO MILLENIUM III Padrão
    80,
    '2018', 
    1, -- Garagem Central
    'ATIVO'
),
(
    2,
    'DEF-5678',
    'V002',
    3, -- Viacao Gato Preto
    3, -- CAIO APACHE VIP V BRT
    160,
    '2020', 
    2, -- Garagem Norte
    'ATIVO'
),
(
    3,
    'GHI-9012',
    'V003',
    3, -- Viacao Gato Preto
    2, -- CAIO MILLENIUM III Articulado
    120,
    '2019', 
    3, -- Garagem Sul
    'ATIVO'
);

-- Synchronize sequence
SELECT setval(pg_get_serial_sequence('T_VEI_VEICULO', 'vei_veiculo_id'), coalesce(max(vei_veiculo_id), 1)) FROM T_VEI_VEICULO;

