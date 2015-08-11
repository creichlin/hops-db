package ch.kerbtier.hopsdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.kerbtier.hopsdb.model.ModelProvider;

public class DbSelection<T> {
  private Class<T> type;
  private String where = null;
  private int page = 0;
  private int pageSize = 20;
  private ModelProvider models;

  public DbSelection(Class<T> type, ModelProvider models) {
    this.type = type;
    this.models = models;
  }

  private String createQuery() {
    StringBuilder sb = new StringBuilder();
    sb.append("from `");
    sb.append(models.getModel(type).getName());
    sb.append("` ");
    if (where != null) {
      sb.append("where ");
      sb.append(where);
      sb.append(" ");
    }
    return sb.toString();
  }

  public List<T> list(Db db) throws SQLException {
    DbPs ps = db.prepareStatement("select * " + createQuery() + "limit ?,?");
    ps.setInt(1, page * pageSize);
    ps.setInt(2, pageSize);
    List<T> list = ps.select(type);
    return list;
  }

  public int maxPage(Db db) throws SQLException {
    DbPs ps = db.prepareStatement("select count(*) " + createQuery());
    DbRs rs = ps.executeQuery();
    rs.next();
    int mp = rs.getInt(1);

    if (mp % pageSize > 0) {
      mp = mp / pageSize + 1;
    } else {
      mp = mp / pageSize;
    }

    return mp;
  }

  public List<Page> pages(Db db) throws SQLException {
    int min = page - 10;
    int max = page + 10;

    if (min < 0) {
      min = 0;
    }

    int maxPage = maxPage(db);

    if (max > maxPage) {
      max = maxPage;
    }

    List<Page> ints = new ArrayList<>();

    for (int cnt = min; cnt < max; cnt++) {
      ints.add(new Page(cnt, cnt == page));
    }

    return ints;
  }
  
  public Page previous() {
    if(page > 0) {
      return new Page(page - 1, false);
    }
    return null;
  }

  public Page next(Db db) throws SQLException {
    if(page < maxPage(db) - 1) {
      return new Page(page + 1, false);
    }
    return null;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }
  
  public class Page {
    private int number;
    private boolean active;
    
    public Page(int number, boolean active) {
      this.number = number;
      this.active = active;
    }

    public int getNumber() {
      return number;
    }

    public boolean isActive() {
      return active;
    }
  }
}
