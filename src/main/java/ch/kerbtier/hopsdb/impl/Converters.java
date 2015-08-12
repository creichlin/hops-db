package ch.kerbtier.hopsdb.impl;

import java.util.HashMap;
import java.util.Map;

import ch.kerbtier.hopsdb.impl.converters.ClobToString;
import ch.kerbtier.hopsdb.impl.converters.DateTime;
import ch.kerbtier.hopsdb.impl.converters.NumberToLong;
import ch.kerbtier.hopsdb.impl.converters.NumberToInteger;
import ch.kerbtier.hopsdb.impl.converters.SqlTimeToDate;
import ch.kerbtier.hopsdb.util.Util;

public class Converters {
  
  private static Map<Class<?>, Map<Class<?>, Converter<?, ?>>> mappings = new HashMap<>();
  
  static {
    register(new DateTime());
    register(new ClobToString());
    register(new SqlTimeToDate());
    register(new NumberToInteger());
    register(new NumberToLong());
  }
  
  public static void register(Converter<?, ?> converter) {
    Class<?> from = converter.from();
    Class<?> to = converter.to();

    if(!mappings.containsKey(from)) {
      synchronized (mappings) {
        if(!mappings.containsKey(from)) {
          mappings.put(from, new HashMap<Class<?>, Converter<?, ?>>());
        }
      }
    }
    Map<Class<?>, Converter<?, ?>> toMappings = mappings.get(from);
    
    if(toMappings.containsKey(to)) {
      throw new RuntimeException("converter " + converter + " using types already registers");
    }

    toMappings.put(to, converter);
  }

  public static <T> Object convert(T from, Class<?> to) {
    for(Class<?> c: Util.allInterfaces(from.getClass())) {
      Object response = convert(from, (Class<T>)c, to);
      if(response != null) {
        return response;
      }
    }
    
    return from;
  }
  
  private static <T> Object convert(T from, Class<T> fromType, Class<?> to) {
    if(mappings.containsKey(fromType)) {
      Map<Class<?>, Converter<T, ?>> toMappings = (Map<Class<?>, Converter<T, ?>>)(Object)mappings.get(fromType);
      if(toMappings.containsKey(to)) {
        try {
          return toMappings.get(to).convert(from);
        }catch(Exception e) {
          throw new RuntimeException(e);
        }
        
      }
    }
    return null;
  }
}
