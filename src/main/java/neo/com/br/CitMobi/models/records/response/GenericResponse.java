package neo.com.br.CitMobi.models.records.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenericResponse<T>(
        String status,
        String message,
        T data
) {
}

