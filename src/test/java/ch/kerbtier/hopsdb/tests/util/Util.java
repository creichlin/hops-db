package ch.kerbtier.hopsdb.tests.util;

import java.net.URL;

import ch.kerbtier.hopsdb.Db;
import ch.kerbtier.hopsdb.DbPs;
import ch.kerbtier.hopsdb.model.annotations.AnnotationModelProvider;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;


public class Util {
  private static int counter;

  public static Db create(Object context) {
    String name = context.getClass().getCanonicalName();
    name = name.substring(name.lastIndexOf(".") + 1);
    name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name) + ".sql";
    
    AnnotationModelProvider amp = new AnnotationModelProvider();
    
    Db db = new Db("jdbc:h2:mem:name" + (counter++) + ";USER=test;PASSWORD=test", amp);
    db.setPrintStatements(true);

    System.out.println("got db " + (counter - 1));
    
    URL url = Resources.getResource(context.getClass(), name);
    try {
      String code = Resources.toString(url, Charsets.UTF_8);

      DbPs ps = db.prepareStatement(code);
      ps.executeUpdate();
      db.commit();
    } catch (Exception e) {
      db.rollback();
      throw new RuntimeException(e);
    }

    return db;
  }
}
