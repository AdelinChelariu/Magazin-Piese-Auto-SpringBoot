package ro.ugal.autopartsstore.audit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audit {
    String action();       // ex: CREATE_PRODUCT
    String entity();       // ex: Product
}
