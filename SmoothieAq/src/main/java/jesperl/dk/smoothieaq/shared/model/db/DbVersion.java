package jesperl.dk.smoothieaq.shared.model.db;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

@Retention(RUNTIME)
@Target(TYPE)
public @interface DbVersion {
	short value();
}
