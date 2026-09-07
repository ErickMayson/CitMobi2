package neo.com.br.CitMobi.repository.specification;

import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.ibge.UF;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioAcesso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LinhaSpecificationTest {

    @Test
    @DisplayName("Should build conjunction when access list is null or empty")
    void shouldBuildConjunctionWhenEmpty() {
        Specification<Linha> specNull = LinhaSpecification.filterByAccess(null);
        Specification<Linha> specEmpty = LinhaSpecification.filterByAccess(Collections.emptyList());

        assertNotNull(specNull);
        assertNotNull(specEmpty);
    }

    @Test
    @DisplayName("Should build specification with IBGE access predicates")
    void shouldBuildSpecificationWithAccessList() {
        Municipio municipio = new Municipio();
        municipio.setCodIbge(3550308L);

        UF uf = new UF();
        uf.setCodIbge(35L);
        uf.setSigla("SP");

        Usuario usuario = new Usuario();
        UsuarioAcesso acesso = new UsuarioAcesso(usuario, municipio, uf);

        Specification<Linha> spec = LinhaSpecification.filterByAccess(List.of(acesso));
        assertNotNull(spec);
    }
}
