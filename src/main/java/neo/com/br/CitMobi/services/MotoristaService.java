package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.motorista.MotoristaHorarioRecord;
import neo.com.br.CitMobi.models.records.motorista.MotoristaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import neo.com.br.CitMobi.models.veiculo.LinhaPlaca;
import neo.com.br.CitMobi.models.veiculo.MotoristaPlaca;
import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.repository.veiculo.LinhaPlacaRepository;
import neo.com.br.CitMobi.repository.veiculo.MotoristaPlacaRepository;
import neo.com.br.CitMobi.repository.veiculo.VeiculoRepository;
import neo.com.br.CitMobi.repository.viagem.ViagemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MotoristaService {

    private static final Logger logger = LoggerFactory.getLogger(MotoristaService.class);

    private final UsuarioRepository usuarioRepository;
    private final MotoristaPlacaRepository motoristaPlacaRepository;
    private final LinhaPlacaRepository linhaPlacaRepository;
    private final VeiculoRepository veiculoRepository;
    private final LinhaRepository linhaRepository;
    private final OperadorRepository operadorRepository;
    private final ViagemRepository viagemRepository;
    private final TokenService tokenService;

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

    public MotoristaService(UsuarioRepository usuarioRepository,
                            MotoristaPlacaRepository motoristaPlacaRepository,
                            LinhaPlacaRepository linhaPlacaRepository,
                            VeiculoRepository veiculoRepository,
                            LinhaRepository linhaRepository,
                            OperadorRepository operadorRepository,
                            ViagemRepository viagemRepository,
                            TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.motoristaPlacaRepository = motoristaPlacaRepository;
        this.linhaPlacaRepository = linhaPlacaRepository;
        this.veiculoRepository = veiculoRepository;
        this.linhaRepository = linhaRepository;
        this.operadorRepository = operadorRepository;
        this.viagemRepository = viagemRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GenericResponse<List<MotoristaRecord>>> getAllMotoristas(String authHeader) {
        try {
            List<Usuario> motoristas;
            String cnpj = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    cnpj = tokenService.getOperadorIdFromToken(authHeader);
                } catch (Exception ignored) {}
            }

            if (cnpj != null && !cnpj.isEmpty()) {
                motoristas = usuarioRepository.findByOperadorCnpjAndRoleAndFlagAtivo(cnpj, UsuarioRole.MOTORISTA, "S");
            } else {
                motoristas = usuarioRepository.findAll().stream()
                        .filter(u -> u.getRole() == UsuarioRole.MOTORISTA && "S".equalsIgnoreCase(u.getFlagAtivo()))
                        .collect(Collectors.toList());
            }

            List<MotoristaRecord> records = motoristas.stream().map(this::mapToRecord).collect(Collectors.toList());
            return ResponseEntity.ok(new GenericResponse<>("200", "Motoristas listados com sucesso", records));
        } catch (Exception e) {
            logger.error("Erro ao buscar motoristas: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao buscar motoristas: " + e.getMessage(), null));
        }
    }

    public MotoristaRecord mapToRecord(Usuario u) {
        List<MotoristaPlaca> mpList = motoristaPlacaRepository.findByMotorista_Id(u.getId());
        if (mpList.isEmpty() && u.getCpf() != null) {
            mpList = motoristaPlacaRepository.findByMotorista_Cpf(u.getCpf());
        }

        Map<String, List<String>> horariosGrouped = new HashMap<>();
        Map<String, String[]> horarioMeta = new HashMap<>();

        for (MotoristaPlaca mp : mpList) {
            Veiculo v = mp.getVeiculo();
            String veiculoId = v != null ? (v.getCodigoVeiculo() != null ? v.getCodigoVeiculo() : String.valueOf(v.getId())) : "1";
            String veiculoPlaca = v != null ? v.getPlaca() : "SEM PLACA";
            String veiculoModelo = (v != null && v.getVeiculoModelo() != null) ? v.getVeiculoModelo().getModelo() : "Padrão";

            String rotaId = "1";
            String rotaNome = "Linha Operacional";

            if (v != null) {
                List<LinhaPlaca> lpList = linhaPlacaRepository.findByVeiculo_Placa(v.getPlaca());
                Optional<LinhaPlaca> matchLp = lpList.stream()
                        .filter(lp -> lp.getDiaSemana() != null && lp.getDiaSemana().equalsIgnoreCase(mp.getDiaSemana()))
                        .findFirst();
                if (matchLp.isEmpty() && !lpList.isEmpty()) {
                    matchLp = Optional.of(lpList.get(0));
                }

                if (matchLp.isPresent() && matchLp.get().getLinha() != null) {
                    Linha l = matchLp.get().getLinha();
                    rotaId = String.valueOf(l.getId());
                    rotaNome = "Linha " + l.getCodigoLinha() + " - " + l.getAtendimento();
                }
            }

            String startTime = mp.getHoraInicio() != null ? mp.getHoraInicio().toString().substring(0, 5) : "06:00";
            String endTime = mp.getHoraFim() != null ? mp.getHoraFim().toString().substring(0, 5) : "14:00";

            String key = veiculoPlaca + "|" + rotaNome + "|" + startTime + "|" + endTime;
            String uiDay = DB_TO_UI_DAYS.getOrDefault(mp.getDiaSemana(), "SEG");

            horariosGrouped.computeIfAbsent(key, k -> new ArrayList<>()).add(uiDay);
            horarioMeta.put(key, new String[]{veiculoId, veiculoPlaca, veiculoModelo, rotaId, rotaNome, startTime, endTime});
        }

        List<MotoristaHorarioRecord> horarios = horariosGrouped.entrySet().stream().map(entry -> {
            String[] meta = horarioMeta.get(entry.getKey());
            return new MotoristaHorarioRecord(
                    meta[0],
                    meta[1],
                    meta[2],
                    meta[3],
                    meta[4],
                    meta[5],
                    meta[6],
                    entry.getValue()
            );
        }).collect(Collectors.toList());

        String status = "FORA DE TURNO";
        boolean hasActiveTrip = viagemRepository.findFirstByMotorista_IdAndStatus(u.getId(), ViagemStatus.EM_ANDAMENTO).isPresent();
        if (hasActiveTrip) {
            status = "EM ATENDIMENTO";
        } else if (!horarios.isEmpty()) {
            status = "AGUARDANDO";
        }

        return new MotoristaRecord(
                u.getId() != null ? u.getId().toString() : u.getLogin(),
                u.getNome(),
                u.getCpf(),
                u.getTelefone(),
                status,
                horarios
        );
    }

    @Transactional
    public ResponseEntity<GenericResponse<MotoristaRecord>> createMotorista(MotoristaRecord record, String authHeader) {
        try {
            String cnpj = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    cnpj = tokenService.getOperadorIdFromToken(authHeader);
                } catch (Exception ignored) {}
            }

            Operador op = null;
            if (cnpj != null && !cnpj.isEmpty()) {
                op = operadorRepository.findByCnpj(cnpj).orElse(null);
            }
            if (op == null) {
                op = operadorRepository.findAll().stream().findFirst().orElse(null);
            }

            String rawCpf = record.cpf() != null ? record.cpf().replaceAll("\\D", "") : "";
            String rawPhone = record.telefone() != null ? record.telefone().replaceAll("\\D", "") : "";
            String login = rawCpf.isEmpty() ? (record.nome() != null ? record.nome().toLowerCase().replaceAll("\\s+", "") : "driver") : rawCpf;

            Usuario u = new Usuario();
            u.setId(UUID.randomUUID());
            u.setLogin(login);
            u.setNome(record.nome());
            u.setCpf(rawCpf);
            u.setTelefone(rawPhone);
            u.setEmail(login + "@citmobi.com.br");
            u.setSenha(new BCryptPasswordEncoder().encode("mobibrasil"));
            u.setRole(UsuarioRole.MOTORISTA);
            u.setFlagAtivo("S");
            u.setOperador(op);
            u.setDataCriacao(Instant.now());

            Usuario saved = usuarioRepository.save(u);

            if (record.horarios() != null && !record.horarios().isEmpty()) {
                saveSchedules(saved, record.horarios());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new GenericResponse<>("201", "Motorista cadastrado com sucesso", mapToRecord(saved)));
        } catch (Exception e) {
            logger.error("Erro ao cadastrar motorista: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao cadastrar motorista: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<MotoristaRecord>> updateMotorista(String idOrLogin, MotoristaRecord record, String authHeader) {
        try {
            Optional<Usuario> optUser = Optional.empty();
            try {
                UUID uuid = UUID.fromString(idOrLogin);
                optUser = usuarioRepository.findById(uuid);
            } catch (IllegalArgumentException ignored) {}

            if (optUser.isEmpty()) {
                String cleanCpf = idOrLogin.replaceAll("\\D", "");
                optUser = usuarioRepository.findAll().stream()
                        .filter(u -> u.getLogin().equalsIgnoreCase(idOrLogin) || (u.getCpf() != null && u.getCpf().equalsIgnoreCase(cleanCpf)))
                        .findFirst();
            }

            if (optUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>("404", "Motorista não encontrado: " + idOrLogin, null));
            }

            Usuario u = optUser.get();
            if (record.nome() != null && !record.nome().isBlank()) {
                u.setNome(record.nome());
            }
            if (record.telefone() != null && !record.telefone().isBlank()) {
                u.setTelefone(record.telefone().replaceAll("\\D", ""));
            }
            if (record.cpf() != null && !record.cpf().isBlank()) {
                u.setCpf(record.cpf().replaceAll("\\D", ""));
            }

            Usuario saved = usuarioRepository.save(u);

            if (record.horarios() != null) {
                motoristaPlacaRepository.deleteAll(motoristaPlacaRepository.findByMotorista_Id(saved.getId()));
                saveSchedules(saved, record.horarios());
            }

            return ResponseEntity.ok(new GenericResponse<>("200", "Motorista atualizado com sucesso", mapToRecord(saved)));
        } catch (Exception e) {
            logger.error("Erro ao atualizar motorista: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao atualizar motorista: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<Void>> deleteMotorista(String idOrLogin) {
        try {
            Optional<Usuario> optUser = Optional.empty();
            try {
                UUID uuid = UUID.fromString(idOrLogin);
                optUser = usuarioRepository.findById(uuid);
            } catch (IllegalArgumentException ignored) {}

            if (optUser.isEmpty()) {
                String cleanCpf = idOrLogin.replaceAll("\\D", "");
                optUser = usuarioRepository.findAll().stream()
                        .filter(u -> u.getLogin().equalsIgnoreCase(idOrLogin) || (u.getCpf() != null && u.getCpf().equalsIgnoreCase(cleanCpf)))
                        .findFirst();
            }

            if (optUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>("404", "Motorista não encontrado: " + idOrLogin, null));
            }

            Usuario u = optUser.get();
            u.setFlagAtivo("N");
            usuarioRepository.save(u);

            return ResponseEntity.ok(new GenericResponse<>("200", "Motorista inativado com sucesso", null));
        } catch (Exception e) {
            logger.error("Erro ao inativar motorista: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao inativar motorista: " + e.getMessage(), null));
        }
    }

    private void saveSchedules(Usuario driver, List<MotoristaHorarioRecord> horarios) {
        if (horarios == null || horarios.isEmpty()) return;

        for (MotoristaHorarioRecord h : horarios) {
            Optional<Veiculo> optV = Optional.empty();
            if (h.veiculoPlaca() != null && !h.veiculoPlaca().isBlank()) {
                optV = veiculoRepository.findByPlaca(h.veiculoPlaca().trim().toUpperCase());
            }
            if (optV.isEmpty() && h.veiculoId() != null) {
                try {
                    Long vId = Long.parseLong(String.valueOf(h.veiculoId()));
                    optV = veiculoRepository.findById(vId);
                } catch (NumberFormatException ignored) {}
            }

            Veiculo veiculo = optV.orElseGet(() -> veiculoRepository.findAll().stream().findFirst().orElse(null));

            if (veiculo != null) {
                LocalTime start = h.startTime() != null ? LocalTime.parse(h.startTime().length() == 5 ? h.startTime() + ":00" : h.startTime()) : LocalTime.of(6, 0);
                LocalTime end = h.endTime() != null ? LocalTime.parse(h.endTime().length() == 5 ? h.endTime() + ":00" : h.endTime()) : LocalTime.of(14, 0);

                if (h.days() != null) {
                    for (String day : h.days()) {
                        String dbDay = UI_TO_DB_DAYS.getOrDefault(day.toUpperCase(), "SEGUNDA");
                        MotoristaPlaca mp = new MotoristaPlaca(driver, veiculo, dbDay, start, end);
                        motoristaPlacaRepository.save(mp);
                    }
                }
            }
        }
    }
}
