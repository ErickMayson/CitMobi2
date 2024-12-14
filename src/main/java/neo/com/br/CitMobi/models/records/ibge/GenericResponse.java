package neo.com.br.CitMobi.models.records.ibge;

import java.util.List;

public record GenericResponse<T>(List<T> list) {
}