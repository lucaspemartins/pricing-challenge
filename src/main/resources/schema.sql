create table farm
(
   idFarm integer not null,
   farmName varchar(255) not null,
   ownerName varchar(255) not null,
   paymentAmount double,
   primary key(idFarm)
);

create table coordinate
(
   idCoordinate integer not null,
   latitute double not null,
   longitude double not null,
   coordinateType varchar(40) not null,
   idFarm integer not null,
   primary key(idCoordinate),
   foreign key(idFarm) references farm(idFarm)
);

create table service
(
   idService integer not null,
   serviceName varchar(255) not null,
   idCoordinate integer not null,
   primary key(idService),
   foreign key(idCoordinate) references coordinate(idCoordinate)
);