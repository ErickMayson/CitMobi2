package neo.com.br.CitMobi.models.records.motorista;

import java.util.List;

public record MotoristaRecord(
        String id,
        String nome,
        String cpf,
        String login,
        String telefone,
        Long operadorId,
        String status,
        List<MotoristaHorarioRecord> horarios
) {}
