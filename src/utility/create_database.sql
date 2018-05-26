/*======== CREATING THE USER AND ADDING THE PRIVILEGES ========*/

/*
Log into the root user in mysql and create the admin user
mysql -u root -p
*/
CREATE USER IF NOT EXISTS 'parking_admin'@'localhost' IDENTIFIED BY 'parkingadminpassword';

GRANT ALL PRIVILEGES ON *.* TO 'parking_admin'@'localhost' WITH GRANT OPTION;

FLUSH PRIVILEGES;

/*
Log out of the root user and login using the admin credentials
mysql -u parking_admin -p
*/

/*======== CREATING THE DATABASE ========*/
CREATE DATABASE parking_system;
USE parking_system;

/*======== CREATING THE TABLES  ========*/

/*==== customers ====*/
CREATE TABLE IF NOT EXISTS customers
(
	id INT NOT NULL AUTO_INCREMENT,
	fname VARCHAR(100) NOT NULL,
	lname VARCHAR(100) NOT NULL,
	cellphone VARCHAR(20) NOT NULL,
	email_addr VARCHAR(100) NOT NULL,
	city VARCHAR(50) NOT NULL,
	ads_flag TINYINT NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB;



/*==== cars ====*/
CREATE TABLE IF NOT EXISTS cars
(
	id INT NOT NULL AUTO_INCREMENT,
	customer_id INT NOT NULL,
	make_model VARCHAR(50) NOT NULL,
	color TINYINT NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(customer_id) REFERENCES customers(id) ON DELETE CASCADE
) ENGINE=INNODB;


/*==== creating parking structural tables ====*/
CREATE TABLE IF NOT EXISTS cities
(
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS streets
(
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	city_id INT NOT NULL,
	start_x DOUBLE NOT NULL,
	start_y DOUBLE NOT NULL,
	end_x DOUBLE NOT NULL,
	end_y DOUBLE NOT NULL,
	rep_x DOUBLE NOT NULL,
	rep_y DOUBLE NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(city_id) REFERENCES cities(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS parking_spots
(
	id INT NOT NULL AUTO_INCREMENT,
	base_station_id TINYINT NOT NULL,
	parko_meter_id TINYINT NOT NULL,
	sesor_id TINYINT NOT NULL,
	street_id INT NOT NULL,
	city_id INT NOT NULL,
	status ENUM('FULL','EMPTY') NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(street_id) REFERENCES streets(id) ON DELETE CASCADE,
	FOREIGN KEY(city_id) REFERENCES cities(id) ON DELETE CASCADE
) ENGINE=INNODB;

/*==== Pricing ====*/
CREATE TABLE IF NOT EXISTS price_rates
(
	id INT NOT NULL AUTO_INCREMENT,
	description VARCHAR(100) NOT NULL,
	price SMALLINT NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS available_rates
(
	street_id INT NOT NULL,
	price_rate_id INT NOT NULL,
	FOREIGN KEY(street_id) REFERENCES streets(id) ON DELETE CASCADE,
	FOREIGN KEY(price_rate_id) REFERENCES price_rates(id) ON DELETE CASCADE
) ENGINE=INNODB;

/*==== Park and Transactions ====*/
CREATE TABLE IF NOT EXISTS park_transactions
(
	id INT NOT NULL AUTO_INCREMENT,
	status ENUM('open','close') NOT NULL DEFAULT 'open',
	/*From spot_id and car_id at least one of them must have value. They cannot be null both together.*/
	spot_id INT NULL,
	car_id INT NULL,
	price_rate_id INT NOT NULL,
	fee SMALLINT NOT NULL,
	start_time TIME NOT NULL,
	time_length SMALLINT NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(spot_id) REFERENCES parking_spots(id) ON DELETE CASCADE,
	FOREIGN KEY(car_id) REFERENCES cars(id) ON DELETE CASCADE,
	FOREIGN KEY(price_rate_id) REFERENCES price_rates(id) ON DELETE CASCADE
) ENGINE=INNODB;

/*==== Transactions ====*/
CREATE TABLE IF NOT EXISTS customer_wallet
(
        customer_id INT NOT NULL,
        balance INT NOT NULL DEFAULT 0,
        FOREIGN KEY(customer_id) REFERENCES customers(id) ON DELETE CASCADE
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS customer_transactions
(
        id INT NOT NULL AUTO_INCREMENT,
        type ENUM('park','deposit') NOT NULL DEFAULT 'park',
        deposit_amount SMALLINT NULL,
        park_id INT NULL,
        PRIMARY KEY(id),
	/*park_id can be null in the case where the type is 'deposit'*/
        FOREIGN KEY(park_id) REFERENCES park_transactions(id) ON DELETE CASCADE
) ENGINE=INNODB;

/* Test Data */


INSERT INTO cities (name) VALUE ('Tehran');
INSERT INTO streets (name, city_id, start_x, start_y, end_x, end_y, rep_x, rep_y) VALUE ('Chamran', 1, 0, 0, 10, 10, 5, 5);
INSERT INTO streets (name, city_id, start_x, start_y, end_x, end_y, rep_x, rep_y) VALUE ('Zargari', 1, 10, 10, 110, 110, 10, 15);
INSERT INTO streets (name, city_id, start_x, start_y, end_x, end_y, rep_x, rep_y) VALUE ('Shahid Ghoddoosi', 1, 100, 100, 1100, 1100, 100, 150);
INSERT INTO streets (name, city_id, start_x, start_y, end_x, end_y, rep_x, rep_y) VALUE ('Shahid Taheriwq', 1, 500, 500, 300, 100, 200, 190);
