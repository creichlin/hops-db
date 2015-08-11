package ch.kerbtier.hopsdb.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface Column {

  String name() default NULL;
  boolean key() default false;

  public static final String NULL = "NULL_oZShvXLv0RMTBhe61cN7";

}
