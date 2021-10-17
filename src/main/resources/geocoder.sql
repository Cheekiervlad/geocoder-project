DROP TABLE IF EXISTS location;
CREATE TABLE location
(
    l_id                SERIAL,
    l_formatted_address varchar(100)     not null,
    l_longitude         double precision not null,
    l_latitude          double precision not null
)