CREATE TABLE composite_key (
  id1 integer,
  id2 integer auto_increment,
  value1 text,
  value2 text,
  value3 text,
  primary key(id1, id2)
);