package neo.com.br.CitMobi.models.ibge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.records.ibge.SubdistritoRecord;

@Data
@Entity(name = "T_GLB_SUBDISTRITO")
@Table(name = "T_GLB_SUBDISTRITO", schema = "ibge")
@NoArgsConstructor
public class Subdistrito {

    @Id
    @Column(name = "GLB_SUBDISTRITO_COD")
    private Long codIbge;

    @Column(name = "GLB_SUBDISTRITO_NOME", length = 150)
    private String nome;

    @Column(name = "GLB_DISTRITO_COD")
    private Long codDistrito;

    public Subdistrito(SubdistritoRecord record) {
        this.codIbge = record.id();
        this.nome = record.nome();
        this.codDistrito = record.distrito().id();
    }

    public static Subdistrito fromRecord(SubdistritoRecord record) {
        Subdistrito subdistrito = new Subdistrito();
        subdistrito.setCodIbge(record.id());
        subdistrito.setNome(record.nome());
        subdistrito.setCodDistrito(record.distrito().id());
        return subdistrito;
    }

}
