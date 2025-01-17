package neo.com.br.CitMobi.models.records.glb;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;

public record OperadorRecord(
        String cnpj,
        String razao
) {

    public Operador toOperador() {
        return new Operador(cnpj, razao);
    }

}
