package neo.com.br.CitMobi.models.records.telemetria;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TelemetriaVeiculoRecord(
        Long veiculoId,
        String placa,
        String codigoVeiculo,
        Long operadorId,
        String operadorNome,
        Long viagemId,
        UUID motoristaId,
        String motoristaNome,
        Long linhaId,
        String linhaCodigo,
        String linhaDescricao,
        Long rotaId,
        String rotaPrefixo,
        String sentido,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal velocidade,
        BigDecimal bearing,
        BigDecimal odometer,
        Integer sequenciaParadaAtual,
        String statusParadaAtual,
        Long paradaAtualId,
        String paradaAtualLogradouro,
        OffsetDateTime ultimaAtualizacao
) {}
