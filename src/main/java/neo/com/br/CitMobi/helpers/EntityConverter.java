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
                terminal.getTerminalId().getNome(),
                terminal.getLogradouro(),
                terminal.getNumero(),
                terminal.getLongitude().toString(),
                terminal.getLatitude().toString(),
                terminal.getTerminalId().getMunicipioCod().toString(),
                terminal.getUf().getSigla(),
                terminal.getTipo().getTipoId().toString()
        );
    }

}
