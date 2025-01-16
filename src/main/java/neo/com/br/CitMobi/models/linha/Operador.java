package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

// Criar uma unica linha e colocar itinerarios de ida e volta na tabela itinerario // Isso nem faz sentido
@Entity
@Table(name = "T_GLB_OPERADOR")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Operador {

    @Id
    @Column(name = "GLB_OPERADOR_CNPJ")
    private String cnpjOperador;

    @Column(name = "GLB_OPERADOR_RAZAOSOCIAL")
    private String razaoSocial;


}
