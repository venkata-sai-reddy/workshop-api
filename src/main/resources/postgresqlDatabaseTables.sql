CREATE TABLE user_type ( code serial PRIMARY KEY, name VARCHAR (255) NOT NULL );

CREATE TABLE user_prof ( user_id serial PRIMARY KEY, first_name VARCHAR (100) NOT NULL, last_name VARCHAR (100) NOT NULL, email_id VARCHAR (255) UNIQUE NOT NULL, phn_num VARCHAR (10) NOT NULL, user_type_code INT NOT NULL, registered_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP , FOREIGN KEY (user_type_code) REFERENCES user_type (code) );

CREATE TABLE login_dtl ( user_id INT PRIMARY KEY, email_id VARCHAR (100) UNIQUE NOT NULL, password VARCHAR (100) NOT NULL, temp_password VARCHAR (100), locked BOOLEAN DEFAULT false, un_suc_atmpt NUMERIC (1) DEFAULT 0, last_login TIMESTAMP, FOREIGN KEY (user_id) REFERENCES user_prof (user_id) );

CREATE TABLE session_dtl (session_id serial PRIMARY KEY, user_id INT NOT NULL, session_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, session_end_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP + interval ’15 minutes’, extend_count INT NOT NULL DEFAULT 0, FOREIGN KEY (user_id) REFERENCES user_prof (user_id));

CREATE TABLE venue_dtl (venue_id VARCHAR(10) PRIMARY KEY, name VARCHAR(50) NOT NULL);

CREATE TABLE workshop_dtl ( workshop_id serial PRIMARY KEY, name VARCHAR (100) NOT NULL, description VARCHAR (3000), location VARCHAR (100), capacity NUMERIC (3), enroll_count NUMERIC (3), created_user_id INT NOT NULL, created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, workshop_date DATE NOT NULL, start_time TIMESTAMP NOT NULL, end_time TIMESTAMP NOT NULL, FOREIGN KEY (created_user_id) REFERENCES user_prof (user_id), FOREIGN KEY (location) REFERENCES venue_dtl (venue_id) );

CREATE TABLE status_dtl ( code serial PRIMARY KEY, name VARCHAR (20) UNIQUE NOT NULL );

CREATE TABLE skill_dtl (skill_id serial PRIMARY KEY, name VARCHAR (50) UNIQUE NOT NULL, status_code INT NOT NULL, FOREIGN KEY (status_code) REFERENCES status_dtl(code) );

CREATE TABLE workshop_skill_info ( workshop_id INT , skill_id INT, PRIMARY KEY (workshop_id, skill_id), FOREIGN KEY (workshop_id) REFERENCES workshop_dtl (workshop_id), FOREIGN KEY (skill_id) REFERENCES skill_dtl (skill_id) );

CREATE TABLE workshop_register_dtl ( register_id serial PRIMARY KEY, workshop_id INT NOT NULL, reg_user_id INT NOT NULL, reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (workshop_id) REFERENCES workshop_dtl (workshop_id), FOREIGN KEY (reg_user_id) REFERENCES user_prof (user_id) );

CREATE TABLE workshop_request_dtl (request_id serial PRIMARY KEY, req_user_id INT NOT NULL, req_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, skill_id INT, FOREIGN KEY (req_user_id) REFERENCES user_prof (user_id), FOREIGN KEY (skill_id) REFERENCES skill_dtl (skill_id));

CREATE TABLE user_skill_dtl ( user_id INT , skill_id INT, PRIMARY KEY (user_id, skill_id), FOREIGN KEY (user_id) REFERENCES user_prof (user_id), FOREIGN KEY (skill_id) REFERENCES skill_dtl (skill_id) );

INSERT INTO user_type (name) VALUES ('ADMIN');
INSERT INTO user_type (name) VALUES ('Student');
INSERT INTO user_type (name) VALUES ('Instructor');
INSERT INTO status_dtl (name) VALUES ('Approved');
INSERT INTO status_dtl (name) VALUES ('Rejected');
INSERT INTO status_dtl (name) VALUES ('Requested');
INSERT INTO venue_dtl (venue_id, name) VALUES ('ONLINE', 'Online');
INSERT INTO venue_dtl (venue_id, name) VALUES('JC101', 'Jonas Clark Hall 101');
