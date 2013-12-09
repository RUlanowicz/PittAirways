-- We assumed that Date had to be renamed to CDate due to Date being a system reserved keyword
-- Ryan Ulanowicz
-- Zach Liss
-- Group One
-- Term Project
-- CS 1555

DROP TABLE CDate;
DROP TABLE Reservation_detail;
DROP TABLE Reservation;
DROP TABLE Flight;
DROP TABLE Price;
DROP TABLE Plane;
DROP TABLE Customer;

CREATE TABLE Plane(
	plane_type 			varchar(4),
	manufacture 		varchar(10),
	plane_capacity 		int,
	last_service 		date,
	year 				int,
	CONSTRAINT Plane_PK PRIMARY KEY (plane_type)
);

CREATE TABLE Flight(
	flight_number 		varchar(3),
	plane_type 			varchar(4),
	departure_city 		varchar(3),
	arrival_city 		varchar(3),
	departure_time 		varchar(4),
	arrival_time 		varchar(4),
	weekly_schedule		varchar(7),
	CONSTRAINT Flight_PK PRIMARY KEY (flight_number),
	CONSTRAINT Plane_Type_FK FOREIGN KEY (plane_type) REFERENCES Plane(plane_type)
);

CREATE TABLE Price(
	departure_city 		varchar(4),
	arrival_city		varchar(4),
	high_price			int,
	low_price			int,
	CONSTRAINT Price_PK PRIMARY KEY (departure_city,arrival_city)
);

CREATE TABLE Customer(
	cid					varchar(9),
	salutation			varchar(3),
	first_name			varchar(30),
	last_name			varchar(30),
	credit_card_num		varchar(16),
	credit_card_expire	date,
	street				varchar(30),
	city				varchar(30),
	state				varchar(2),
	phone				varchar(10),
	email				varchar(30),
	CONSTRAINT Customer_PK PRIMARY KEY (cid)
);

CREATE TABLE Reservation(
	reservation_number	varchar(5),
	cid					varchar(9),
	cost				int,
	reservation_date	date,
	ticketed			varchar(1),
	CONSTRAINT Reservation_PK PRIMARY KEY (reservation_number),
	CONSTRAINT Cid_FK FOREIGN KEY (cid) REFERENCES Customer(cid)
);

CREATE TABLE Reservation_detail(
	reservation_number	varchar(5),
	flight_number		varchar(3),
	flight_date			date,
	leg					int,
	CONSTRAINT Reservation_Detail_PK PRIMARY KEY (reservation_number,leg),
	CONSTRAINT Flight_Number_FK FOREIGN KEY (flight_number) REFERENCES Flight(flight_number),
	CONSTRAINT Reservation_Number_FK FOREIGN KEY (reservation_number) REFERENCES Reservation(reservation_number)
);

CREATE TABLE CDate(
	c_date				date,
	CONSTRAINT Date_PK PRIMARY KEY (c_date)
);