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

    public ParadaRecord toRecord(Parada parada) {
        return new ParadaRecord(
                parada.getParadaId().getLinParadaId().toString(),
                parada.getLogradouro(),
                parada.getNumero(),
                parada.getLongitude().toString(),
                parada.getLatitude().toString(),
                parada.getParadaId().toString(),
                parada.getUf().getSigla(),
                parada.getTipo().getTipoId().toString()
        );
    }

}
