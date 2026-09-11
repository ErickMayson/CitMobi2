package neo.com.br.CitMobi.models.records.glb;

import neo.com.br.CitMobi.models.linha.Operador;

public record OperadorRecord(
        Long id,
        String cnpj,
        String razaoSocial,
        String flagRegulador
) {

    public OperadorRecord(String cnpj, String razaoSocial) {
        this(null, cnpj, razaoSocial, "N");
    }

    public static OperadorRecord fromEntity(Operador operador) {
        if (operador == null) return null;
        return new OperadorRecord(
                operador.getId(),
                operador.getCnpj(),
                operador.getRazaoSocial(),
                operador.getFlagRegulador()
        );
    }

    public Operador toOperador() {
        return new Operador(cnpj, razaoSocial, flagRegulador != null ? flagRegulador : "N");
    }
}
