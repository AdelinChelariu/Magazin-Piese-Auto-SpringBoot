package ro.ugal.autopartsstore.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ro.ugal.autopartsstore.domain.ActivityLog;
import ro.ugal.autopartsstore.repository.ActivityLogRepository;
import ro.ugal.autopartsstore.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private final ActivityLogRepository logRepo;
    private final UserRepository userRepo;
    private final HttpServletRequest request;

    public AuditService(ActivityLogRepository logRepo, UserRepository userRepo, HttpServletRequest request) {
        this.logRepo = logRepo;
        this.userRepo = userRepo;
        this.request = request;
    }

    public void log(String action, String entity, Long entityId, boolean success, String errorMessage) {
        ActivityLog l = new ActivityLog();

        // user + roles
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            String username = auth.getName();
            userRepo.findByUsername(username).ifPresent(u -> l.setUser(u));

            String roles = auth.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .sorted()
                    .collect(Collectors.joining(","));
            l.setRoleSnapshot(roles);
        } else {
            l.setRoleSnapshot("");
        }

        l.setAction(action);
        l.setEntity(entity);
        l.setEntityId(entityId);
        l.setSuccess(success);
        l.setErrorMessage(errorMessage);

        // request info
        l.setIpAddress(getClientIp());
        l.setCreatedAt(LocalDateTime.now());

        // opțional: salvezi metoda/path dacă ai coloane; dacă nu, ignoră
        // l.setRequestMethod(request.getMethod());
        // l.setRequestPath(request.getRequestURI());

        logRepo.save(l);
    }

    private String getClientIp() {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
