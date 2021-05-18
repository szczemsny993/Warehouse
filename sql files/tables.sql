CREATE TABLE computer
(
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`brand` VARCHAR(32) NOT NULL DEFAULT '',
	`model` VARCHAR(32) NOT NULL DEFAULT '',
	`type` VARCHAR(32) NOT NULL DEFAULT '',
	`processor` VARCHAR(32) NOT NULL DEFAULT '',
	`ram` VARCHAR(32) NOT NULL DEFAULT '',
	`hdd` VARCHAR(32) NOT NULL DEFAULT '',
	`dvd` BOOLEAN NOT NULL DEFAULT true,
	`license` BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE supply
(
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`unique_id` INT,
	`date` VARCHAR(32) NOT NULL DEFAULT '1970-1-1',
	`amount` INT NOT NULL DEFAULT 0,
	`pallete_count` INT NOT NULL DEFAULT 0,
	`comment` VARCHAR(256) DEFAULT ''
);

CREATE TABLE supply_private
(
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`supply_id` INT NOT NULL DEFAULT 0,
	`computer_id` INT NOT NULL DEFAULT 0,
	`amount` INT NOT NULL DEFAULT 0
);

CREATE TABLE warehouse
(
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`computer_id` INT NOT NULL DEFAULT 0,
	`amount` INT NOT NULL DEFAULT 0
);

CREATE TABLE client
(
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(32) NOT NULL DEFAULT '',
    `name` VARCHAR(64) NOT NULL DEFAULT '',
    `nip` VARCHAR(32) NOT NULL DEFAULT '',
    `city` VARCHAR(32) NOT NULL DEFAULT '',
    `post_code` VARCHAR(16) NOT NULL DEFAULT '',
    `address` VARCHAR(32) NOT NULL DEFAULT '', 
    `telephone` VARCHAR(32) NOT NULL DEFAULT ''
);

CREATE TABLE passwords
(
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `client_id` INT NOT NULL,
    `type` VARCHAR(32) NOT NULL DEFAULT 'Urzadzenie',
    `date` VARCHAR(32) NOT NULL DEFAULT '',
    `code` VARCHAR(32) NOT NULL DEFAULT '',
    `name` VARCHAR(32) NOT NULL DEFAULT '',
    `port` INT NOT NULL DEFAULT 0,
    `user` VARCHAR(32) NOT NULL DEFAULT '',
    `password` VARCHAR(32) NOT NULL DEFAULT '',
    `description` VARCHAR(64) NOT NULL DEFAULT '',
    `location` VARCHAR(32) NOT NULL DEFAULT '',
    `serial` VARCHAR(64) NOT NULL DEFAULT '',
    `mac` VARCHAR(32) NOT NULL DEFAULT '',
    `internal_id` VARCHAR(32) NOT NULL DEFAULT '',
    `license` VARCHAR(64) NOT NULL DEFAULT '',
    `position` VARCHAR(32) NOT NULL DEFAULT ''
);

CREATE TABLE accounts
(
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`user` VARCHAR(32) NOT NULL,
	`hashpassword` VARCHAR(64) NOT NULL,
	`permision_level` INT(11) NOT NULL DEFAULT 1,
	`user_directory` VARCHAR(32) NOT NULL DEFAULT ''
);

INSERT INTO accounts (user, hashpassword, permision_level) VALUES ('administrator', '9e89bd9522b64e8f28023bc04268a527c597fbd391d79e2611914c79ae93adfe', '3');
INSERT INTO account_permission (account_name, module, value) VALUES 
('administrator', 'warehouse', '1'),('administrator', 'shipment', '1'),
('administrator', 'supply', '1'),('administrator', 'computer', 1),
('administrator', 'client', '1'),('administrator', 'passwords', '1'),
('administrator', 'subscribtion', '1'), ('administrator', 'backup', '1');


CREATE TABLE shipment
(
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`date` DATE NOT NULL DEFAULT '2018-01-01',
	`shipment_id` VARCHAR(32) NOT NULL DEFAULT '',
	`warranty_length` INT NOT NULL DEFAULT 0,
	`pallete_count` INT NOT NULL DEFAULT 0,
	`description` VARCHAR(256) NOT NULL DEFAULT ''
);

CREATE TABLE shipment_content
(
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`shipment_id` VARCHAR(32) NOT NULL DEFAULT '',
	`computer_id` INT NOT NULL DEFAULT 0,
	`computer_amount` INT NOT NULL DEFAULT 0
);

CREATE TABLE bar_codes
(
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`shipment_id` VARCHAR(32) NOT NULL DEFAULT '',
	`casing` VARCHAR(64) NOT NULL DEFAULT '',
	`motherboard` VARCHAR(64) NOT NULL DEFAULT '',
	`hdd` VARCHAR(64) NOT NULL DEFAULT '',
	`power_supply` VARCHAR(64) NOT NULL DEFAULT '',
	`dvd` VARCHAR(64) NOT NULL DEFAULT ''
);

CREATE TABLE account_permission
(
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`account_name` VARCHAR(32) NOT NULL DEFAULT '',
	`module` VARCHAR(32) NOT NULL DEFAULT '',
	`value` BOOLEAN NOT NULL DEFAULT false
);

























