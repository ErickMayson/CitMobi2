package neo.com.br.CitMobi.models.ibge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.records.ibge.MunicipioRecord;

@Entity
@Table(name = "T_GLB_MUNICIPIO", schema = "ibge")
@Data
@NoArgsConstructor
public class Municipio {

    @Id
    @Column(name = "GLB_MUNICIPIO_COD")
    private Long codIbge;

    @Column(name = "GLB_MUNICIPIO_NOME", length = 150)
    private String nome;

    @Column(name = "GLB_UF_SIGLA", length = 2)
    private String uf;

    @Column(name = "GLB_REGIAOIMEDIATA_COD")
    private Long codRegiaoImediata;

    public Municipio(MunicipioRecord record) {
        this.codIbge = record.id();
        this.nome = record.nome();
        this.uf = record.regiaoImediata().regiaoIntermediaria().UF().sigla();
        this.codRegiaoImediata = record.regiaoImediata().id();
    }

    public static Municipio fromRecord(MunicipioRecord record) {
        Municipio municipio = new Municipio();
        municipio.setCodIbge(record.id());
        municipio.setNome(record.nome());
        municipio.setUf(record.regiaoImediata().regiaoIntermediaria().UF().sigla());
        return municipio;
    }

    public Long getCodigoIbge() {
        return codIbge;
    }

    public void setCodigoIbge(Long codIbge) {
        this.codIbge = codIbge;
    }
}
