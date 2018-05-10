drop table if exists ExtraCharges;
drop table if exists ChargeTypes;
drop table if exists Bills;
drop table if exists Reservations;
drop table if exists Customers;
drop table if exists RoomPrices;
drop table if exists Employees;
drop table if exists Rooms; 
drop table if exists BedInfo; 

create table BedInfo(
    ID integer primary key,
    NumBed integer not null,
    BedType text not null
);

create table Rooms(
    RoomNum integer primary key,
    RoomView text not null,
    BedID integer references BedInfo (ID),
    check (RoomNum % 100 <= 12),
    check (RoomNum < 600)
);

create table Employees(
    Login text primary key,
    Pwd text not null,
    Name text not null,
    Email text,
    Phone text,
    Admin boolean default false
);

create table RoomPrices(
    ID serial primary key,
    RoomNum integer references Rooms (RoomNum),
    Price decimal,
    Date date
);

create table Customers(
    Login text not null primary key,
    Pwd text not null,
    FName text not null,
    LName text not null,
    Email text not null,
    Address text not null,
    CCN text not null,
    ExpDate date,
    CRCCode integer not null
);

create table Reservations(
    RID serial primary key,
    CustLogin text references Customers,
    CheckIn date not null,
    CheckOut date not null,
    RoomNum integer references Rooms,
    ActualCheckOut date
);

create table Bills(
    ID serial primary key,
    ResID integer references Reservations,
    CustLogin text references Customers,
    Reason text,
    RoomPrice decimal check (RoomPrice > 0),
    ExtraPrice decimal check (RoomPrice > 0),
    TotalPrice decimal check (TotalPrice = RoomPrice + ExtraPrice),
    Date date
);

create table ChargeTypes(
    ID serial primary key,
    Name text not null,
    Cost decimal not null
);

create table ExtraCharges(
    ID serial primary key,
    ResID integer references Reservations,
    ChargeID integer references ChargeTypes
);