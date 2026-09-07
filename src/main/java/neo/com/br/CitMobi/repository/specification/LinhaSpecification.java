package neo.com.br.CitMobi.repository.specification;

import jakarta.persistence.criteria.Predicate;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.usuario.UsuarioAcesso;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LinhaSpecification {

    public static Specification<Linha> filterByAccess(List<UsuarioAcesso> acessos) {
        return (root, query, criteriaBuilder) -> {
            if (acessos == null || acessos.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for (UsuarioAcesso acesso : acessos) {
                if (acesso.getMunicipio() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("municipio").get("codIbge"), acesso.getMunicipio().getCodIbge()));
                }
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
