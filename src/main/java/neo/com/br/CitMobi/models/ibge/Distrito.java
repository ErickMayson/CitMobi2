package neo.com.br.CitMobi.models.ibge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.records.ibge.DistritoRecord;

@Data
@Entity(name = "T_GLB_DISTRITO")
@Table(name = "T_GLB_DISTRITO", schema = "ibge")
@NoArgsConstructor
public class Distrito {

    @Id
    @Column(name = "GLB_DISTRITO_COD")
    private Long codIbge;

    @Column(name = "GLB_DISTRITO_NOME", length = 150)
    private String nome;

    @Column(name = "GLB_MUNICIPIO_COD")
    private Long codMunicipio;

    public Distrito(DistritoRecord record) {
        this.codIbge = record.id();
        this.nome = record.nome();
        this.codMunicipio = record.municipio().id();
    }

    public static Distrito fromRecord(DistritoRecord record) {
        Distrito distrito = new Distrito();
        distrito.setCodIbge(record.id());
        distrito.setNome(record.nome());
        distrito.setCodMunicipio(record.municipio().id());
        return distrito;
    }

}
