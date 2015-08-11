CREATE TABLE inheritance_parent (
  identifier integer primary key auto_increment,
  value1 text
);

CREATE TABLE inheritance_child (
  identifier integer primary key auto_increment,
  value1 text,
  value2 text
);