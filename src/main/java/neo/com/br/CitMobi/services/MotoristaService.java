package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.motorista.MotoristaHorarioRecord;
import neo.com.br.CitMobi.models.records.motorista.MotoristaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import neo.com.br.CitMobi.models.veiculo.MotoristaHora;
import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.veiculo.VeiculoEscala;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.repository.usuario.MotoristaHoraRepository;
import neo.com.br.CitMobi.repository.veiculo.VeiculoEscalaRepository;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MotoristaService {

    private static final Logger logger = LoggerFactory.getLogger(MotoristaService.class);

    private final UsuarioRepository usuarioRepository;
    private final VeiculoEscalaRepository veiculoEscalaRepository;
    private final MotoristaHoraRepository motoristaHoraRepository;
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
                            VeiculoEscalaRepository veiculoEscalaRepository,
                            MotoristaHoraRepository motoristaHoraRepository,
                            VeiculoRepository veiculoRepository,
                            LinhaRepository linhaRepository,
                            OperadorRepository operadorRepository,
                            ViagemRepository viagemRepository,
                            TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.veiculoEscalaRepository = veiculoEscalaRepository;
        this.motoristaHoraRepository = motoristaHoraRepository;
        this.veiculoRepository = veiculoRepository;
        this.linhaRepository = linhaRepository;
        this.operadorRepository = operadorRepository;
        this.viagemRepository = viagemRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GenericResponse<List<MotoristaRecord>>> getAllMotoristas(Long operadorId, String authHeader) {
        try {
            List<Usuario> motoristas;
            String cnpj = null;
            String flagRegulador = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    cnpj = tokenService.getOperadorIdFromToken(authHeader);
                    flagRegulador = tokenService.getFlagReguladorFromToken(authHeader);
                } catch (Exception ignored) {}
            }

            if (operadorId != null) {
                motoristas = usuarioRepository.findByOperador_IdAndRoleAndFlagAtivo(operadorId, UsuarioRole.MOTORISTA, "S");
            } else if ("S".equalsIgnoreCase(flagRegulador)) {
                motoristas = usuarioRepository.findAll().stream()
                        .filter(u -> u.getRole() == UsuarioRole.MOTORISTA && "S".equalsIgnoreCase(u.getFlagAtivo()))
                        .collect(Collectors.toList());
            } else if (cnpj != null && !cnpj.isEmpty()) {
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
        List<VeiculoEscala> escalasList = veiculoEscalaRepository.findByMotorista_Id(u.getId());
        if (escalasList.isEmpty() && u.getCpf() != null) {
            escalasList = veiculoEscalaRepository.findByMotorista_Cpf(u.getCpf());
        }

        Map<String, List<String>> horariosGrouped = new HashMap<>();
        Map<String, String[]> horarioMeta = new HashMap<>();

        for (VeiculoEscala escala : escalasList) {
            Veiculo v = escala.getVeiculo();
            String veiculoId = v != null ? (v.getCodigoVeiculo() != null ? v.getCodigoVeiculo() : String.valueOf(v.getId())) : "1";
            String veiculoPlaca = v != null ? v.getPlaca() : "SEM PLACA";
            String veiculoModelo = (v != null && v.getVeiculoModelo() != null) ? v.getVeiculoModelo().getModelo() : "Padrão";

            String rotaId = "1";
            String rotaNome = "Linha Operacional";
            if (escala.getLinha() != null) {
                Linha l = escala.getLinha();
                rotaId = String.valueOf(l.getId());
                rotaNome = "Linha " + l.getCodigoLinha() + " - " + l.getAtendimento();
            }

            String startTime = escala.getHoraInicio() != null ? escala.getHoraInicio().toString().substring(0, 5) : "06:00";
            String endTime = escala.getHoraFim() != null ? escala.getHoraFim().toString().substring(0, 5) : "14:00";

            String key = veiculoPlaca + "|" + rotaNome + "|" + startTime + "|" + endTime;
            String uiDay = DB_TO_UI_DAYS.getOrDefault(escala.getDiaSemana(), "SEG");

            horariosGrouped.computeIfAbsent(key, k -> new ArrayList<>()).add(uiDay);
            horarioMeta.put(key, new String[]{veiculoId, veiculoPlaca, veiculoModelo, rotaId, rotaNome, startTime, endTime});
        }

        List<MotoristaHora> horas = motoristaHoraRepository.findByMotorista_Id(u.getId());
        String pausaInicioStr = null;
        String pausaFimStr = null;
        if (!horas.isEmpty()) {
            MotoristaHora mh = horas.get(0);
            pausaInicioStr = mh.getPausaInicio() != null ? mh.getPausaInicio().toString().substring(0, 5) : null;
            pausaFimStr = mh.getPausaFim() != null ? mh.getPausaFim().toString().substring(0, 5) : null;
        }

        long[] scheduleSeq = {1L};
        final String finalPausaInicio = pausaInicioStr;
        final String finalPausaFim = pausaFimStr;
        List<MotoristaHorarioRecord> horarios = horariosGrouped.entrySet().stream().map(entry -> {
            String[] meta = horarioMeta.get(entry.getKey());
            return new MotoristaHorarioRecord(
                    scheduleSeq[0]++,
                    meta[0],
                    meta[1],
                    meta[2],
                    meta[3],
                    meta[4],
                    meta[5],
                    meta[6],
                    entry.getValue(),
                    finalPausaInicio,
                    finalPausaFim
            );
        }).collect(Collectors.toList());

        String status = "FORA DE TURNO";
        boolean hasActiveTrip = viagemRepository.findFirstByMotorista_IdAndStatus(u.getId(), ViagemStatus.EM_ANDAMENTO).isPresent();
        if (hasActiveTrip) {
            status = "EM ATENDIMENTO";
        } else {
            LocalTime now = LocalTime.now();
            boolean isBreak = horas.stream().anyMatch(h ->
                    h.getPausaInicio() != null && h.getPausaFim() != null &&
                    !now.isBefore(h.getPausaInicio()) && !now.isAfter(h.getPausaFim())
            );
            if (isBreak) {
                status = "PAUSA";
            } else if (!horarios.isEmpty()) {
                status = "AGUARDANDO";
            }
        }

        return new MotoristaRecord(
                u.getId() != null ? u.getId().toString() : u.getLogin(),
                u.getNome(),
                u.getCpf(),
                u.getCnhNumero(),
                u.getCnhValidade(),
                u.getLogin(),
                u.getTelefone(),
                u.getOperador() != null ? u.getOperador().getId() : null,
                u.getOperador() != null ? u.getOperador().getRazaoSocial() : null,
                status,
                horarios
        );
    }

    @Transactional
    public ResponseEntity<GenericResponse<MotoristaRecord>> createMotorista(MotoristaRecord record, String authHeader) {
        try {
            if (record.cnhNumero() == null || record.cnhNumero().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new GenericResponse<>("400", "CNH number is required for drivers", null));
            }
            if (record.cnhValidade() == null || record.cnhValidade().isBefore(LocalDate.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new GenericResponse<>("400", "CNH validity date is required and must not be expired", null));
            }

            Operador op = null;
            if (record.operadorId() != null) {
                op = operadorRepository.findById(record.operadorId()).orElse(null);
            }

            if (op == null) {
                String cnpj = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    try {
                        cnpj = tokenService.getOperadorIdFromToken(authHeader);
                    } catch (Exception ignored) {}
                }

                if (cnpj != null && !cnpj.isEmpty()) {
                    op = operadorRepository.findByCnpj(cnpj).orElse(null);
                }
            }

            if (op == null) {
                op = operadorRepository.findAll().stream().findFirst().orElse(null);
            }

            String rawCpf = record.cpf() != null ? record.cpf().replaceAll("\\D", "") : "";
            String rawPhone = record.telefone() != null ? record.telefone().replaceAll("\\D", "") : "";
            String login = (record.login() != null && !record.login().isBlank())
                    ? record.login().trim()
                    : (rawCpf.isEmpty() ? (record.nome() != null ? record.nome().toLowerCase().replaceAll("\\s+", "") : "driver") : rawCpf);

            Usuario u = new Usuario();
            u.setId(UUID.randomUUID());
            u.setLogin(login);
            u.setNome(record.nome());
            u.setCpf(rawCpf);
            u.setCnhNumero(record.cnhNumero().trim());
            u.setCnhValidade(record.cnhValidade());
            u.setTelefone(rawPhone);
            u.setEmail(login + "@citmobi.com.br");
            u.setSenha(new BCryptPasswordEncoder().encode("mobibrasil"));
            u.setRole(UsuarioRole.MOTORISTA);
            u.setFlagAtivo("S");
            u.setOperador(op);
            u.setDataCriacao(Instant.now());

            Usuario saved = usuarioRepository.save(u);

            if (record.horarios() != null && !record.horarios().isEmpty()) {
                String scheduleError = saveSchedules(saved, record.horarios());
                if (scheduleError != null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new GenericResponse<>("400", scheduleError, null));
                }
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
            if (record.cnhNumero() != null && !record.cnhNumero().isBlank()) {
                u.setCnhNumero(record.cnhNumero().trim());
            }
            if (record.cnhValidade() != null) {
                if (record.cnhValidade().isBefore(LocalDate.now())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new GenericResponse<>("400", "CNH validity date cannot be in the past", null));
                }
                u.setCnhValidade(record.cnhValidade());
            }

            Usuario saved = usuarioRepository.save(u);

            if (record.horarios() != null) {
                List<VeiculoEscala> oldEscalas = veiculoEscalaRepository.findByMotorista_Id(saved.getId());
                for (VeiculoEscala old : oldEscalas) {
                    old.setMotorista(null);
                    veiculoEscalaRepository.save(old);
                }
                motoristaHoraRepository.deleteAll(motoristaHoraRepository.findByMotorista_Id(saved.getId()));

                String scheduleError = saveSchedules(saved, record.horarios());
                if (scheduleError != null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new GenericResponse<>("400", scheduleError, null));
                }
            }

            return ResponseEntity.ok(new GenericResponse<>("200", "Motorista atualizado com sucesso", mapToRecord(saved)));
        } catch (Exception e) {
            logger.error("Erro ao atualizar motorista: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao atualizar motorista: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<MotoristaRecord>> transferDriverOperator(String idOrLogin, Long targetOperadorId, String authHeader) {
        try {
            boolean isAdminOrRegulator = false;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String flagRegulador = tokenService.getFlagReguladorFromToken(authHeader);
                    String role = tokenService.getRoleFromToken(authHeader);
                    if ("S".equalsIgnoreCase(flagRegulador) || "ROLE_ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
                        isAdminOrRegulator = true;
                    }
                } catch (Exception ignored) {}
            }

            if (!isAdminOrRegulator) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new GenericResponse<>("403", "Apenas administradores do sistema ou órgãos reguladores podem transferir motoristas entre operadoras", null));
            }

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

            Usuario driver = optUser.get();

            boolean hasActiveTrip = viagemRepository.findFirstByMotorista_IdAndStatus(driver.getId(), ViagemStatus.EM_ANDAMENTO).isPresent();
            if (hasActiveTrip) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new GenericResponse<>("400", "Não é possível transferir motorista com viagem em andamento", null));
            }

            Optional<Operador> optTargetOp = operadorRepository.findById(targetOperadorId);
            if (optTargetOp.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>("404", "Operadora de destino não encontrada: " + targetOperadorId, null));
            }

            List<VeiculoEscala> oldEscalas = veiculoEscalaRepository.findByMotorista_Id(driver.getId());
            for (VeiculoEscala slot : oldEscalas) {
                slot.setMotorista(null);
                veiculoEscalaRepository.save(slot);
            }

            motoristaHoraRepository.deleteAll(motoristaHoraRepository.findByMotorista_Id(driver.getId()));

            driver.setOperador(optTargetOp.get());
            Usuario saved = usuarioRepository.save(driver);

            return ResponseEntity.ok(new GenericResponse<>("200", "Motorista transferido de operadora com sucesso", mapToRecord(saved)));
        } catch (Exception e) {
            logger.error("Erro ao transferir motorista: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao transferir motorista: " + e.getMessage(), null));
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

            List<VeiculoEscala> oldEscalas = veiculoEscalaRepository.findByMotorista_Id(u.getId());
            for (VeiculoEscala slot : oldEscalas) {
                slot.setMotorista(null);
                veiculoEscalaRepository.save(slot);
            }

            return ResponseEntity.ok(new GenericResponse<>("200", "Motorista inativado com sucesso", null));
        } catch (Exception e) {
            logger.error("Erro ao inativar motorista: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao inativar motorista: " + e.getMessage(), null));
        }
    }

    private String saveSchedules(Usuario driver, List<MotoristaHorarioRecord> horarios) {
        if (horarios == null || horarios.isEmpty()) return null;

        Map<String, List<LocalTime[]>> daySlots = new HashMap<>();

        for (MotoristaHorarioRecord h : horarios) {
            LocalTime start = h.startTime() != null ? LocalTime.parse(h.startTime().length() == 5 ? h.startTime() + ":00" : h.startTime()) : LocalTime.of(6, 0);
            LocalTime end = h.endTime() != null ? LocalTime.parse(h.endTime().length() == 5 ? h.endTime() + ":00" : h.endTime()) : LocalTime.of(14, 0);

            if (start.isAfter(end) || start.equals(end)) {
                return "Horário de início deve ser anterior ao horário de término";
            }

            if (h.days() != null) {
                for (String day : h.days()) {
                    String dbDay = UI_TO_DB_DAYS.getOrDefault(day.toUpperCase(), "SEGUNDA");
                    List<LocalTime[]> existing = daySlots.computeIfAbsent(dbDay, k -> new ArrayList<>());
                    for (LocalTime[] slot : existing) {
                        if (!(end.isBefore(slot[0]) || end.equals(slot[0]) || start.isAfter(slot[1]) || start.equals(slot[1]))) {
                            return "Conflito de horários no dia " + day + " para o motorista";
                        }
                    }
                    existing.add(new LocalTime[]{start, end});
                }
            }
        }

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

            Linha linha = null;
            if (h.rotaId() != null) {
                try {
                    Long rId = Long.parseLong(String.valueOf(h.rotaId()));
                    linha = linhaRepository.findById(rId).orElse(null);
                } catch (NumberFormatException ignored) {}
            }
            if (linha == null) {
                linha = linhaRepository.findAll().stream().findFirst().orElse(null);
            }

            if (veiculo != null && linha != null) {
                LocalTime start = h.startTime() != null ? LocalTime.parse(h.startTime().length() == 5 ? h.startTime() + ":00" : h.startTime()) : LocalTime.of(6, 0);
                LocalTime end = h.endTime() != null ? LocalTime.parse(h.endTime().length() == 5 ? h.endTime() + ":00" : h.endTime()) : LocalTime.of(14, 0);

                if (h.days() != null) {
                    for (String day : h.days()) {
                        String dbDay = UI_TO_DB_DAYS.getOrDefault(day.toUpperCase(), "SEGUNDA");
                        VeiculoEscala escala = new VeiculoEscala(veiculo, linha, driver, dbDay, start, end);
                        veiculoEscalaRepository.save(escala);

                        LocalTime pStart = h.pausaInicio() != null ? LocalTime.parse(h.pausaInicio().length() == 5 ? h.pausaInicio() + ":00" : h.pausaInicio()) : start.plusHours(4);
                        LocalTime pEnd = h.pausaFim() != null ? LocalTime.parse(h.pausaFim().length() == 5 ? h.pausaFim() + ":00" : h.pausaFim()) : pStart.plusHours(1);
                        MotoristaHora mh = new MotoristaHora(driver, dbDay, start, end, pStart, pEnd);
                        motoristaHoraRepository.save(mh);
                    }
                }
            }
        }
        return null;
    }
}
