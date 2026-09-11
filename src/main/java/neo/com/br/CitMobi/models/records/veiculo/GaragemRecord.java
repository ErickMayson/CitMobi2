package neo.com.br.CitMobi.models.records.veiculo;

import neo.com.br.CitMobi.models.veiculo.Garagem;

public record GaragemRecord(
        Long id,
        String descricao,
        Long operadorId,
        String operadorNome,
        Long municipioCod,
        String logradouro,
        String numero,
        String cep
) {

    public static GaragemRecord fromEntity(Garagem garagem) {
        if (garagem == null) return null;
        return new GaragemRecord(
                garagem.getId(),
                garagem.getDescricao(),
                garagem.getOperador() != null ? garagem.getOperador().getId() : null,
                garagem.getOperador() != null ? garagem.getOperador().getRazaoSocial() : null,
                garagem.getMunicipio() != null ? garagem.getMunicipio().getCodIbge() : null,
                garagem.getLogradouro(),
                garagem.getNumero(),
                garagem.getCep()
        );
    }
}
