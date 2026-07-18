package neo.com.br.CitMobi.models.ibge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.records.ibge.RegiaoRecord;

@Data
@Entity(name = "T_GLB_REGIAO")
@Table(name = "T_GLB_REGIAO", schema = "ibge")
@NoArgsConstructor
public class Regiao {

    @Id
    @Column(name = "GLB_REGIAO_COD")
    private Long codIbge;

    @Column(name = "GLB_REGIAO_NOME", length = 100)
    private String nome;

    @Column(name = "GLB_REGIAO_SIGLA", length = 4)
    private String sigla;

    public Regiao(RegiaoRecord record) {
        this.codIbge = record.id();
        this.nome = record.nome();
        this.sigla = record.sigla();
    }

    public static Regiao fromRecord(RegiaoRecord record) {
        Regiao regiao = new Regiao();
        regiao.setCodIbge(record.id());
        regiao.setNome(record.nome());
        regiao.setSigla(record.sigla());
        return regiao;
    }

}
