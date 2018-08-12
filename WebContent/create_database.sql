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
	profile_image VARCHAR(100) NOT NULL,
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
	plate_number VARCHAR(50) NOT NULL,
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

CREATE TABLE IF NOT EXISTS sectors
(
	id INT NOT NULL AUTO_INCREMENT,
	city_id INT NOT NULL,
	rep_x DOUBLE NOT NULL,
	rep_y DOUBLE NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(city_id) REFERENCES cities(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS sector_segments
(
	id INT NOT NULL AUTO_INCREMENT,
	sector_id INT NOT NULL,
	start_x DOUBLE NOT NULL,
	start_y DOUBLE NOT NULL,
	end_x DOUBLE NOT NULL,
	end_y DOUBLE NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(sector_id) REFERENCES sectors(id)
) ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS parking_spots
(
	id INT NOT NULL AUTO_INCREMENT,
	base_station_id TINYINT NOT NULL,
	parko_meter_id TINYINT NOT NULL,
	sensor_id TINYINT NOT NULL,
	segment_id INT NOT NULL,
	status ENUM('FULL','EMPTY') NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(segment_id) REFERENCES sector_segments(id) ON DELETE CASCADE
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
	sector_id INT NOT NULL,
	price_rate_id INT NOT NULL,
	FOREIGN KEY(sector_id) REFERENCES sectors(id) ON DELETE CASCADE,
	FOREIGN KEY(price_rate_id) REFERENCES price_rates(id) ON DELETE CASCADE
) ENGINE=INNODB;

/*==== Park and Transactions ====*/
CREATE TABLE IF NOT EXISTS park_transactions
(
	id INT NOT NULL AUTO_INCREMENT,
	status ENUM('open','close') NOT NULL DEFAULT 'open',
	/*From spot_id and car_id at least one of them must have value. They cannot be null both together.*/
	sector_id INT NULL,
	segment_id INT NULL,
	spot_id INT NULL,
	customer_id INT NULL,
	car_id INT NULL,
	price_rate_id INT NOT NULL,
	start_time TIME NOT NULL,
	time_length SMALLINT NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(spot_id) REFERENCES parking_spots(id) ON DELETE CASCADE,
	FOREIGN KEY(car_id) REFERENCES cars(id) ON DELETE CASCADE,
	FOREIGN KEY(price_rate_id) REFERENCES price_rates(id) ON DELETE CASCADE
) ENGINE=INNODB;

/*==== Transactions ====*/
CREATE TABLE IF NOT EXISTS customer_wallets
(
        customer_id INT NOT NULL,
        balance INT NOT NULL DEFAULT 0,
        FOREIGN KEY(customer_id) REFERENCES customers(id) ON DELETE CASCADE
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS wallet_transaction
(
		id INT NOT NULL AUTO_INCREMENT,
        customer_id INT NOT NULL,
        top_up INT NOT NULL,
        transaction_date DATE NOT NULL,
        description VARCHAR(100) NOT NULL,
        amount INT NOT NULL DEFAULT 0,
        PRIMARY KEY(id),
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

CREATE TABLE IF NOT EXISTS sensors
(
	id INT NOT NULL AUTO_INCREMENT,
	full_flag TINYINT NOT NULL,
	last_changed time NOT NULL,
	last_updated time NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB;

/* Test Data */


INSERT INTO cities (name) VALUE ('Tehran');
INSERT INTO sectors (city_id, rep_x, rep_y) VALUE (1, 5, 5);
INSERT INTO sectors (city_id, rep_x, rep_y) VALUE (1, 10, 15);
INSERT INTO sectors (city_id, rep_x, rep_y) VALUE (1, 100, 150);
INSERT INTO sectors (city_id, rep_x, rep_y) VALUE (1, 200, 190);
INSERT INTO sector_segments (sector_id, start_x, start_y, end_x, end_y) VALUE (1, 1, 1, 2, 2);
INSERT INTO sector_segments (sector_id, start_x, start_y, end_x, end_y) VALUE (1, 2, 2, 3, 3);
INSERT INTO sector_segments (sector_id, start_x, start_y, end_x, end_y) VALUE (1, 3, 3, 6, 6);
INSERT INTO sector_segments (sector_id, start_x, start_y, end_x, end_y) VALUE (2, 5, 10, 7, 12);
INSERT INTO sector_segments (sector_id, start_x, start_y, end_x, end_y) VALUE (2, 7, 12, 9, 14);
INSERT INTO sector_segments (sector_id, start_x, start_y, end_x, end_y) VALUE (2, 9, 14, 12, 17);
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 1, 1, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 2, 1, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 3, 1, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 4, 2, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 5, 2, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 6, 2, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 7, 3, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 8, 3, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 9, 3, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 10, 4, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 11, 4, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 12, 4, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 13, 5, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 14, 5, 'EMPTY');
INSERT INTO parking_spots (base_station_id, parko_meter_id, sensor_id, segment_id, status) 
VALUE (0, 0, 15, 5, 'EMPTY');

INSERT INTO price_rates (description, price)
VALUE ('cheap', 8000);
INSERT INTO price_rates (description, price)
VALUE ('medium', 1000);
INSERT INTO price_rates (description, price)
VALUE ('expensive', 12000);
INSERT INTO available_rates (sector_id, price_rate_id)
VALUE (1, 1);
INSERT INTO available_rates (sector_id, price_rate_id)
VALUE (1, 2);
INSERT INTO available_rates (sector_id, price_rate_id)
VALUE (2, 1);
INSERT INTO available_rates (sector_id, price_rate_id)
VALUE (2, 3);
INSERT INTO available_rates (sector_id, price_rate_id)
VALUE (3, 2);
INSERT INTO available_rates (sector_id, price_rate_id)
VALUE (4, 3);

INSERT INTO customers (username, password, fname, lname, cellphone, email_addr, ads_flag)
VALUE ('moji', MD5('moji'), 'Mojtaba' , 'Arjomandi', '09171112222', 'moji@moji.com', 1);
INSERT INTO customers (username, password, fname, lname, cellphone, email_addr, ads_flag)
VALUE ('jimi', MD5('jimi'), 'Jamshid' , 'Esmal', '09171112223', 'jimi@jimi.com', 0);

INSERT INTO cars (customer_id, make_model, color)
VALUE (1, 'Pejo 206', 1);
INSERT INTO cars (customer_id, make_model, color)
VALUE (1, 'Pejo 405', 2);
INSERT INTO cars (customer_id, make_model, color)
VALUE (2, 'Jeep', 1);




