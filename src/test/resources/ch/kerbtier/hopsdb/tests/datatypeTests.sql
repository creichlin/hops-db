CREATE TABLE datatypes (
  id integer primary key auto_increment,
  `date` date,
  `time` time,
  `date_time` datetime,
  
  int_int integer,
  int_small smallint,

  long_big bigint,
  long_int int,
  long_small smallint,
  
  float_real real,
  double_double double,
  
  byte_blob blob,
  
  boolean_boolean boolean,
);