package neo.com.br.CitMobi.models.ibge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.records.ibge.RegiaoIntermediariaRecord;

@Data
@Entity(name = "T_GLB_REGIAOINTERMEDIARIA")
@Table(name = "T_GLB_REGIAOINTERMEDIARIA", schema = "ibge")
@NoArgsConstructor
public class RegiaoIntermediaria {

    @Id
    @Column(name = "GLB_REGIAOINTERMEDIARIA_COD")
    private Long codIbge;

    @Column(name = "GLB_REGIAOINTERMEDIARIA_NOME", length = 150)
    private String nome;

    @Column(name = "GLB_UF_COD")
    private Long codUf;

    public RegiaoIntermediaria(RegiaoIntermediariaRecord record) {
        this.codIbge = record.id();
        this.nome = record.nome();
        this.codUf = record.UF().id();
    }

    public static RegiaoIntermediaria fromRecord(RegiaoIntermediariaRecord record) {
        RegiaoIntermediaria regiaoIntermediaria = new RegiaoIntermediaria();
        regiaoIntermediaria.setCodIbge(record.id());
        regiaoIntermediaria.setNome(record.nome());
        regiaoIntermediaria.setCodUf(record.UF().id());
        return regiaoIntermediaria;
    }

}
