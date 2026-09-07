package neo.com.br.CitMobi.models;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.usuario.*;
import neo.com.br.CitMobi.models.veiculo.*;
import neo.com.br.CitMobi.models.viagem.Viagem;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import neo.com.br.CitMobi.models.telemetria.Telemetria;
import neo.com.br.CitMobi.models.telemetria.TelemetriaHistorico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DomainEntitiesTest {

    @Test
    @DisplayName("Should instantiate Viagem with correct relations and status")
    void testViagemEntity() {
        Usuario motorista = new Usuario();
        motorista.setId(UUID.randomUUID());
        motorista.setNome("Motorista Teste");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("ABC1D23");

        Linha linha = new Linha();
        linha.setId(5L);

        Rota rota = new Rota();
        rota.setId(10L);

        Viagem viagem = new Viagem(motorista, veiculo, linha, rota);
        viagem.setId(100L);

        assertNotNull(viagem.getId());
        assertEquals(ViagemStatus.EM_ANDAMENTO, viagem.getStatus());
        assertEquals("Motorista Teste", viagem.getMotorista().getNome());
        assertEquals("ABC1D23", viagem.getVeiculo().getPlaca());

        viagem.setStatus(ViagemStatus.FINALIZADA);
        viagem.setDataFim(OffsetDateTime.now());
        assertEquals(ViagemStatus.FINALIZADA, viagem.getStatus());
        assertNotNull(viagem.getDataFim());
    }

    @Test
    @DisplayName("Should instantiate Telemetria and TelemetriaHistorico correctly")
    void testTelemetriaEntities() {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("ABC1D23");

        Viagem viagem = new Viagem();
        viagem.setId(100L);

        Telemetria telemetria = new Telemetria();
        telemetria.setVeiculo(veiculo);
        telemetria.setViagem(viagem);
        telemetria.setLatitude(BigDecimal.valueOf(-23.55052));
        telemetria.setLongitude(BigDecimal.valueOf(-46.633308));
        telemetria.setVelocidade(BigDecimal.valueOf(45.5));
        telemetria.setBearing(BigDecimal.valueOf(180.0));
        telemetria.setUltimaAtualizacao(OffsetDateTime.now());

        assertNotNull(telemetria.getVeiculo());
        assertEquals(BigDecimal.valueOf(45.5), telemetria.getVelocidade());

        TelemetriaHistorico historico = new TelemetriaHistorico();
        historico.setId(1L);
        historico.setViagem(viagem);
        historico.setLatitude(BigDecimal.valueOf(-23.55052));
        historico.setLongitude(BigDecimal.valueOf(-46.633308));
        historico.setVelocidade(BigDecimal.valueOf(45.5));
        historico.setBearing(BigDecimal.valueOf(180.0));
        historico.setDataRegistro(OffsetDateTime.now());

        assertNotNull(historico.getId());
        assertEquals(BigDecimal.valueOf(-23.55052), historico.getLatitude());
    }

    @Test
    @DisplayName("Should verify VeiculoStatus enum values")
    void testVeiculoStatusEnum() {
        assertEquals(VeiculoStatus.ATIVO, VeiculoStatus.valueOf("ATIVO"));
        assertEquals(VeiculoStatus.INATIVO, VeiculoStatus.valueOf("INATIVO"));
        assertEquals(VeiculoStatus.MANUTENCAO, VeiculoStatus.valueOf("MANUTENCAO"));
        assertEquals(VeiculoStatus.SUCATEADO, VeiculoStatus.valueOf("SUCATEADO"));
        assertEquals(VeiculoStatus.VENDIDO, VeiculoStatus.valueOf("VENDIDO"));
    }
}
