package ro.ugal.autopartsstore.audit;

import java.lang.reflect.Field;

public class AuditIdExtractor {

    private AuditIdExtractor() {}

    public static Long tryExtractId(Object obj) {
        if (obj == null) return null;

        // Dacă e Long direct
        if (obj instanceof Long l) return l;

        // Dacă are câmp "id"
        try {
            Field f = obj.getClass().getDeclaredField("id");
            f.setAccessible(true);
            Object val = f.get(obj);
            if (val instanceof Number n) return n.longValue();
        } catch (Exception ignored) {}

        return null;
    }
}
