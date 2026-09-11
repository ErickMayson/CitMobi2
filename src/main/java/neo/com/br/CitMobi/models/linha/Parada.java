package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "T_LIN_PARADA")
@AllArgsConstructor
@NoArgsConstructor
public class Parada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIN_PARADA_ID", nullable = false)
    private Long paradaId;

    public Long getId() {
        return paradaId;
    }

    @Column(name = "LIN_PARADA_LOGRADOURO", nullable = false, length = 255)
    private String logradouro;

    @Column(name = "LIN_PARADA_NUMERO", nullable = false, length = 255)
    private String numero;

    @Column(name = "LIN_PARADA_OBS", nullable = false, length = 255)
    private String obs;

    @Column(name = "LIN_PARADA_LATITUDE", nullable = false)
    private BigDecimal latitude;

    @Column(name = "LIN_PARADA_LONGITUDE", nullable = false)
    private BigDecimal longitude;

    @Column(name = "GLB_MUNICIPIO_COD")
    private Long municipio;

    @Column(name = "GLB_UF_SIGLA", nullable = false)
    private String uf;

    @Column(name = "LIN_TIPO_ID", nullable = false)
    private Long tipo;

    @Column(name = "LIN_PARADA_FLAGATIVA", nullable = false)
    private String ativa;


    public ParadaRecord toRecord() {
        return new ParadaRecord(
                paradaId,
                logradouro,
                numero,
                obs,
                toLatLong(latitude, longitude),
                municipio,
                uf,
                tipo,
                ativa
        );
    }

    public Parada(String logradouro, String numero, String obs, BigDecimal longitude, BigDecimal latitude, Long municipio, String uf, Long tipo) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.obs = obs;
        this.longitude = longitude;
        this.latitude = latitude;
        this.municipio = municipio;
        this.uf = uf;
        this.tipo = tipo;
    }

    public Parada(Long paradaId, String logradouro, String numero, String obs, List<BigDecimal> latLong, Long municipio, String uf, Long tipo, String flagAtiva) {
        this.paradaId = paradaId;
        this.logradouro = logradouro;
        this.numero = numero;
        this.obs = obs;
        this.latitude = latLong.get(0);
        this.longitude = latLong.get(1);
        this.municipio = municipio;
        this.uf = uf;
        this.tipo = tipo;
        this.ativa = flagAtiva;
    }

    public List<BigDecimal> toLatLong(BigDecimal latitude, BigDecimal longitude) {
        return List.of(latitude, longitude);
    }
}

