Kevin Crimi
9872642000
crimi

----Contents-----

Populate.java - Populates data from building.xy, hydrant.xy, and firebuilding.txt into the database

Hw2.java - Main application class containing GUI

DBConnect.java - class used for interacting with the database

Building.java - helper class to make objects of buildings

ImagePanel.java - custom class extending JPanel which contains the paint and coordinate logic involvedin drawing

createdb.sql - used to create tables, insert metadata, and createindices

dropdb.sql - used to drop the tables and delete metadata created in createdb.sql


-----Compiling Instructions------

javac -classpath "bin/sdoapi.jar;bin/ojdbc6.jar" *java

java -classpath ".;bin/sdoapi.jar;bin/ojdbc6.jar" Populate bin/building.xy bin/hydrant.xy bin/firebuilding.txt

java -classpath ".;bin/sdoapi.jar;bin/ojdbc6.jar" Hw2

-----Video Demonstration-------

http://youtu.be/48rsV9Hs30s