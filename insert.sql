insert into Price values('PIT','JFK',250,120);
insert into Price values('JFK','PIT',250,120);
insert into Price values('JFK','DCA',220,100);
insert into Price values('DCA','JFK',210,90);
insert into Price values('PIT','DCA',200,150);
insert into Price values('DCA','PIT',200,150);

insert into Plane values('B737','Boeing',125,to_date('09/09/2009','MM/DD/YYYY'),1996);
insert into Plane values('A320','Airbus',160,to_date('10/01/2011','MM/DD/YYYY'),2001);
insert into Plane values('E145','Embraer',50,to_date('06/15/2010','MM/DD/YYYY'),2008);

--insert into Plane values('B737','Boeing',1,to_date('09/09/2009','MM/DD/YYYY'),1996);
--insert into Plane values('A320','Airbus',2,to_date('10/01/2011','MM/DD/YYYY'),2001);
--insert into Plane values('E145','Embraer',1,to_date('06/15/2010','MM/DD/YYYY'),2008);

insert into Flight values('153','A320','PIT','JFK','1000','1120','SMTWTFS');
insert into Flight values('154','B737','JFK','DCA','1230','1320','S-TW-FS');
--insert into Flight values('552','E145','PIT','DCA','1100','1150','SM-WT-S');

insert into Customer values('111111111','Mr.','Ryan','Ulanowicz','4567890123456789',to_date('09/01/2016','MM/DD/YYYY'),'310 Walnut St.','Pittsburgh','PA','4128288439','rulanowicz@gmail.com');
insert into Customer values('111111112','Mr.','Zach','Liss','4123890123456789',to_date('09/01/2016','MM/DD/YYYY'),'310 Walnut St.','Pittsburgh','PA','4128288439','zachliss08@gmail.com');


insert into Reservation values('22222','111111111',0,to_date('11/14/2013','MM/DD/YYYY'),'0');
insert into Reservation values('11111','111111112',0,to_date('12/2/2013','MM/DD/YYYY'),'1');

insert into Reservation_detail values('22222','153',to_date('11/14/2013','MM/DD/YYYY'),1);
insert into Reservation_detail values('11111','153',to_date('11/14/2013','MM/DD/YYYY'),1);

insert into CDate values(to_date('11/14/2013 0835', 'MM/DD/YYYY HH24MI'));
--insert into CDate values(to_date('11/14/2013 08:35:00', 'MM/DD/YYYY HH:MI:SS'));

--insert into Reservation_detail values('11111','153',to_date('11/14/2013','MM/DD/YYYY'),1);

commit;