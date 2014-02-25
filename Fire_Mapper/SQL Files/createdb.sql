set define off
--CREATE TABLES--
CREATE TABLE Hydrants (
hydIndex VARCHAR(4) PRIMARY KEY,
point MDSYS.SDO_GEOMETRY
);

CREATE TABLE Buildings (
bldIndex VARCHAR(4) PRIMARY KEY,
bldCode VARCHAR(35),
fire CHAR(1),
vertices NUMBER(3,0),
shape MDSYS.SDO_GEOMETRY
);


--UPDATE METADA--
INSERT INTO user_sdo_geom_metadata VALUES(
'Hydrants',
'point',
SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 820, 0.005), SDO_DIM_ELEMENT('Y', 0, 520, 0.005)),
NULL
);

INSERT INTO user_sdo_geom_metadata VALUES(
'Buildings',
'shape',
SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 820, 0.005), SDO_DIM_ELEMENT('Y', 0, 520, 0.005)),
NULL
);

--Create Spatial Indices--
CREATE INDEX hydrant_index ON Hydrants(point)
INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX building_index ON Buildings(shape)
INDEXTYPE IS MDSYS.SPATIAL_INDEX;




