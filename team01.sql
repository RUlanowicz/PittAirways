-- Ryan Ulanowicz
-- Zach Liss
-- Group One
-- Term Project
-- CS 1555

DROP TABLE our_sys_time;
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

CREATE TABLE our_sys_time(
	c_date				date,
	CONSTRAINT Date_PK PRIMARY KEY (c_date)
);

--return the price that needs to be used
CREATE OR REPLACE FUNCTION priceType(resNum in varchar)
RETURN varchar
AS
num_legs int;
dep_date date;
arv_date date;
BEGIN
SELECT count(*) into num_legs
FROM Reservation_detail
WHERE resNum = reservation_number;

IF num_legs = 1 THEN
		return 'high';
ELSIF num_legs > 1 THEN
	
	select flight_date into dep_date
	from Reservation_detail
	where leg = 1;
	
	select flight_date into arv_date
	from Reservation_detail
	where leg = num_legs;

	dbms_output.put_line(dep_date);
	dbms_output.put_line(arv_date);
	IF dep_date = arv_date THEN
		return 'high';
	ELSE 
		return 'low';
	END IF;
END IF;

END;
/

--set the cost of a reservation
CREATE OR REPLACE PROCEDURE setCost (resNum in varchar)
IS
total_cost int;
num_legs int;
current_cost int;
cost_type varchar(4);
dep_date date;
arv_date date;
dep_city varchar(3);
arv_city varchar(3);
cur_flight varchar(3);
BEGIN
total_cost := 0;
SELECT count(*) into num_legs
FROM Reservation_detail
WHERE resNum = reservation_number;

cost_type := priceType(resNum);

dbms_output.put_line('num_legs: ' || num_legs);
FOR i in 1..num_legs LOOP
	dbms_output.put_line('i = ' || i);
	SELECT flight_number into cur_flight
	FROM Reservation_detail
	WHERE leg = i AND resNum = reservation_number;
	SELECT arrival_city, departure_city into arv_city, dep_city
	FROM Flight
	WHERE flight_number = cur_flight;
	if cost_type = 'high' then
		select high_price into current_cost
		from Price
		where arv_city = arrival_city AND dep_city = departure_city;
	else
		select low_price into current_cost
		from Price
		where arv_city = arrival_city AND dep_city = departure_city;
	end if;
	total_cost := total_cost + current_cost;
	dbms_output.put_line('total_cost: ' || total_cost);
END LOOP;
UPDATE Reservation
SET Cost = total_cost
WHERE resNum = reservation_number;
END;
/

--adjust price of reservation that has not been ticketed when a price is updated
CREATE OR REPLACE TRIGGER adjustTicket
AFTER UPDATE ON Price
FOR EACH ROW
DECLARE
res varchar(5);
price_type varchar(4);
high_dif int;
low_dif int;
new_cost int;

CURSOR resCursor IS SELECT *
					FROM Reservation NATURAL JOIN (SELECT reservation_number
 	  								  			   FROM Flight NATURAL JOIN Reservation_detail
   									  			   WHERE :new.arrival_city = arrival_city AND :new.departure_city = departure_city);
BEGIN
new_cost := 0;
high_dif := :new.high_price - :old.high_price;
low_dif := :new.low_price - :old.low_price;

FOR reservation_rec in resCursor
LOOP
	price_type := priceType(reservation_rec.reservation_number);
	dbms_output.put_line(price_type);

	IF reservation_rec.ticketed = '1' THEN
		continue;
	ELSIF price_type = 'low' THEN
		-- set reservation cost += low_dif
		new_cost := reservation_rec.cost + low_dif;
		UPDATE Reservation SET cost=new_cost WHERE reservation_rec.reservation_number = reservation_number;  
	ELSIF price_type = 'high' THEN
		-- set reservation cost += high_dif
		new_cost := reservation_rec.cost + high_dif;
		UPDATE Reservation SET cost=new_cost WHERE reservation_rec.reservation_number = reservation_number; 
	END IF;
END LOOP;
END;
/

--set the plane for a flight to be the next largest plane
CREATE OR REPLACE FUNCTION enlargePlane (flight_num in varchar)
RETURN int
AS
start_type char(4);
rank number;
new_type char(4);
search_status varchar(11);
CURSOR planeCursor is SELECT plane_type FROM Plane ORDER BY plane_capacity ASC;
BEGIN
select plane_type into start_type
from Flight
where flight_num = flight_number;
FOR plane_rec in planeCursor
LOOP
--dbms_output.put_line(plane_rec.plane_type);
IF plane_rec.plane_type = start_type THEN
	search_status := 'plane found';
ELSIF search_status = 'plane found' THEN
	UPDATE Flight SET plane_type = plane_rec.plane_type WHERE flight_num = flight_number;
	return 1;
END IF;
END LOOP;
--this insert should cause the trigger to fail and therefore undo the reservation
INSERT INTO Plane(plane_type) VALUES(start_type);
return 0;
END;
/

--return the number of free seats on a flight
CREATE OR REPLACE FUNCTION freeSeats(flight_num in varchar)
RETURN int
AS
cap int;
cur_size int;
BEGIN
SELECT plane_capacity into cap FROM Plane NATURAL JOIN Flight WHERE flight_number = flight_num;
SELECT COUNT(*) INTO cur_size FROM Reservation_detail WHERE flight_number = flight_num;
return cap - cur_size;
END;
/

--upgrade the size of a flight if it is full and an insert is being made on it
CREATE OR REPLACE TRIGGER planeUpgrade
BEFORE INSERT ON Reservation_detail
FOR EACH ROW
DECLARE
enlargeResult int;
BEGIN 

dbms_output.put_line(:new.flight_number);
dbms_output.put_line(freeSeats(:new.flight_number));
IF freeSeats(:new.flight_number) = 0 THEN
	enlargeResult := enlargePlane(:new.flight_number);
END IF;
END;
/

--check if a flight flys only today, only tomorrow, both or neither
CREATE OR REPLACE FUNCTION checkDay(schedule in varchar, day in int)
RETURN int
AS
d1 char;
d2 char;
BEGIN
d1 := substr(schedule, day, 1);
d2 := substr(schedule, day+1, 1);
IF d1 = '-' AND d2 = '-' THEN --not today or tomorrow
	return 0;
ELSIF d2 = '-' THEN --only today
	return 1;
ELSIF d1 = '-' THEN --only tomorrow
	return 2;
ELSE 	            --both days
	return 3;
END IF;
END;
/

--cancel all non ticketed reservations that use a particular flight
CREATE OR REPLACE PROCEDURE cancelFlightReservations(flight_num in varchar)
IS
CURSOR resCursor IS SELECT DISTINCT reservation_number FROM Reservation_detail WHERE flight_number = flight_num;
tick char;
BEGIN
FOR res_rec IN resCursor
LOOP
	dbms_output.put_line('cancelling reservation for: ' || flight_num);
	SELECT ticketed into tick FROM Reservation WHERE reservation_number = res_rec.reservation_number;

	IF tick = '0' THEN
		dbms_output.put_line('deleting reservation: ' || res_rec.reservation_number);
		DELETE FROM Reservation_detail WHERE reservation_number = res_rec.reservation_number;
		DELETE FROM Reservation WHERE reservation_number = res_rec.reservation_number;
	END IF;
END LOOP;
END;
/

--try to move a flight to a smaller sized plane
CREATE OR REPLACE PROCEDURE attemptDownSize(flight_num in varchar)
AS
start_type char(4);
cap int;
rank number;
new_type char(4);
search_status varchar(11);
CURSOR planeCursor is SELECT plane_type, plane_capacity FROM Plane ORDER BY plane_capacity DESC;
BEGIN
select plane_type into start_type from Flight where flight_num = flight_number;
select plane_capacity into cap from Plane where plane_type = start_type;
FOR plane_rec in planeCursor
LOOP
IF plane_rec.plane_type = start_type THEN
	search_status := 'plane found';
ELSIF search_status = 'plane found' THEN
	IF (cap - freeSeats(flight_num) <= plane_rec.plane_capacity) THEN
		UPDATE Flight SET plane_type = plane_rec.plane_type WHERE flight_num = flight_number;
		cap := plane_rec.plane_capacity;
	END IF;
END IF;
END LOOP;
END;
/

--cancel all non-ticketed reservations 12 hours before one of their flights
--if the number of passengers will fit in a smaller plane, downsize it
CREATE OR REPLACE TRIGGER cancelReservation
AFTER UPDATE ON our_sys_time
FOR EACH ROW
DECLARE
r int;
day int;
schedule varchar(7);
build_date date;
date_dif float;
date_dif2 float;
CURSOR flightCursor IS SELECT flight_number, departure_time, weekly_schedule FROM Flight;
BEGIN
day := to_char(:new.c_date, 'D');

FOR flight_rec in flightCursor
LOOP
	r := checkDay(flight_rec.weekly_schedule, day);
	IF r = 0 THEN
		continue;
	ELSE
		build_date := to_date(to_char(:new.c_date, 'MM/DD/YYYY') || ' ' || flight_rec.departure_time, 'MM/DD/YYYY HH24MI');
	END IF;

	date_dif := build_date - :new.c_date;
	date_dif2 := date_dif + 1;

	IF r = 1 THEN
		IF date_dif <= .5 AND date_dif > 0 THEN
			cancelFlightReservations(flight_rec.flight_number);
			attemptDownSize(flight_rec.flight_number);
		END IF;
	ELSIF r = 2 THEN
		IF date_dif2 <= .5 AND date_dif2 > 0  THEN
			cancelFlightReservations(flight_rec.flight_number);
			attemptDownSize(flight_rec.flight_number);
		END IF;
	ELSIF r = 3 THEN
		IF (date_dif <= .5 AND date_dif > 0) OR (date_dif2 <= .5 AND date_dif2 > 0) THEN
			cancelFlightReservations(flight_rec.flight_number);
			attemptDownSize(flight_rec.flight_number);
		END IF;
	END IF;
END LOOP;
END;
/

commit;
