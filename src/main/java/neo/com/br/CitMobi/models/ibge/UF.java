package neo.com.br.CitMobi.models.ibge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.IbgeConsumer.models.record.UfRecord;

@Data
@Entity(name = "T_GLB_UF")
@NoArgsConstructor
public class UF {

    @Id
    @Column(name = "GLB_UF_COD")
    private Long codIbge;

    @Column(name = "GLB_UF_NOME", precision = 100)
    private String nome;

    @Column(name = "GLB_UF_SIGLA", precision = 4)
    private String sigla;

    // Construtor que recebe um UFRecord
    public UF(UfRecord record) {
        this.codIbge = record.id();
        this.nome = record.nome();
        this.sigla = record.sigla();
    }

    // Método de conversão
    public static UF fromRecord(UfRecord record) {
        UF uf = new UF();
        uf.setCodIbge(record.id());
        uf.setNome(record.nome());
        uf.setSigla(record.sigla());
        return uf;
    }

}
