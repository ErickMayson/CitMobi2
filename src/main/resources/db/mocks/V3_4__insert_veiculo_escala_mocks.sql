-- Operational Vehicle Run & Driver Slot Seeds (T_VEI_ESCALA)
-- Vehicle 1 (ABC-1234) on Linha 1 (3301)
-- Morning Shift: João Silva (00000000-0000-0000-0003-000000000002) (06:00 - 14:00) SEG-SEX
-- Afternoon Shift: Ana Costa (00000000-0000-0000-0003-000000000005) (14:00 - 22:00) SEG-SEX
INSERT INTO T_VEI_ESCALA (VEI_VEICULO_ID, LIN_LINHA_ID, USU_USUARIO_ID, VEI_ESCALA_DIASEMANA, VEI_ESCALA_HORAINICIO, VEI_ESCALA_HORAFIM)
VALUES 
(1, 1, '00000000-0000-0000-0003-000000000002', 'SEGUNDA', '06:00:00', '14:00:00'),
(1, 1, '00000000-0000-0000-0003-000000000002', 'TERCA', '06:00:00', '14:00:00'),
(1, 1, '00000000-0000-0000-0003-000000000002', 'QUARTA', '06:00:00', '14:00:00'),
(1, 1, '00000000-0000-0000-0003-000000000002', 'QUINTA', '06:00:00', '14:00:00'),
(1, 1, '00000000-0000-0000-0003-000000000002', 'SEXTA', '06:00:00', '14:00:00'),
(1, 1, '00000000-0000-0000-0003-000000000005', 'SEGUNDA', '14:00:00', '22:00:00'),
(1, 1, '00000000-0000-0000-0003-000000000005', 'TERCA', '14:00:00', '22:00:00'),
(1, 1, '00000000-0000-0000-0003-000000000005', 'QUARTA', '14:00:00', '22:00:00'),
(1, 1, '00000000-0000-0000-0003-000000000005', 'QUINTA', '14:00:00', '22:00:00'),
(1, 1, '00000000-0000-0000-0003-000000000005', 'SEXTA', '14:00:00', '22:00:00');

-- Vehicle 2 (DEF-5678) on Linha 3 (9051)
-- Morning Shift: Maria Santos (00000000-0000-0000-0003-000000000003) (05:30 - 13:30) SEG-SAB
-- Afternoon Shift: Vacant slot (NULL) (13:30 - 21:30) SEG-SAB
INSERT INTO T_VEI_ESCALA (VEI_VEICULO_ID, LIN_LINHA_ID, USU_USUARIO_ID, VEI_ESCALA_DIASEMANA, VEI_ESCALA_HORAINICIO, VEI_ESCALA_HORAFIM)
VALUES 
(2, 3, '00000000-0000-0000-0003-000000000003', 'SEGUNDA', '05:30:00', '13:30:00'),
(2, 3, '00000000-0000-0000-0003-000000000003', 'TERCA', '05:30:00', '13:30:00'),
(2, 3, '00000000-0000-0000-0003-000000000003', 'QUARTA', '05:30:00', '13:30:00'),
(2, 3, '00000000-0000-0000-0003-000000000003', 'QUINTA', '05:30:00', '13:30:00'),
(2, 3, '00000000-0000-0000-0003-000000000003', 'SEXTA', '05:30:00', '13:30:00'),
(2, 3, '00000000-0000-0000-0003-000000000003', 'SABADO', '05:30:00', '13:30:00'),
(2, 3, NULL, 'SEGUNDA', '13:30:00', '21:30:00'),
(2, 3, NULL, 'TERCA', '13:30:00', '21:30:00'),
(2, 3, NULL, 'QUARTA', '13:30:00', '21:30:00'),
(2, 3, NULL, 'QUINTA', '13:30:00', '21:30:00'),
(2, 3, NULL, 'SEXTA', '13:30:00', '21:30:00'),
(2, 3, NULL, 'SABADO', '13:30:00', '21:30:00');

-- Operational Trip Sessions Seed
-- Trip 1: Active run for driver João Silva on Line 3 / Rota 1 (Vehicle 1)
INSERT INTO T_USU_VIAGEM (
    USU_VIAGEM_ID,
    USU_USUARIO_ID,
    VEI_VEICULO_ID,
    LIN_LINHA_ID,
    LIN_ROTA_ID,
    USU_VIAGEM_DTINICIO,
    USU_VIAGEM_STATUS
) VALUES (
    1,
    '00000000-0000-0000-0003-000000000002', -- João Silva
    1, -- Vehicle ABC-1234
    3, -- Linha 9051
    1, -- Rota 1 (IDA)
    now() - interval '25 minutes',
    'EM_ANDAMENTO'
);

-- Trip 2: Active run for driver Maria Santos on Line 1 / Rota 2 (Vehicle 2)
INSERT INTO T_USU_VIAGEM (
    USU_VIAGEM_ID,
    USU_USUARIO_ID,
    VEI_VEICULO_ID,
    LIN_LINHA_ID,
    LIN_ROTA_ID,
    USU_VIAGEM_DTINICIO,
    USU_VIAGEM_STATUS
) VALUES (
    2,
    '00000000-0000-0000-0003-000000000003', -- Maria Santos
    2, -- Vehicle DEF-5678
    1, -- Linha 3301
    2, -- Rota 2 (VOLTA)
    now() - interval '10 minutes',
    'EM_ANDAMENTO'
);

-- Trip 3: Completed run for driver Antonio Ferreira (Vehicle 3)
INSERT INTO T_USU_VIAGEM (
    USU_VIAGEM_ID,
    USU_USUARIO_ID,
    VEI_VEICULO_ID,
    LIN_LINHA_ID,
    LIN_ROTA_ID,
    USU_VIAGEM_DTINICIO,
    USU_VIAGEM_DTFIM,
    USU_VIAGEM_STATUS
) VALUES (
    3,
    '00000000-0000-0000-0003-000000000001', -- Antonio Ferreira
    3, -- Vehicle GHI-9012
    1, -- Linha 3301
    1, -- Rota 1 (IDA)
    now() - interval '3 hours',
    now() - interval '2 hours',
    'FINALIZADA'
);

SELECT setval(pg_get_serial_sequence('T_USU_VIAGEM', 'usu_viagem_id'), coalesce(max(usu_viagem_id), 1)) FROM T_USU_VIAGEM;

-- Real-time Telemetry Cache Seed (Vehicle 1 and Vehicle 2)
INSERT INTO T_VEI_TELEMETRIA (
    VEI_VEICULO_ID,
    USU_VIAGEM_ID,
    VEI_TELEMETRIA_LATITUDE,
    VEI_TELEMETRIA_LONGITUDE,
    VEI_TELEMETRIA_VELOCIDADE,
    VEI_TELEMETRIA_BEARING,
    VEI_TELEMETRIA_ODOMETER,
    VEI_TELEMETRIA_SEQPARADA,
    VEI_TELEMETRIA_STATUSPARADA,
    LIN_PARADA_ID,
    VEI_TELEMETRIA_DTUPDATE
) VALUES 
(
    1,
    1,
    -23.547132,
    -46.607014,
    11.80,
    88.50,
    3420.00,
    1,
    'IN_TRANSIT_TO',
    3,
    now()
),
(
    2,
    2,
    -23.551234,
    -46.634567,
    14.20,
    270.00,
    1850.00,
    2,
    'IN_TRANSIT_TO',
    1,
    now()
);

-- Historical Telemetry Breadcrumbs Seed
INSERT INTO T_VEI_TELEMETRIA_HISTORICO (
    USU_VIAGEM_ID,
    VEI_TELEMETRIA_LATITUDE,
    VEI_TELEMETRIA_LONGITUDE,
    VEI_TELEMETRIA_VELOCIDADE,
    VEI_TELEMETRIA_BEARING,
    VEI_TELEMETRIA_ODOMETER,
    VEI_TELEMETRIA_DTREGISTRO
) VALUES
-- Trip 1 breadcrumbs (Vehicle 1)
(1, -23.502900, -46.380309, 0.00, 0.00, 0.00, now() - interval '25 minutes'),
(1, -23.504232, -46.379010, 8.50, 45.00, 450.00, now() - interval '20 minutes'),
(1, -23.547132, -46.607014, 11.80, 88.50, 3420.00, now()),

-- Trip 2 breadcrumbs (Vehicle 2)
(2, -23.560000, -46.650000, 0.00, 0.00, 0.00, now() - interval '10 minutes'),
(2, -23.555000, -46.640000, 12.00, 260.00, 900.00, now() - interval '5 minutes'),
(2, -23.551234, -46.634567, 14.20, 270.00, 1850.00, now()),

-- Trip 3 breadcrumbs (Completed Trip 3)
(3, -23.541234, -46.643210, 0.00, 0.00, 0.00, now() - interval '3 hours'),
(3, -23.545000, -46.640000, 10.00, 120.00, 800.00, now() - interval '2 hours 40 minutes'),
(3, -23.550000, -46.630000, 12.50, 130.00, 2100.00, now() - interval '2 hours 20 minutes'),
(3, -23.558000, -46.620000, 0.00, 0.00, 3500.00, now() - interval '2 hours');


