package ch.kerbtier.hopsdb.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Util {

  public static List<Class<?>> allInterfaces(Class<?> from) {
    Set<Class<?>> scheduled = new HashSet<>();
    LinkedList<Class<?>> queue = new LinkedList<>();
    List<Class<?>> result = new ArrayList<>();

    queue.add(from);

    while (!queue.isEmpty()) {
      Class<?> next = queue.removeFirst();
      result.add(next);

      for (Class<?> iFace : next.getInterfaces()) {
        if (!scheduled.contains(iFace)) {
          queue.add(iFace);
          scheduled.add(iFace);
        }
      }

      if (next.getSuperclass() != null && !scheduled.contains(next.getSuperclass())) {
        queue.add(next.getSuperclass());
        scheduled.add(next.getSuperclass());
      }

    }

    return result;
  }

}
