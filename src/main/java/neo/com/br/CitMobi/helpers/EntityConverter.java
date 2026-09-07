package neo.com.br.CitMobi.helpers;

import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Terminal;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.linha.TerminalRecord;
import org.springframework.stereotype.Component;

@Component
public class EntityConverter {
    public TerminalRecord terminalRecordCreator(Terminal terminal) {
        return new TerminalRecord(
                terminal.getNome(),
                terminal.getLogradouro(),
                terminal.getNumero(),
                terminal.getLongitude() != null ? terminal.getLongitude().toString() : null,
                terminal.getLatitude() != null ? terminal.getLatitude().toString() : null,
                terminal.getMunicipio() != null ? terminal.getMunicipio().getCodigoIbge().toString() : null,
                terminal.getUf() != null ? terminal.getUf().getSigla() : null,
                terminal.getTipo() != null ? terminal.getTipo().getTipoId().toString() : null
        );
    }

}
