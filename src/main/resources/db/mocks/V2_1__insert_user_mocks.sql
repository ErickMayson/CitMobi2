INSERT INTO T_USU_USUARIO (
    USU_USUARIO_ID,
    USU_USUARIO_LOGIN,
    GLB_OPERADOR_ID,
    USU_USUARIO_EMAIL,
    USU_USUARIO_SENHA,
    USU_USUARIO_NOME,
    USU_USUARIO_TELEFONE,
    USU_USUARIO_CPF,
    USU_USUARIO_ROLE,
    USU_USUARIO_FLAGATIVO,
    USU_USUARIO_DTCRIACAO
) VALUES
(
    '00000000-0000-0000-0001-000000000001',
    'Mobiadm',
    9, -- Cit Mobi
    'citMobi@citmobi.com.br',
    '$2a$10$9BH8374tLxctMcb/ZpBgzuwaOCh.4IEFvExT9H1BcLb0SpuRYSV6q',
    'ADMINISTRADOR DO SISTEMA',
    '11999999999',
    '72160206008',
    'ADMIN',
    'S',
    NOW()
),
(
    '00000000-0000-0000-0001-000000000002',
    'admin',
    9, -- Cit Mobi
    'admin@citmobi.com.br',
    '$2a$10$q/dv8vJAJUMgzpV8ZlSAPe.4L7D1eR5KRPiJ1FP/AJSyfC3wb.zIa',
    'ADMINISTRADOR DO SISTEMA',
    '11988887777',
    '00000000000',
    'ADMIN',
    'S',
    NOW()
),
(
    '00000000-0000-0000-0001-000000000003',
    'emferreira',
    9, -- Cit Mobi
    'emferreira@citmobi.com.br',
    '$2a$10$E9kiMoArVLh3a5uAsJoEVexBFpMXnOFeRLed9.PQVemAe7gesz3sK',
    'ERICK MAYSON CARDOSO FERREIRA',
    '11999990001',
    '12345678909',
    'ADMIN',
    'S',
    NOW()
),
(
    '00000000-0000-0000-0002-000000000001',
    'gtpadm',
    3, -- Viacao Gato Preto
    'ti_gatopreto@gatopreto.com',
    '$2a$10$5CAmDGBcLR7./akaUc5Czu9RaHYmD1z2H84lr13uzvUwDeT.N54qC',
    'ADMINISTRADOR GATO PRETO',
    '14987950411',
    '60870847000',
    'ADMIN',
    'S',
    NOW()
),
(
    '00000000-0000-0000-0002-000000000002',
    'ana.julia',
    1, -- Viacao Metropole Paulista
    'ana.julia@viacaomp.com',
    '$2a$10$9BH8374tLxctMcb/ZpBgzuwaOCh.4IEFvExT9H1BcLb0SpuRYSV6q',
    'ANA JULIA VIANA',
    '14988112233',
    '84395928846',
    'USER',
    'S',
    NOW()
);

-- Seed Access Grants
INSERT INTO T_USU_USUARIO_ACESSO (USU_USUARIO_ID, GLB_UF_COD, GLB_MUNICIPIO_COD)
VALUES
('00000000-0000-0000-0001-000000000001', 35, NULL),    -- Mobiadm: State of SP
('00000000-0000-0000-0001-000000000002', 35, NULL),    -- admin: State of SP
('00000000-0000-0000-0001-000000000003', 35, NULL),    -- emferreira: State of SP
('00000000-0000-0000-0002-000000000001', NULL, 3550308), -- gtpadm: São Paulo
('00000000-0000-0000-0002-000000000002', NULL, 3550308); -- ana.julia: São Paulo

