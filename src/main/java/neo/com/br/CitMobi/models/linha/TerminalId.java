package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import lombok.Data;

@Embeddable
@Data
public class TerminalId {

    @Column(name = "LIN_TERMINAL_NOME", nullable = false, length = 255)
    private String nome; // Primary key
    @Column(name = "GLB_MUNICIPIO_COD", nullable = false)
    private Long municipioCod;
}
