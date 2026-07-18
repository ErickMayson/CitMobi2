package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.veiculo.*;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.repository.veiculo.*;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.veiculo.VeiculoRecord;
import neo.com.br.CitMobi.models.records.veiculo.RouteBlockRecord;
import neo.com.br.CitMobi.models.records.veiculo.DriverBlockRecord;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final VeiculoModeloRepository veiculoModeloRepository;
    private final GaragemRepository garagemRepository;
    private final LinhaPlacaRepository linhaPlacaRepository;
    private final MotoristaPlacaRepository motoristaPlacaRepository;
    private final OperadorRepository operadorRepository;
    private final UsuarioRepository usuarioRepository;

    public VeiculoService(VeiculoRepository veiculoRepository,
                          VeiculoModeloRepository veiculoModeloRepository,
                          GaragemRepository garagemRepository,
                          LinhaPlacaRepository linhaPlacaRepository,
                          MotoristaPlacaRepository motoristaPlacaRepository,
                          OperadorRepository operadorRepository,
                          UsuarioRepository usuarioRepository) {
        this.veiculoRepository = veiculoRepository;
        this.veiculoModeloRepository = veiculoModeloRepository;
        this.garagemRepository = garagemRepository;
        this.linhaPlacaRepository = linhaPlacaRepository;
        this.motoristaPlacaRepository = motoristaPlacaRepository;
        this.operadorRepository = operadorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Helper mappings between DB days (SEGUNDA) and UI days (SEG)
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
        } else if (upper.contains("APACHE")) {
            return new String[]{"CAIO", trimmed};
        } else if (upper.contains("MILLENNIUM") || upper.contains("MILLENIUM")) {
            return new String[]{"CAIO", trimmed};
        }
        
        // Default fallback: split on first space if there is one
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Error fetching vehicles: " + e.getMessage(), null));
        }
    }

    private VeiculoRecord mapToRecord(Veiculo v) {
        // Aggregate routes
        List<LinhaPlaca> lpList = linhaPlacaRepository.findByLinhaPlacaIdVeiculoPlaca(v.getPlaca());
        Map<String, List<String>> routesGrouped = new HashMap<>(); // key: "routeName|startTime|endTime"
        for (LinhaPlaca lp : lpList) {
            String routeName = "Linha " + lp.getLinhaPlacaId().getLinhaId() + " - " + lp.getLinhaPlacaId().getLinhaAtendimento();
            String startTime = lp.getLinhaPlacaId().getHoraInicio() != null ? lp.getLinhaPlacaId().getHoraInicio().toString().substring(0, 5) : "00:00";
            String endTime = lp.getLinhaPlacaId().getHoraFim() != null ? lp.getLinhaPlacaId().getHoraFim().toString().substring(0, 5) : "23:59";
            String key = routeName + "|" + startTime + "|" + endTime;
            String uiDay = DB_TO_UI_DAYS.getOrDefault(lp.getLinhaPlacaId().getDiaSemana(), "SEG");
            routesGrouped.computeIfAbsent(key, k -> new ArrayList<>()).add(uiDay);
        }

        List<RouteBlockRecord> routes = routesGrouped.entrySet().stream().map(entry -> {
            String[] parts = entry.getKey().split("\\|");
            return new RouteBlockRecord(parts[0], parts[1], parts[2], entry.getValue());
        }).collect(Collectors.toList());

        // Aggregate drivers
        List<MotoristaPlaca> mpList = motoristaPlacaRepository.findByMotoristaPlacaIdVeiculoPlaca(v.getPlaca());
        Map<String, List<String>> driversGrouped = new HashMap<>(); // key: "name|startTime|endTime"
        for (MotoristaPlaca mp : mpList) {
            String name = mp.getMotoristaPlacaId().getUsuario().getNome();
            String startTime = mp.getMotoristaPlacaId().getHoraInicio() != null ? mp.getMotoristaPlacaId().getHoraInicio().toString().substring(0, 5) : "00:00";
            String endTime = mp.getMotoristaPlacaId().getHoraFim() != null ? mp.getMotoristaPlacaId().getHoraFim().toString().substring(0, 5) : "23:59";
            String key = name + "|" + startTime + "|" + endTime;
            String uiDay = DB_TO_UI_DAYS.getOrDefault(mp.getMotoristaPlacaId().getDiaSemana(), "SEG");
            driversGrouped.computeIfAbsent(key, k -> new ArrayList<>()).add(uiDay);
        }

        List<DriverBlockRecord> drivers = driversGrouped.entrySet().stream().map(entry -> {
            String[] parts = entry.getKey().split("\\|");
            return new DriverBlockRecord(parts[0], parts[1], parts[2], entry.getValue());
        }).collect(Collectors.toList());

        // Calculate status
        String status = "GARAGEM";
        if ("N".equals(v.getFlagAtivo())) {
            status = "INATIVO";
        } else if (!routes.isEmpty()) {
            status = "EM ATENDIMENTO";
        }

        return new VeiculoRecord(
                v.getVeiculoId(),
                v.getPlaca(),
                v.getVeiculoModelo().getModelo(),
                v.getVeiculoModelo().getTipo(),
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
            // Parse brand and model name
            String[] parsed = parseBrandAndModel(record.model());
            String brand = parsed[0];
            String modelName = parsed[1];

            List<VeiculoModelo> models = veiculoModeloRepository.findAll();
            VeiculoModelo model = models.stream()
                    .filter(m -> m.getModelo().equalsIgnoreCase(modelName) && m.getTipo().equalsIgnoreCase(record.type()))
                    .findFirst()
                    .orElseGet(() -> veiculoModeloRepository.save(new VeiculoModelo(brand, modelName, 2, record.type())));

            // Find or create garage
            List<Garagem> garages = garagemRepository.findAll();
            Garagem garage = garages.stream()
                    .filter(g -> g.getDescricao().equalsIgnoreCase(record.garage()))
                    .findFirst()
                    .orElseGet(() -> {
                        Operador op = operadorRepository.findAll().stream().findFirst().orElse(null);
                        return garagemRepository.save(new Garagem(record.garage(), op, 3550308L, "Logradouro Padrao", "S/N", "01001000"));
                    });

            Operador op = operadorRepository.findAll().stream().findFirst().orElse(null);

            Veiculo v = new Veiculo(
                    record.plate(),
                    record.id(),
                    op,
                    model,
                    record.capacity() != null ? record.capacity() : 80,
                    "2020",
                    garage
            );
            v.setFlagAtivo("INATIVO".equals(record.status()) ? "N" : "S");
            veiculoRepository.save(v);

            saveSchedules(v, record.routes(), record.drivers());

            return ResponseEntity.ok(new GenericResponse<>("201", "Bus registered successfully", mapToRecord(v)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Error registering vehicle: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<VeiculoRecord>> updateVeiculo(String plate, VeiculoRecord record) {
        try {
            Optional<Veiculo> optionalV = veiculoRepository.findById(plate);
            if (optionalV.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>("404", "Vehicle not found with plate: " + plate, null));
            }

            Veiculo v = optionalV.get();
            v.setVeiculoId(record.id());
            v.setCapacidade(record.capacity());
            v.setFlagAtivo("INATIVO".equals(record.status()) ? "N" : "S");

            // Update model if changed
            if (!v.getVeiculoModelo().getModelo().equalsIgnoreCase(record.model()) || !v.getVeiculoModelo().getTipo().equalsIgnoreCase(record.type())) {
                String[] parsed = parseBrandAndModel(record.model());
                String brand = parsed[0];
                String modelName = parsed[1];

                List<VeiculoModelo> models = veiculoModeloRepository.findAll();
                VeiculoModelo model = models.stream()
                        .filter(m -> m.getModelo().equalsIgnoreCase(modelName) && m.getTipo().equalsIgnoreCase(record.type()))
                        .findFirst()
                        .orElseGet(() -> veiculoModeloRepository.save(new VeiculoModelo(brand, modelName, 2, record.type())));
                v.setVeiculoModelo(model);
            }

            // Update garage if changed
            if (v.getGaragem() == null || !v.getGaragem().getDescricao().equalsIgnoreCase(record.garage())) {
                List<Garagem> garages = garagemRepository.findAll();
                Garagem garage = garages.stream()
                        .filter(g -> g.getDescricao().equalsIgnoreCase(record.garage()))
                        .findFirst()
                        .orElseGet(() -> {
                            Operador op = v.getOperador();
                            return garagemRepository.save(new Garagem(record.garage(), op, 3550308L, "Logradouro Padrao", "S/N", "01001000"));
                        });
                v.setGaragem(garage);
            }

            veiculoRepository.save(v);

            // Clear and reload schedules
            linhaPlacaRepository.deleteAll(linhaPlacaRepository.findByLinhaPlacaIdVeiculoPlaca(v.getPlaca()));
            motoristaPlacaRepository.deleteAll(motoristaPlacaRepository.findByMotoristaPlacaIdVeiculoPlaca(v.getPlaca()));

            saveSchedules(v, record.routes(), record.drivers());

            return ResponseEntity.ok(new GenericResponse<>("200", "Vehicle updated successfully", mapToRecord(v)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Error updating vehicle: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<Void>> deleteVeiculo(String plate) {
        try {
            Optional<Veiculo> optionalV = veiculoRepository.findById(plate);
            if (optionalV.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>("404", "Vehicle not found with plate: " + plate, null));
            }

            // Delete dependencies first
            linhaPlacaRepository.deleteAll(linhaPlacaRepository.findByLinhaPlacaIdVeiculoPlaca(plate));
            motoristaPlacaRepository.deleteAll(motoristaPlacaRepository.findByMotoristaPlacaIdVeiculoPlaca(plate));
            veiculoRepository.deleteById(plate);

            return ResponseEntity.ok(new GenericResponse<>("200", "Vehicle deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Error deleting vehicle: " + e.getMessage(), null));
        }
    }

    private void saveSchedules(Veiculo v, List<RouteBlockRecord> routes, List<DriverBlockRecord> drivers) {
        if (routes != null) {
            for (RouteBlockRecord r : routes) {
                // Parse e.g., "Linha 3301 - 10" or similar
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

                for (String day : r.days()) {
                    String dbDay = UI_TO_DB_DAYS.getOrDefault(day.toUpperCase(), "SEGUNDA");
                    LinhaPlaca lp = new LinhaPlaca(lineId, atendimento, 3550308L, v.getPlaca(), dbDay, r.startTime(), r.endTime());
                    linhaPlacaRepository.save(lp);
                }
            }
        }

        if (drivers != null) {
            for (DriverBlockRecord d : drivers) {
                // Find driver user by name
                List<Usuario> users = usuarioRepository.findAll();
                Usuario driver = users.stream()
                        .filter(u -> u.getNome().equalsIgnoreCase(d.name()))
                        .findFirst()
                        .orElse(null);

                if (driver != null) {
                    for (String day : d.days()) {
                        String dbDay = UI_TO_DB_DAYS.getOrDefault(day.toUpperCase(), "SEGUNDA");
                        MotoristaPlaca mp = new MotoristaPlaca(driver.getCpf(), v.getPlaca(), dbDay, d.startTime(), d.endTime());
                        motoristaPlacaRepository.save(mp);
                    }
                }
            }
        }
    }
}
