package neo.com.br.CitMobi.models.records.linha;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.linha.RotaId;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RotaRecord(
        Itinerario itinerario,
        List<Parada> paradas,
        Long sequencia
) {

    public List<Rota> toRotaList() {
        return paradas.stream()
                .map(parada -> {
                    RotaId rotaId = new RotaId(itinerario, parada, sequencia);
                    Rota rota = new Rota();
                    rota.setRotaId(rotaId);
                    return rota;
                })
                .collect(Collectors.toList());
    }


// Provavelmente é um metodo muito inteligente, mas não quero usar :)
//    public Map<String, Object> toJson() {
//        return Map.of(
//                "data", List.of(Map.of(
//                        "itinerario", Map.of(
//                                "itinerarioId", itinerario.getItinerarioId(),
//                                "linhaId", itinerario.getLinhaId(),
//                                "linhaAtendimento", itinerario.getLinhaAtendimento(),
//                                "prefixo", itinerario.getPrefixo(),
//                                "municipio", itinerario.getMunicipio(),
//                                "linhaSentido", itinerario.getLinhaSentido(),
//                                "rota", paradas.stream().map(parada -> Map.of(
//                                        "paradaId", parada.getLinParadaId(),
//                                        "logradouro", parada.getLogradouro(),
//                                        "numero", parada.getNumero(),
//                                        "longitude", parada.getLongitude(),
//                                        "latitude", parada.getLatitude(),
//                                        "municipio", parada.getMunicipio(),
//                                        "ufSigla", parada.getUf().getSigla(),
//                                        "tipoId", parada.getTipo().getTipoId()
//                                )).collect(Collectors.toList())
//                        )
//                ))
//        );
//    }
}
