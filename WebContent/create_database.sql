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
CREATE DATABASE parking_db CHARACTER SET utf8 COLLATE utf8_general_ci;;
USE parking_db;

/*======== CREATING THE TABLES  ========*/

/*==== users ====*/
CREATE TABLE IF NOT EXISTS users
(
	id INT NOT NULL AUTO_INCREMENT,
	type ENUM('customer','police', 'basestation') NOT NULL DEFAULT 'customer',
	username VARCHAR(100),/*username and password are only used in the case of basestation and police.*/
	password VARCHAR(100),
	fname VARCHAR(100),
	lname VARCHAR(100),
	cellphone VARCHAR(20),
	email_addr VARCHAR(100),
	profile_image VARCHAR(100),
	ads_flag TINYINT,
	PRIMARY KEY(id)
) ENGINE=INNODB;

/*==== cars ====*/
CREATE TABLE IF NOT EXISTS cars
(
	id INT NOT NULL AUTO_INCREMENT,
	customer_id INT NOT NULL,
	make_model VARCHAR(50) NOT NULL,
	color TINYINT NOT NULL,
	plate_number VARCHAR(50) NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(customer_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=INNODB;

/*==== Pricing ====*/
CREATE TABLE IF NOT EXISTS price_rates
(
	id INT NOT NULL AUTO_INCREMENT,
	pricing VARCHAR(500) NOT NULL,/*the format of this field is a JSON array of objects like {int, int, int}*/
	PRIMARY KEY(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS working_hours
(
	id INT NOT NULL AUTO_INCREMENT,
	start_time TIME NOT NULL,
	end_time TIME NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB;


/*==== creating parking structural tables ====*/
CREATE TABLE IF NOT EXISTS cities
(
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS sectors
(
	id INT NOT NULL AUTO_INCREMENT,
	capacity INT NOT NULL,
	city_id INT NOT NULL,
	rep_x DOUBLE NOT NULL,
	rep_y DOUBLE NOT NULL,
	price_rates_id INT,
	working_hour_id INT,
	PRIMARY KEY(id),
	FOREIGN KEY(city_id) REFERENCES cities(id),
	FOREIGN KEY(price_rates_id) REFERENCES price_rates(id),
	FOREIGN KEY(working_hour_id) REFERENCES working_hours(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS sector_segments
(
	id INT NOT NULL AUTO_INCREMENT,
	sector_id INT NOT NULL,
	capacity INT NOT NULL,
	start_x DOUBLE NOT NULL,
	start_y DOUBLE NOT NULL,
	end_x DOUBLE NOT NULL,
	end_y DOUBLE NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS sensors
(
	id INT NOT NULL AUTO_INCREMENT,
	city_id INT NOT NULL,
	full_flag TINYINT NOT NULL,
	last_changed time NOT NULL,
	last_updated time NOT NULL,
	PRIMARY KEY(id),
 	FOREIGN KEY(city_id) REFERENCES cities(id) ON DELETE CASCADE
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS spots
(
	sector_id INT NOT NULL,
	local_spot_id INT NOT NULL,
	sensor_id INT NOT NULL,
	FOREIGN KEY(sensor_id) REFERENCES sensors(id) ON DELETE CASCADE
) ENGINE=INNODB;

/*==== Transactions and Reservations ====*/
CREATE TABLE IF NOT EXISTS customer_wallets
(
        id INT NOT NULL,
        customer_id INT NOT NULL,
        balance INT NOT NULL DEFAULT 0,
        FOREIGN KEY(customer_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS reservations
(
	id int NOT NULL AUTO_INCREMENT,
	type ENUM('none', 'localSpotId','sectorId', 'sensorId') NOT NULL DEFAULT 'none',
	location_id INT,/*local_spot_id or sector_id or sensor_id*/
	car_id INT,/*car_id in the cases where the reservation determines the car.*/
	start_time TIME NOT NULL,
	time_length INT NOT NULL,/*how many 30 minutes?*/
	PRIMARY KEY(id),
	FOREIGN KEY(car_id) REFERENCES cars(id) ON DELETE CASCADE
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS transactions
(
    id INT NOT NULL AUTO_INCREMENT,
    type ENUM('topUp','paymentByWallet', 'paymentByRFCARD') NOT NULL DEFAULT 'paymentByWallet',
    payer_id VARCHAR(100) NOT NULL,/*either contains a string of wallet_id or RFID of the payment*/
    reservation_id INT,
    price INT NOT NULL,	
    transaction_date DATE NOT NULL,
    transaction_time TIME NOT NULL,
    description VARCHAR(100) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
) ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS logs
(
	id INT NOT NULL AUTO_INCREMENT,
	log_group VARCHAR(100) NOT NULL,
	log TEXT NOT NULL,
	time_ time DEFAULT NULL,
	date_ date DEFAULT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB;



/* Test Data */
INSERT INTO users(type, username, password) VALUE ("basestation", "basestation", MD5("basestationpassword"));
INSERT INTO cities(name) VALUE ("تهران");
INSERT INTO cities(name) VALUE ("شیراز");
INSERT INTO cities(name) VALUE ("بابلسر");
INSERT INTO cities(name) VALUE ("مشهد");



