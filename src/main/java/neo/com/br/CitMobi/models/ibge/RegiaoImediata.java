package neo.com.br.CitMobi.models.ibge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.records.ibge.RegiaoImediataRecord;

@Data
@Entity(name = "T_GLB_REGIAOIMEDIATA")
@Table(name = "T_GLB_REGIAOIMEDIATA", schema = "ibge")
@NoArgsConstructor
public class RegiaoImediata {

    @Id
    @Column(name = "GLB_REGIAOIMEDIATA_COD")
    private Long codIbge;

    @Column(name = "GLB_REGIAOIMEDIATA_NOME", length = 150)
    private String nome;

    @Column(name = "GLB_REGIAOINTERMEDIARIA_COD")
    private Long codRegiaoIntermediaria;

    public RegiaoImediata(RegiaoImediataRecord record) {
        this.codIbge = record.id();
        this.nome = record.nome();
        this.codRegiaoIntermediaria = record.regiaoIntermediaria().id();
    }

    public static RegiaoImediata fromRecord(RegiaoImediataRecord record) {
        RegiaoImediata regiaoImediata = new RegiaoImediata();
        regiaoImediata.setCodIbge(record.id());
        regiaoImediata.setNome(record.nome());
        regiaoImediata.setCodRegiaoIntermediaria(record.regiaoIntermediaria().id());
        return regiaoImediata;
    }

}
