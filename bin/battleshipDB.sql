USE Battleship;

CREATE TABLE login (
	username			VARCHAR(45)			NOT NULL,
	password			VARCHAR(45)			NOT NULL,
	PRIMARY KEY (username)
);

CREATE UNIQUE INDEX username_UNIQUE
ON login (username);


INSERT INTO login (username, password)
	VALUES 	('tropicalCurve', 'mississippi'),
			('jdz1214', 'Mercedes789!'),
			('dhz1219', 'Whistle1');