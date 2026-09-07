package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.veiculo.*;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.repository.ibge.MunicipioRepository;
import neo.com.br.CitMobi.repository.veiculo.*;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.veiculo.VeiculoRecord;
import neo.com.br.CitMobi.models.records.veiculo.RouteBlockRecord;
import neo.com.br.CitMobi.models.records.veiculo.DriverBlockRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VeiculoService {

    private static final Logger logger = LoggerFactory.getLogger(VeiculoService.class);

    private final VeiculoRepository veiculoRepository;
    private final VeiculoModeloRepository veiculoModeloRepository;
    private final GaragemRepository garagemRepository;
    private final LinhaPlacaRepository linhaPlacaRepository;
    private final MotoristaPlacaRepository motoristaPlacaRepository;
    private final OperadorRepository operadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final LinhaRepository linhaRepository;
    private final MunicipioRepository municipioRepository;

    public VeiculoService(VeiculoRepository veiculoRepository,
                          VeiculoModeloRepository veiculoModeloRepository,
                          GaragemRepository garagemRepository,
                          LinhaPlacaRepository linhaPlacaRepository,
                          MotoristaPlacaRepository motoristaPlacaRepository,
                          OperadorRepository operadorRepository,
                          UsuarioRepository usuarioRepository,
                          LinhaRepository linhaRepository,
                          MunicipioRepository municipioRepository) {
        this.veiculoRepository = veiculoRepository;
        this.veiculoModeloRepository = veiculoModeloRepository;
        this.garagemRepository = garagemRepository;
        this.linhaPlacaRepository = linhaPlacaRepository;
        this.motoristaPlacaRepository = motoristaPlacaRepository;
        this.operadorRepository = operadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.linhaRepository = linhaRepository;
        this.municipioRepository = municipioRepository;
    }

    private static final Map<String, String> DB_TO_UI_DAYS = Map.of(
            "SEGUNDA", "SEG",
            "TERCA", "TER",
            "QUARTA", "QUA",
            "QUINTA", "QUI",
            "SEXTA", "SEX",
            "SABADO", "SAB",
            "DOMINGO", "DOM"
    );

    private static final Map<String, String> UI_TO_DB_DAYS = Map.of(
            "SEG", "SEGUNDA",
            "TER", "TERCA",
            "QUA", "QUARTA",
            "QUI", "QUINTA",
            "SEX", "SEXTA",
            "SAB", "SABADO",
            "DOM", "DOMINGO"
    );

    private String[] parseBrandAndModel(String fullModel) {
        if (fullModel == null || fullModel.trim().isEmpty()) {
            return new String[]{"CAIO", "DESCONHECIDO"};
        }

        String trimmed = fullModel.trim();
        String upper = trimmed.toUpperCase();

        if (upper.startsWith("CAIO ")) {
            return new String[]{"CAIO", trimmed.substring(5).trim()};
        } else if (upper.startsWith("MARCOPOLO ")) {
            return new String[]{"MARCOPOLO", trimmed.substring(10).trim()};
        } else if (upper.startsWith("MASCARELLO ")) {
            return new String[]{"MASCARELLO", trimmed.substring(11).trim()};
        } else if (upper.startsWith("NEOBUS ")) {
            return new String[]{"NEOBUS", trimmed.substring(7).trim()};
        } else if (upper.contains("APACHE") || upper.contains("MILLENNIUM") || upper.contains("MILLENIUM")) {
            return new String[]{"CAIO", trimmed};
        }

        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace > 0) {
            return new String[]{
                    trimmed.substring(0, firstSpace).toUpperCase(),
                    trimmed.substring(firstSpace + 1).trim()
            };
        }

        return new String[]{"CAIO", trimmed};
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GenericResponse<List<VeiculoRecord>>> getAllVeiculos() {
        try {
            List<Veiculo> veiculos = veiculoRepository.findAll();
            List<VeiculoRecord> records = veiculos.stream().map(this::mapToRecord).collect(Collectors.toList());
            return ResponseEntity.ok(new GenericResponse<>("200", "Buses retrieved successfully", records));
        } catch (Exception e) {
            logger.error("Error fetching vehicles: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Error fetching vehicles: " + e.getMessage(), null));
        }
    }

    public VeiculoRecord mapToRecord(Veiculo v) {
        List<LinhaPlaca> lpList = linhaPlacaRepository.findByVeiculo_Placa(v.getPlaca());
        Map<String, List<String>> routesGrouped = new HashMap<>();
        for (LinhaPlaca lp : lpList) {
            String routeName = lp.getLinha() != null
                    ? "Linha " + lp.getLinha().getCodigoLinha() + " - " + lp.getLinha().getAtendimento()
                    : "Linha Indefinida";
            String startTime = lp.getHoraInicio() != null ? lp.getHoraInicio().toString().substring(0, 5) : "00:00";
            String endTime = lp.getHoraFim() != null ? lp.getHoraFim().toString().substring(0, 5) : "23:59";
            String key = routeName + "|" + startTime + "|" + endTime;
            String uiDay = DB_TO_UI_DAYS.getOrDefault(lp.getDiaSemana(), "SEG");
            routesGrouped.computeIfAbsent(key, k -> new ArrayList<>()).add(uiDay);
        }

        List<RouteBlockRecord> routes = routesGrouped.entrySet().stream().map(entry -> {
            String[] parts = entry.getKey().split("\\|");
            return new RouteBlockRecord(parts[0], parts[1], parts[2], entry.getValue());
        }).collect(Collectors.toList());

        List<MotoristaPlaca> mpList = motoristaPlacaRepository.findByVeiculo_Placa(v.getPlaca());
        Map<String, List<String>> driversGrouped = new HashMap<>();
        for (MotoristaPlaca mp : mpList) {
            String name = mp.getMotorista() != null ? mp.getMotorista().getNome() : "Desconhecido";
            String startTime = mp.getHoraInicio() != null ? mp.getHoraInicio().toString().substring(0, 5) : "00:00";
            String endTime = mp.getHoraFim() != null ? mp.getHoraFim().toString().substring(0, 5) : "23:59";
            String key = name + "|" + startTime + "|" + endTime;
            String uiDay = DB_TO_UI_DAYS.getOrDefault(mp.getDiaSemana(), "SEG");
            driversGrouped.computeIfAbsent(key, k -> new ArrayList<>()).add(uiDay);
        }

        List<DriverBlockRecord> drivers = driversGrouped.entrySet().stream().map(entry -> {
            String[] parts = entry.getKey().split("\\|");
            return new DriverBlockRecord(parts[0], parts[1], parts[2], entry.getValue());
        }).collect(Collectors.toList());

        String status = v.getStatus() != null ? v.getStatus().name() : "ATIVO";

        return new VeiculoRecord(
                v.getCodigoVeiculo() != null ? v.getCodigoVeiculo() : String.valueOf(v.getId()),
                v.getPlaca(),
                v.getVeiculoModelo() != null ? v.getVeiculoModelo().getModelo() : "Padrao",
                v.getVeiculoModelo() != null ? v.getVeiculoModelo().getTipo() : "Urbano",
                v.getCapacidade(),
                status,
                v.getGaragem() != null ? v.getGaragem().getDescricao() : "Sem Garagem",
                routes,
                drivers
        );
    }

    @Transactional
    public ResponseEntity<GenericResponse<VeiculoRecord>> createVeiculo(VeiculoRecord record) {
        try {
            String[] parsed = parseBrandAndModel(record.model());
            String brand = parsed[0];
            String modelName = parsed[1];

            List<VeiculoModelo> models = veiculoModeloRepository.findAll();
            VeiculoModelo model = models.stream()
                    .filter(m -> m.getModelo().equalsIgnoreCase(modelName) && m.getTipo().equalsIgnoreCase(record.type()))
                    .findFirst()
                    .orElseGet(() -> veiculoModeloRepository.save(new VeiculoModelo(brand, modelName, 2, record.type() != null ? record.type() : "Urbano")));

            Operador op = operadorRepository.findAll().stream().findFirst().orElse(null);
            Municipio muni = municipioRepository.findById(3550308L).orElseGet(() -> municipioRepository.findAll().stream().findFirst().orElse(null));

            List<Garagem> garages = garagemRepository.findAll();
            Garagem garage = garages.stream()
                    .filter(g -> g.getDescricao().equalsIgnoreCase(record.garage()))
                    .findFirst()
                    .orElseGet(() -> garagemRepository.save(new Garagem(
                            record.garage() != null ? record.garage() : "Garagem Central",
                            op,
                            muni,
                            "Logradouro Padrao",
                            "S/N",
                            "01001000"
                    )));

            VeiculoStatus vStatus = parseStatus(record.status());

            Veiculo v = new Veiculo(
                    record.plate().trim().toUpperCase(),
                    record.id() != null ? record.id().trim() : record.plate().trim().toUpperCase(),
                    op,
                    model,
                    record.capacity() != null ? record.capacity() : 80,
                    "2020",
                    garage
            );
            v.setStatus(vStatus);
            v = veiculoRepository.save(v);

            saveSchedules(v, record.routes(), record.drivers());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new GenericResponse<>("201", "Bus registered successfully", mapToRecord(v)));
        } catch (Exception e) {
            logger.error("Error registering vehicle: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Error registering vehicle: " + e.getMessage(), null));
        }
    }

    private VeiculoStatus parseStatus(String statusStr) {
        if (statusStr == null) return VeiculoStatus.ATIVO;
        String clean = statusStr.trim().toUpperCase().replace(" ", "_");
        if ("EM_MANUTENCAO".equals(clean) || "MANUTENCAO".equals(clean)) {
            return VeiculoStatus.MANUTENCAO;
        }
        if ("INATIVO".equals(clean)) {
            return VeiculoStatus.INATIVO;
        }
        if ("SUCATEADO".equals(clean)) {
            return VeiculoStatus.SUCATEADO;
        }
        if ("VENDIDO".equals(clean)) {
            return VeiculoStatus.VENDIDO;
        }
        try {
            return VeiculoStatus.valueOf(clean);
        } catch (IllegalArgumentException ignored) {
            return VeiculoStatus.ATIVO;
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<VeiculoRecord>> updateVeiculo(String plate, VeiculoRecord record) {
        try {
            Optional<Veiculo> optionalV = veiculoRepository.findByPlaca(plate);
            if (optionalV.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>("404", "Vehicle not found with plate: " + plate, null));
            }

            Veiculo v = optionalV.get();
            if (record.id() != null) {
                v.setCodigoVeiculo(record.id());
            }
            if (record.capacity() != null) {
                v.setCapacidade(record.capacity());
            }
            if (record.status() != null) {
                v.setStatus(parseStatus(record.status()));
            }

            if (record.model() != null) {
                String[] parsed = parseBrandAndModel(record.model());
                String brand = parsed[0];
                String modelName = parsed[1];

                List<VeiculoModelo> models = veiculoModeloRepository.findAll();
                VeiculoModelo model = models.stream()
                        .filter(m -> m.getModelo().equalsIgnoreCase(modelName) && (record.type() == null || m.getTipo().equalsIgnoreCase(record.type())))
                        .findFirst()
                        .orElseGet(() -> veiculoModeloRepository.save(new VeiculoModelo(brand, modelName, 2, record.type() != null ? record.type() : "Urbano")));
                v.setVeiculoModelo(model);
            }

            if (record.garage() != null && (v.getGaragem() == null || !v.getGaragem().getDescricao().equalsIgnoreCase(record.garage()))) {
                List<Garagem> garages = garagemRepository.findAll();
                Municipio muni = municipioRepository.findById(3550308L).orElseGet(() -> municipioRepository.findAll().stream().findFirst().orElse(null));
                Operador op = v.getOperador();
                Garagem garage = garages.stream()
                        .filter(g -> g.getDescricao().equalsIgnoreCase(record.garage()))
                        .findFirst()
                        .orElseGet(() -> garagemRepository.save(new Garagem(record.garage(), op, muni, "Logradouro Padrao", "S/N", "01001000")));
                v.setGaragem(garage);
            }

            Veiculo savedVeiculo = veiculoRepository.save(v);

            linhaPlacaRepository.deleteAll(linhaPlacaRepository.findByVeiculo_Placa(savedVeiculo.getPlaca()));
            motoristaPlacaRepository.deleteAll(motoristaPlacaRepository.findByVeiculo_Placa(savedVeiculo.getPlaca()));

            saveSchedules(savedVeiculo, record.routes(), record.drivers());

            return ResponseEntity.ok(new GenericResponse<>("200", "Vehicle updated successfully", mapToRecord(savedVeiculo)));
        } catch (Exception e) {
            logger.error("Error updating vehicle: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Error updating vehicle: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<Void>> deleteVeiculo(String plate) {
        try {
            Optional<Veiculo> optionalV = veiculoRepository.findByPlaca(plate);
            if (optionalV.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>("404", "Vehicle not found with plate: " + plate, null));
            }

            linhaPlacaRepository.deleteAll(linhaPlacaRepository.findByVeiculo_Placa(plate));
            motoristaPlacaRepository.deleteAll(motoristaPlacaRepository.findByVeiculo_Placa(plate));
            veiculoRepository.delete(optionalV.get());

            return ResponseEntity.ok(new GenericResponse<>("200", "Vehicle deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting vehicle: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Error deleting vehicle: " + e.getMessage(), null));
        }
    }

    private void saveSchedules(Veiculo v, List<RouteBlockRecord> routes, List<DriverBlockRecord> drivers) {
        if (routes != null) {
            for (RouteBlockRecord r : routes) {
                String lineId = "3301";
                String atendimento = "10";
                if (r.routeName().contains("Linha") && r.routeName().contains("-")) {
                    String[] routeParts = r.routeName().replace("Linha", "").trim().split("-");
                    lineId = routeParts[0].trim();
                    atendimento = routeParts[1].trim();
                } else if (r.routeName().contains("-")) {
                    String[] routeParts = r.routeName().split("-");
                    lineId = routeParts[0].trim();
                    atendimento = routeParts[1].trim();
                } else {
                    lineId = r.routeName().trim();
                }

                String finalLineId = lineId;
                String finalAtendimento = atendimento;
                Linha linha = linhaRepository.findAll().stream()
                        .filter(l -> l.getCodigoLinha().equalsIgnoreCase(finalLineId) && l.getAtendimento().equalsIgnoreCase(finalAtendimento))
                        .findFirst()
                        .orElseGet(() -> linhaRepository.findAll().stream().findFirst().orElse(null));

                if (linha != null) {
                    LocalTime start = r.startTime() != null ? LocalTime.parse(r.startTime().length() == 5 ? r.startTime() + ":00" : r.startTime()) : LocalTime.of(6, 0);
                    LocalTime end = r.endTime() != null ? LocalTime.parse(r.endTime().length() == 5 ? r.endTime() + ":00" : r.endTime()) : LocalTime.of(22, 0);
                    for (String day : r.days()) {
                        String dbDay = UI_TO_DB_DAYS.getOrDefault(day.toUpperCase(), "SEGUNDA");
                        LinhaPlaca lp = new LinhaPlaca(linha, v, dbDay, start, end);
                        linhaPlacaRepository.save(lp);
                    }
                }
            }
        }

        if (drivers != null) {
            for (DriverBlockRecord d : drivers) {
                List<Usuario> users = usuarioRepository.findAll();
                Usuario driver = users.stream()
                        .filter(u -> u.getNome() != null && u.getNome().equalsIgnoreCase(d.name()))
                        .findFirst()
                        .orElse(null);

                if (driver != null) {
                    LocalTime start = d.startTime() != null ? LocalTime.parse(d.startTime().length() == 5 ? d.startTime() + ":00" : d.startTime()) : LocalTime.of(6, 0);
                    LocalTime end = d.endTime() != null ? LocalTime.parse(d.endTime().length() == 5 ? d.endTime() + ":00" : d.endTime()) : LocalTime.of(14, 0);
                    for (String day : d.days()) {
                        String dbDay = UI_TO_DB_DAYS.getOrDefault(day.toUpperCase(), "SEGUNDA");
                        MotoristaPlaca mp = new MotoristaPlaca(driver, v, dbDay, start, end);
                        motoristaPlacaRepository.save(mp);
                    }
                }
            }
        }
    }
}
