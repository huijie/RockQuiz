CREATE TABLE userTable (
	userId varchar(20),
	password char(40),
	registrationTime datetime, 
	status char(1), -- u/s: u-user;s-admin
	gender char(1), -- m/f
	email varchar(50),
)

CREATE TABLE userId_history (
	Time datetime,
	Type char(1), 
	content varchar(50)
)

/*
userId_history format:
time --|--- type ------|----- content
		  (t)ake ------|----- quizId
		 (c)reate -----|---- quizName
		(a)chievement -|-- achievename
*/

CREATE TABLE userId_network (
	userId varchar(20),
	status char(1) -- r(request)/u(unconfirmed)/f(confirmed)/i(ignore)
)

CREATE TABLE userId_inbox (
	Time datetime,
	fromUser varchar(20),
	Type char(1), -- c(challenge)/r(friend request)/f(friend confirm)/n(normal text)
	title varchar(50),
	content text,
	ifRead char(1) -- 1(read)/0(unread)
)
