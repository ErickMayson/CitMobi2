package neo.com.br.CitMobi.models.records.motorista;

import java.time.LocalDate;
import java.util.List;

public record MotoristaRecord(
        String id,
        String nome,
        String cpf,
        String cnhNumero,
        LocalDate cnhValidade,
        String login,
        String telefone,
        Long operadorId,
        String operadorNome,
        String status,
        List<MotoristaHorarioRecord> horarios
) {}

