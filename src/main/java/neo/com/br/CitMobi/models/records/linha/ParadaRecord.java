package neo.com.br.CitMobi.models.records.linha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Parada;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record ParadaRecord(
        Long paradaId,
        @NotBlank(message = "Logradouro é um campo obrigatorio")
        String logradouro,
        @NotBlank(message = "numero é um campo obrigatorio")
        String numero,
        String obs,
        @NotNull(message = "Latitude e Longitude é obrigatorio")
        List<BigDecimal> latLong,
        @NotNull(message = "Municipio é obrigatório")
        Long municipio,
        @NotBlank(message = "UF é obrigatório")
        String ufSigla,
        @NotNull(message = "Tipo é obrigatório")
        Long tipoId,
        String flagAtiva
) {

    public Parada toParada() {
        isValidLatLong(latLong);
        return new Parada(
                paradaId,
                safeTrimAndUppercase(logradouro),
                safeTrimAndUppercase(numero),
                safeTrimAndUppercase(obs),
                latLong,
                municipio,
                ufSigla,
                tipoId,
                ativaIfNull(flagAtiva)
        );
    }

    private String ativaIfNull(String flagAtiva) {return flagAtiva != null ? flagAtiva : "S";}

    private String safeTrimAndUppercase(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private List<BigDecimal> toLatLong(BigDecimal latitude, BigDecimal longitude) {
        List<BigDecimal> latLong = new ArrayList<>();
        latLong.add(latitude);
        latLong.add(longitude);
        return latLong;
    }

    private BigDecimal latLongToBigDecimal(BigDecimal coordinate) {
        if (coordinate == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        return coordinate;
    }

    private void isValidLatLong(List<BigDecimal> latLong) {
        if (latLong == null || latLong.size() != 2) {
            throw new IllegalArgumentException("Invalid latitude/longitude list: Must contain exactly two values.");
        }

        BigDecimal latitude = latLong.get(0);
        BigDecimal longitude = latLong.get(1);

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and Longitude cannot be null.");
        }

        if (latitude.compareTo(BigDecimal.valueOf(-90)) < 0 || latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new IllegalArgumentException("Invalid latitude: Must be between -90 and 90.");
        }

        if (longitude.compareTo(BigDecimal.valueOf(-180)) < 0 || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new IllegalArgumentException("Invalid longitude: Must be between -180 and 180.");
        }
    }


}
