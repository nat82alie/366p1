/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  amandacheng
 * Created: May 1, 2018
 */

insert into employees (login, pwd, name, admin) values ('admin', 'admin', 'admin', true); 
insert into employees (login, pwd, name) values ('amanda', 'password', 'amanda'); 
insert into employees (login, pwd, name) values ('natalie', 'password', 'natalie');
insert into employees (login, pwd, name) values ('alex', 'password', 'alex'); 

insert into customers values ('newuser', 'password', 'first', 'last', 'email', 'address', '1234567890', '2018-03-02', 123);
insert into customers values ('l', 'p', 'f', 'l', 'e', 'a', '2', '2020-12-10', 5);
insert into customers values ('login', 'pwd', 'fname', 'lname', 'email', 'address', 'ccn', '2019-12-12', 123);
insert into customers values ('amanda', 'p', 'amanda', 'cheng', 'email', 'address', 'ccn', '2019-12-12', 123);

insert into bedinfo values (1, 1, 'single king');
insert into bedinfo values (2, 1, 'double queen');

insert into rooms values (101, 'ocean', 1);
insert into rooms values (102, 'ocean', 2);
insert into rooms values (103, 'ocean', 1);
insert into rooms values (104, 'ocean', 2);
insert into rooms values (105, 'ocean', 1);
insert into rooms values (106, 'ocean', 2);
insert into rooms values (107, 'pool', 1);
insert into rooms values (108, 'pool', 2);
insert into rooms values (109, 'pool', 1);
insert into rooms values (110, 'pool', 2);
insert into rooms values (111, 'pool', 1);
insert into rooms values (112, 'pool', 2);
insert into rooms values (201, 'ocean', 1);
insert into rooms values (202, 'ocean', 2);
insert into rooms values (203, 'ocean', 1);
insert into rooms values (204, 'ocean', 2);
insert into rooms values (205, 'ocean', 1);
insert into rooms values (206, 'ocean', 2);
insert into rooms values (207, 'pool', 1);
insert into rooms values (208, 'pool', 2);
insert into rooms values (209, 'pool', 1);
insert into rooms values (210, 'pool', 2);
insert into rooms values (211, 'pool', 1);
insert into rooms values (212, 'pool', 2);
insert into rooms values (301, 'ocean', 1);
insert into rooms values (302, 'ocean', 2);
insert into rooms values (303, 'ocean', 1);
insert into rooms values (304, 'ocean', 2);
insert into rooms values (305, 'ocean', 1);
insert into rooms values (306, 'ocean', 2);
insert into rooms values (307, 'pool', 1);
insert into rooms values (308, 'pool', 2);
insert into rooms values (309, 'pool', 1);
insert into rooms values (310, 'pool', 2);
insert into rooms values (311, 'pool', 1);
insert into rooms values (312, 'pool', 2);
insert into rooms values (401, 'ocean', 1);
insert into rooms values (402, 'ocean', 2);
insert into rooms values (403, 'ocean', 1);
insert into rooms values (404, 'ocean', 2);
insert into rooms values (405, 'ocean', 1);
insert into rooms values (406, 'ocean', 2);
insert into rooms values (407, 'pool', 1);
insert into rooms values (408, 'pool', 2);
insert into rooms values (409, 'pool', 1);
insert into rooms values (410, 'pool', 2);
insert into rooms values (411, 'pool', 1);
insert into rooms values (412, 'pool', 2);
insert into rooms values (501, 'ocean', 1);
insert into rooms values (502, 'ocean', 2);
insert into rooms values (503, 'ocean', 1);
insert into rooms values (504, 'ocean', 2);
insert into rooms values (505, 'ocean', 1);
insert into rooms values (506, 'ocean', 2);
insert into rooms values (507, 'pool', 1);
insert into rooms values (508, 'pool', 2);
insert into rooms values (509, 'pool', 1);
insert into rooms values (510, 'pool', 2);
insert into rooms values (511, 'pool', 1);
insert into rooms values (512, 'pool', 2);

insert into reservations (custlogin, checkin, checkout, roomnum) values ('login', '2015-10-12', '2015-10-15', 112);
insert into reservations (custlogin, checkin, checkout, roomnum) values ('amanda', '2015-10-09', '2015-10-10', 201);
insert into reservations (custlogin, checkin, checkout, roomnum) values ('amanda', '2018-04-05', '2018-04-06', 107);