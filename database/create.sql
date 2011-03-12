DROP DATABASE IF EXISTS august;
CREATE SCHEMA IF NOT EXISTS august;
USE august;

-- -----------------------------------------------------
-- Table `august`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `august`.`user` ;

CREATE  TABLE IF NOT EXISTS `august`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(16) NOT NULL ,
  `password` VARCHAR(60) NULL ,
  `email` VARCHAR(60) NULL ,
  `sms` VARCHAR(45) NULL ,
  `points` INT NOT NULL DEFAULT 100,
  `last_login` TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`) ) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `august`.`galaxy_shape`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `august`.`galaxy_shape` ;

CREATE  TABLE IF NOT EXISTS `august`.`galaxy_shape` (
  `id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `texture` VARCHAR(120) NOT NULL ,
  PRIMARY KEY (`id`) ) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Data for table `august`.`galaxy_shape`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
INSERT INTO `galaxy_shape` (`id`, `name`, `texture`) VALUES (0, 'Elliptical', '');
INSERT INTO `galaxy_shape` (`id`, `name`, `texture`) VALUES (1, 'Spiral', '');
INSERT INTO `galaxy_shape` (`id`, `name`, `texture`) VALUES (2, 'Dwarf', '');
INSERT INTO `galaxy_shape` (`id`, `name`, `texture`) VALUES (3, 'Ring', '');
INSERT INTO `galaxy_shape` (`id`, `name`, `texture`) VALUES (4, 'Lenticular', '');

COMMIT;

-- -----------------------------------------------------
-- Table `august`.`preference`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `august`.`preference` ;

CREATE  TABLE IF NOT EXISTS `august`.`preference` (
  `id` INT NOT NULL ,
  `description` VARCHAR(80) NOT NULL ,
  `boolean` boolean NOT NULL ,
  `numerical` boolean NOT NULL ,
  PRIMARY KEY (`id`) ) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `august`.`user_pref`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `august`.`user_pref` ;

CREATE  TABLE IF NOT EXISTS `august`.`user_pref` (
  `id` INT NOT NULL ,
  `user_id` INT NOT NULL ,
  `preference_id` INT NOT NULL ,
  `boolean` boolean  NULL ,
  `text` VARCHAR(90) NULL ,
  `numerical` INT NULL ,
  PRIMARY KEY (id) ,
  FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE CASCADE,
  FOREIGN KEY (preference_id) REFERENCES preference(id)
    ON DELETE CASCADE) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `august`.`object`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `august`.`object` ;

CREATE  TABLE IF NOT EXISTS `august`.`object` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `owner` INT,
  `birth` TIMESTAMP NOT NULL DEFAULT NOW(),
  `death` TIMESTAMP NULL DEFAULT NULL, 
  `name` VARCHAR(128) NULL DEFAULT NULL,
  `parent` INT NULL DEFAULT 0,
  `x` DOUBLE NOT NULL DEFAULT 0,
  `y` DOUBLE NOT NULL DEFAULT 0,
  `z` DOUBLE NOT NULL DEFAULT 0, 
  `velocity_magnitude` DOUBLE NOT NULL DEFAULT 0,
  `velocity_vector_x` DOUBLE NOT NULL DEFAULT 0, 
  `velocity_vector_y` DOUBLE NOT NULL DEFAULT 0,
  `velocity_vector_z` DOUBLE NOT NULL DEFAULT 0,
  `accel_magnitude` DOUBLE NOT NULL DEFAULT 0,
  `accel_vector_x` DOUBLE NOT NULL DEFAULT 0,
  `accel_vector_y` DOUBLE NOT NULL DEFAULT 0,
  `accel_vector_z` DOUBLE NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) ,
  FOREIGN KEY (owner) REFERENCES user(id)
    ON DELETE CASCADE
    ,
  FOREIGN KEY (parent) REFERENCES object(id)
    ON DELETE CASCADE
    ) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Data for table `august`.`object`
-- -----------------------------------------------------
INSERT INTO `object` (`name`, `owner`, `parent`)
    VALUES ('The Universe', 1, NULL);

-- -----------------------------------------------------
-- Table `august`.`state`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `august`.`state` ;

CREATE  TABLE IF NOT EXISTS `august`.`state` (
  `id` INT NOT NULL ,
  `name` VARCHAR(128) NOT NULL ,
  PRIMARY KEY (`id`)
    ) ENGINE=InnoDB;

INSERT INTO `state` (`id`, `name`)
    VALUES (1, 'Pulsar');
INSERT INTO `state` (`id`, `name`)
    VALUES (2, 'Supernova');
INSERT INTO `state` (`id`, `name`)
    VALUES (3, 'Black Hole');
INSERT INTO `state` (`id`, `name`)
    VALUES (4, 'Inert');
INSERT INTO `state` (`id`, `name`)
    VALUES (5, 'Forming');

-- -----------------------------------------------------
-- Table `august`.`link`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `august`.`link` ;

CREATE  TABLE IF NOT EXISTS `august`.`link` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `first` INT NOT NULL ,
  `second` INT NOT NULL ,
  PRIMARY KEY (`id`) ,
  FOREIGN KEY (first) REFERENCES object(id)
    ON DELETE CASCADE
    ,
  FOREIGN KEY (second) REFERENCES object(id)
    ON DELETE CASCADE
    ) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `august`.`star`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `august`.`star` ;

CREATE  TABLE IF NOT EXISTS `august`.`star` (
  `id` INT NOT NULL ,
  `mass` DOUBLE NOT NULL ,
  `radius` DOUBLE NOT NULL ,
  `colorR` INT NOT NULL DEFAULT 255,
  `colorG` INT NOT NULL DEFAULT 255,
  `colorB` INT NOT NULL DEFAULT 255,
  `luminosity` DOUBLE NOT NULL DEFAULT 255,
  `frequency` DOUBLE NOT NULL DEFAULT .862,
  `state` INT NOT NULL DEFAULT 5 ,
  PRIMARY KEY (`id`) ,
  FOREIGN KEY (id) REFERENCES object(id)
    ON DELETE CASCADE,
  FOREIGN KEY (state) REFERENCES state(id)
    ON DELETE CASCADE
    ) ENGINE=InnoDB;

USE mysql;
GRANT ALL PRIVILEGES ON august.* TO 'august'@'localhost'
    IDENTIFIED BY 'fsotm00n!' WITH GRANT OPTION;
FLUSH PRIVILEGES;
