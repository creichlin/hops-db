hops-db
=======

A very thin db layer on top of jdbc.

Uses the same structure like jdbc and adds functions to reduce boilerplate code.

Why?
----

Many Object relational mapping/JPA implementations are very powerful and complex and in some cases tricky. They have come a long way
in the last 10 years but sometimes i still like to have a simple and thin layer on top of JDBC to make live a bit easier.

Basic examples
--------------

This is an annotation based sample that uses a single bean.

    @Table
    public class Simple {
      @Column(key = true)
      private int id = -1;
      
      @Column
      private String value;
      
      // getters and setters and constructors
    }

Creating a Db object which provides functions, similar to a jdbc connection.
AnnotationModelProvider reads annotations from the beans and provides those infomations
to the Db instance.

    AnnotationModelProvider models = new AnnotationModelProvider();
    Db db = new Db("jdbc:h2:mem:name;USER=test;PASSWORD=test", models);

Create and save instance:

    Simple simple = new Simple();
    simple.setValue("foooo");
    db.create(simple);

Find instance with id 1:
 
    Simple simple = db.select(Simple.class, 1);

Update instance:

    Simple simple = db.select(Simple.class, 1);
    simple.setValue("bar");
    db.update();

Delete instance:

    Simple simple = db.select(Simple.class, 1);
    db.selete(simple);







