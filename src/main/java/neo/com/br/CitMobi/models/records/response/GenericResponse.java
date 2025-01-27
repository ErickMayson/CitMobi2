package neo.com.br.CitMobi.models.records.response;

public record GenericResponse<T>(
        String status,
        String message,
        T data
) {
}

