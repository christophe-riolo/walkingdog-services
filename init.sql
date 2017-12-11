CREATE TABLE IF NOT EXISTS Devices (
	email VARCHAR(100),
	device_id VARCHAR(50),
	device_type VARCHAR(100),
	PRIMARY KEY(email, device_id)
);

CREATE TABLE IF NOT EXISTS Dogs (
	uuid VARCHAR(50),
	breed VARCHAR(100),
	name VARCHAR(100),
	picture VARCHAR(100),
	rate FLOAT,
	PRIMARY KEY(uuid)
);

CREATE TABLE IF NOT EXISTS Userdog (
	email VARCHAR(100),
	dog_id VARCHAR(50),
	FOREIGN KEY(email) REFERENCES Devices(email),
	FOREIGN KEY(dog_id) REFERENCES Dogs(uuid),
	UNIQUE(email, dog_id)
);
