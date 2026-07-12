INSERT INTO T_VEI_GARAGEM(
   VEI_GARAGEM_ID,
   VEI_GARAGEM_DESCRICAO,
   GLB_OPERADOR_CNPJ,
   GLB_MUNICIPIO_COD,
   VEI_GARAGEM_LOGRADOURO,
   VEI_GARAGEM_NUMERO,
   VEI_GARAGEM_CEP
) VALUES (
   DEFAULT,
   'UNIDADE IMPERADOR',
   '31974104000120',
   3550308,
   'Av. Águia de Haia',
   '2970',
   '03694000'
);

INSERT INTO T_VEI_VEICULOMODELO (
    VEI_VEICULOMODELO_ID,
    VEI_VEICULO_MARCA,
    VEI_VEICULO_MODELO,
    VEI_VEICULO_EIXO,
    VEI_VEICULO_TIPO
) VALUES (
    DEFAULT,                   -- BIGSERIAL, auto increment
    'CAIO',                     -- Marca
    'MILLENIUM III',            -- Modelo
    2,                          -- Eixos
    'BASICO'                    -- Tipo
);

INSERT INTO T_VEI_VEICULO (
    VEI_VEICULO_PLACA,
    VEI_VEICULO_ID,
    GLB_OPERADOR_CNPJ,
    VEI_VEICULOMODELO_ID,
    VEI_VEICULO_CAPACIDADE,
    VEI_VEICULO_ANOFABRICACAO,
    VEI_VEICULO_FLAGATIVO
) VALUES (
    'ABC1234',                      -- Placa
    '36001',                        -- ID interno
    '31974104000120',                -- Operador
    (SELECT VEI_VEICULOMODELO_ID FROM T_VEI_VEICULOMODELO WHERE VEI_VEICULO_MODELO='MILLENIUM III'),
    65,                              -- Capacidade
    '2015',                          -- Ano de Fabricação
    'S'                              -- Ativo
);

INSERT INTO T_VEI_LINHAPLACA (
    LIN_LINHA_ID,
    LIN_LINHA_ATENDIMENTO,
    GLB_MUNICIPIO_COD,
    VEI_VEICULO_PLACA,
    VEI_LINHAPLACA_DIASEMANA,
    VEI_LINHAPLACA_HORAINICIO,
    VEI_LINHAPLACA_HORAFIM
) VALUES (
    '3301',              -- Linha
    '10',
    3550308,             -- São Paulo
    'ABC1234',           -- Placa do veiculo
    'SEGUNDA',           -- Dia da semana
    '06:00:00',          -- Hora de início
    '22:00:00'           -- Hora de fim
);

INSERT INTO T_VEI_LINHAPLACA (
    LIN_LINHA_ID,
    LIN_LINHA_ATENDIMENTO,
    GLB_MUNICIPIO_COD,
    VEI_VEICULO_PLACA,
    VEI_LINHAPLACA_DIASEMANA,
    VEI_LINHAPLACA_HORAINICIO,
    VEI_LINHAPLACA_HORAFIM
) VALUES (
    '3301',              -- Linha
    '10',
    3550308,             -- São Paulo
    'ABC1234',           -- Placa do veiculo
    'TERCA',           -- Dia da semana
    '06:00:00',          -- Hora de início
    '22:00:00'           -- Hora de fim
);

INSERT INTO T_VEI_LINHAPLACA (
    LIN_LINHA_ID,
    LIN_LINHA_ATENDIMENTO,
    GLB_MUNICIPIO_COD,
    VEI_VEICULO_PLACA,
    VEI_LINHAPLACA_DIASEMANA,
    VEI_LINHAPLACA_HORAINICIO,
    VEI_LINHAPLACA_HORAFIM
) VALUES (
    '3301',              -- Linha
    '10',
    3550308,             -- São Paulo
    'ABC1234',           -- Placa do veiculo
    'QUARTA',           -- Dia da semana
    '06:00:00',          -- Hora de início
    '22:00:00'           -- Hora de fim
);

INSERT INTO T_VEI_LINHAPLACA (
    LIN_LINHA_ID,
    LIN_LINHA_ATENDIMENTO,
    GLB_MUNICIPIO_COD,
    VEI_VEICULO_PLACA,
    VEI_LINHAPLACA_DIASEMANA,
    VEI_LINHAPLACA_HORAINICIO,
    VEI_LINHAPLACA_HORAFIM
) VALUES (
    '3301',              -- Linha
    '10',
    3550308,             -- São Paulo
    'ABC1234',           -- Placa do veiculo
    'QUINTA',           -- Dia da semana
    '06:00:00',          -- Hora de início
    '22:00:00'           -- Hora de fim
);

INSERT INTO T_VEI_LINHAPLACA (
    LIN_LINHA_ID,
    LIN_LINHA_ATENDIMENTO,
    GLB_MUNICIPIO_COD,
    VEI_VEICULO_PLACA,
    VEI_LINHAPLACA_DIASEMANA,
    VEI_LINHAPLACA_HORAINICIO,
    VEI_LINHAPLACA_HORAFIM
) VALUES (
    '3301',              -- Linha
    '10',
    3550308,             -- São Paulo
    'ABC1234',           -- Placa do veiculo
    'SEXTA',           -- Dia da semana
    '06:00:00',          -- Hora de início
    '22:00:00'           -- Hora de fim
);

INSERT INTO T_VEI_MOTORISTAPLACA (
    USU_USUARIO_CPF,
    VEI_VEICULO_PLACA,
    VEI_MOTORISTAPLACA_DIASEMANA,
    VEI_MOTORISTAPLACA_HORAINICIO,
    VEI_MOTORISTAPLACA_HORAFIM
) VALUES (
    '79458254000',      -- CPF do motorista Antonio Ferreira
    'ABC1234',          -- Veículo
    'SEGUNDA',          -- Dia
    '06:00:00',         -- Início turno
    '22:00:00'          -- Fim turno
);

INSERT INTO T_VEI_MOTORISTAPLACA (
    USU_USUARIO_CPF,
    VEI_VEICULO_PLACA,
    VEI_MOTORISTAPLACA_DIASEMANA,
    VEI_MOTORISTAPLACA_HORAINICIO,
    VEI_MOTORISTAPLACA_HORAFIM
) VALUES (
    '79458254000',      -- CPF do motorista Antonio Ferreira
    'ABC1234',          -- Veículo
    'QUINTA',          -- Dia
    '06:00:00',         -- Início turno
    '22:00:00'          -- Fim turno
);

INSERT INTO T_USU_MOTORISTAHORA (
    USU_USUARIO_CPF,
    USU_MOTORISTAHORA_DIASEMANA,
    USU_MOTORISTAHORA_HORAINICIO,
    USU_MOTORISTAHORA_HORAFIM,
    USU_MOTORISTAHORA_PAUSAINICIO,
    USU_MOTORISTAHORA_PAUSAFIM
) VALUES (
    '79458254000',
    'SEGUNDA',
    '06:00:00',
    '22:00:00',
    '12:00:00',
    '14:00:00'
);

INSERT INTO T_USU_MOTORISTAHORA (
    USU_USUARIO_CPF,
    USU_MOTORISTAHORA_DIASEMANA,
    USU_MOTORISTAHORA_HORAINICIO,
    USU_MOTORISTAHORA_HORAFIM,
    USU_MOTORISTAHORA_PAUSAINICIO,
    USU_MOTORISTAHORA_PAUSAFIM
) VALUES (
    '79458254000',
    'QUINTA',
    '06:00:00',
    '22:00:00',
    '12:00:00',
    '14:00:00'
);






