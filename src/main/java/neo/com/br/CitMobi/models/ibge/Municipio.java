package neo.com.br.CitMobi.models.ibge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.records.ibge.DistritoRecord;

@Entity
@Table(name = "T_GLB_MUNICIPIO")
@Data
@NoArgsConstructor
public class Municipio {

    @Id
    @Column(name = "GLB_MUNICIPIO_COD")
    private Long codIbge;

    @Column(name = "GLB_MUNICIPIO_NOME", precision = 150)
    private String nome;

    @Column(name = "GLB_UF_SIGLA", precision = 4)
    private String uf;

    // Construtor que recebe um UFRecord
        public Municipio(DistritoRecord record) {
        this.codIbge = record.municipio().id();
        this.nome = record.municipio().nome();
        this.uf = record.municipio().microrregiao().mesorregiao().UF().sigla();
    }

    // Método de conversão
    public static UF fromRecord(DistritoRecord record) {
        UF uf = new UF();
        uf.setCodIbge(record.municipio().id());
        uf.setNome(record.municipio().nome());
        uf.setSigla(record.municipio().microrregiao().mesorregiao().UF().sigla());
        return uf;
    }

}
