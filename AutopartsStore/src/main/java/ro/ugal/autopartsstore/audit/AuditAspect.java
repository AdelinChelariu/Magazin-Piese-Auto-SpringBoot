package ro.ugal.autopartsstore.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @Around("@annotation(audit)")
    public Object around(ProceedingJoinPoint pjp, Audit audit) throws Throwable {
        Long entityId = null;

        try {
            Object result = pjp.proceed();

            // încercăm să extragem id-ul: dacă metoda returnează un obiect cu câmp "id"
            entityId = AuditIdExtractor.tryExtractId(result);

            auditService.log(audit.action(), audit.entity(), entityId, true, null);
            return result;
        } catch (Throwable ex) {
            auditService.log(audit.action(), audit.entity(), entityId, false, ex.getMessage());
            throw ex;
        }
    }
}
