package neo.com.br.CitMobi.models.ibge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.records.ibge.UfRecord;

@Data
@Entity(name = "T_GLB_UF")
@Table(name = "T_GLB_UF", schema = "ibge")
@NoArgsConstructor
public class UF {

    @Id
    @Column(name = "GLB_UF_COD")
    private Long codIbge;

    @Column(name = "GLB_UF_NOME", length = 100)
    private String nome;

    @Column(name = "GLB_UF_SIGLA", length = 2)
    private String sigla;

    @Column(name = "GLB_REGIAO_COD")
    private Long codRegiao;

    public UF(UfRecord record) {
        this.codIbge = record.id();
        this.nome = record.nome();
        this.sigla = record.sigla();
        this.codRegiao = record.regiao().id();
    }

    public static UF fromRecord(UfRecord record) {
        UF uf = new UF();
        uf.setCodIbge(record.id());
        uf.setNome(record.nome());
        uf.setSigla(record.sigla());
        uf.setCodRegiao(record.regiao().id());
        return uf;
    }

}
