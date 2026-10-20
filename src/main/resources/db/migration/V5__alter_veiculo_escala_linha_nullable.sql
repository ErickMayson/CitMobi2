-- V5: Permitir que T_VEI_ESCALA tenha motorista alocado sem obrigatoriedade de linha (reserva/standby)
ALTER TABLE T_VEI_ESCALA ALTER COLUMN LIN_LINHA_ID DROP NOT NULL;
