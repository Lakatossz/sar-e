-- MySQL Script generated by MySQL Workbench
-- Sun Jan 15 15:16:55 2023
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema piae_v1_db
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema piae_v1_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `piae_v1_db` DEFAULT CHARACTER SET utf8 COLLATE utf8_czech_ci;
USE `piae_v1_db`;

-- -----------------------------------------------------
-- Table `piae_v1_db`.`workplace`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `piae_v1_db`.`workplace`
(
    `workplace_id`    INT          NOT NULL AUTO_INCREMENT,
    `wrk_enabled`     TINYINT      NOT NULL,
    `wrk_abbrevation` VARCHAR(50)  NOT NULL,
    `wrk_name`        VARCHAR(200) NOT NULL,
    `wrk_manager_id`  INT          NULL,
    PRIMARY KEY (`workplace_id`),
    INDEX `fk_wrk_manager_id_idx` (`wrk_manager_id` ASC) VISIBLE,
    UNIQUE INDEX `wrk_abbrevation_UNIQUE` (`wrk_abbrevation` ASC) VISIBLE,
    UNIQUE INDEX `wrk_name_UNIQUE` (`wrk_name` ASC) VISIBLE,
    CONSTRAINT `fk_wrk_manager_id`
        FOREIGN KEY (`wrk_manager_id`)
            REFERENCES `piae_v1_db`.`employee` (`employee_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `piae_v1_db`.`employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `piae_v1_db`.`employee`
(
    `employee_id`      INT          NOT NULL AUTO_INCREMENT,
    `emp_enabled`      TINYINT      NOT NULL,
    `emp_workplace_id` INT          NOT NULL,
    `emp_first_name`   VARCHAR(100) NOT NULL,
    `emp_last_name`    VARCHAR(100) NOT NULL,
    `emp_orion_login`  VARCHAR(100) NOT NULL,
    `emp_email`        VARCHAR(100) NOT NULL,
    PRIMARY KEY (`employee_id`),
    INDEX `fk_emp_workplace_id_idx` (`emp_workplace_id` ASC) VISIBLE,
    UNIQUE INDEX `emp_orion_login_UNIQUE` (`emp_orion_login` ASC) VISIBLE,
    UNIQUE INDEX `emp_email_UNIQUE` (`emp_email` ASC) VISIBLE,
    CONSTRAINT `fk_emp_workplace_id`
        FOREIGN KEY (`emp_workplace_id`)
            REFERENCES `piae_v1_db`.`workplace` (`workplace_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `piae_v1_db`.`superior`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `piae_v1_db`.`superior`
(
    `superior_id`     INT     NOT NULL AUTO_INCREMENT,
    `sup_enabled`     TINYINT NOT NULL,
    `sup_superior_id` INT     NOT NULL,
    `sup_employee_id` INT     NOT NULL,
    PRIMARY KEY (`superior_id`),
    INDEX `fk_sup_superior_id_idx` (`sup_superior_id` ASC) VISIBLE,
    INDEX `fk_sup_employee_id_idx` (`sup_employee_id` ASC) VISIBLE,
    CONSTRAINT `fk_sup_superior_id`
        FOREIGN KEY (`sup_superior_id`)
            REFERENCES `piae_v1_db`.`employee` (`employee_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `fk_sup_employee_id`
        FOREIGN KEY (`sup_employee_id`)
            REFERENCES `piae_v1_db`.`employee` (`employee_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `piae_v1_db`.`project`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `piae_v1_db`.`project`
(
    `project_id`       INT           NOT NULL AUTO_INCREMENT,
    `pro_enabled`      TINYINT       NOT NULL,
    `pro_name`         VARCHAR(200)  NOT NULL,
    `pro_manager_id`   INT           NOT NULL,
    `pro_workplace_id` INT           NOT NULL,
    `pro_active_from`  DATE          NOT NULL,
    `pro_active_until` DATE          NOT NULL,
    `pro_description`  VARCHAR(2000) NOT NULL,
    PRIMARY KEY (`project_id`),
    INDEX `fk_pro_manager_id_idx` (`pro_manager_id` ASC) VISIBLE,
    INDEX `fk_pro_workplace_id_idx` (`pro_workplace_id` ASC) VISIBLE,
    UNIQUE INDEX `pro_name_UNIQUE` (`pro_name` ASC) VISIBLE,
    CONSTRAINT `fk_pro_manager_id`
        FOREIGN KEY (`pro_manager_id`)
            REFERENCES `piae_v1_db`.`employee` (`employee_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `fk_pro_workplace_id`
        FOREIGN KEY (`pro_workplace_id`)
            REFERENCES `piae_v1_db`.`workplace` (`workplace_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `piae_v1_db`.`assignment`
-- -----------------------------------------------------
CREATE TABLE `assignment` (
                              `assignment_id` int NOT NULL AUTO_INCREMENT,
                              `ass_enabled` tinyint NOT NULL,
                              `ass_employee_id` int NOT NULL,
                              `ass_project_id` int DEFAULT NULL,
                              `ass_course_id` int DEFAULT NULL,
                              `ass_active` int NOT NULL,
                              `ass_function_id` int DEFAULT NULL,
                              `ass_active_from` date NOT NULL,
                              `ass_active_until` date NOT NULL,
                              `ass_scope` int NOT NULL,
                              `ass_description` varchar(2000) COLLATE utf8mb3_czech_ci NOT NULL,
                              PRIMARY KEY (`assignment_id`),
                              KEY `fk_ass_employee_id_idx` (`ass_employee_id`),
                              KEY `fk_ass_project_id_idx` (`ass_project_id`),
                              KEY `fk_ass_course_id_idx` (`ass_course_id`),
                              KEY `fk_ass_function_id_idx` (`ass_function_id`),
                              CONSTRAINT `fk_ass_employee_id` FOREIGN KEY (`ass_employee_id`) REFERENCES `employee` (`employee_id`),
                              CONSTRAINT `fk_ass_project_id` FOREIGN KEY (`ass_project_id`) REFERENCES `project` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_czech_ci;


-- -----------------------------------------------------
-- Table `piae_v1_db`.`project_employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `piae_v1_db`.`project_employee`
(
    `project_employee_id` INT     NOT NULL AUTO_INCREMENT,
    `pre_enabled`         TINYINT NOT NULL,
    `pre_project_id`      INT     NOT NULL,
    `pre_employee_id`     INT     NOT NULL,
    PRIMARY KEY (`project_employee_id`),
    INDEX `fk_pre_project_id_idx` (`pre_project_id` ASC) VISIBLE,
    INDEX `fk_pre_employee_id_idx` (`pre_employee_id` ASC) VISIBLE,
    CONSTRAINT `fk_pre_project_id`
        FOREIGN KEY (`pre_project_id`)
            REFERENCES `piae_v1_db`.`project` (`project_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `fk_pre_employee_id`
        FOREIGN KEY (`pre_employee_id`)
            REFERENCES `piae_v1_db`.`employee` (`employee_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `piae_v1_db`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `piae_v1_db`.`user`
(
    `user_id`     INT          NOT NULL AUTO_INCREMENT,
    `password`    VARCHAR(255) NOT NULL,
    `enabled`     TINYINT      NULL,
    `us_employee` INT          NOT NULL,
    `us_is_temp`  TINYINT      NOT NULL,
    PRIMARY KEY (`user_id`),
    INDEX `fk_user_employee_idx` (`us_employee` ASC) VISIBLE,
    CONSTRAINT `fk_user_employee`
        FOREIGN KEY (`us_employee`)
            REFERENCES `piae_v1_db`.`employee` (`employee_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `piae_v1_db`.`authority`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `piae_v1_db`.`authority`
(
    `authority_id`   INT          NOT NULL AUTO_INCREMENT,
    `auth_user_id`   INT          NOT NULL,
    `auth_authotity` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`authority_id`),
    INDEX `fk_authority_user_idx` (`auth_user_id` ASC) VISIBLE,
    CONSTRAINT `fk_authority_user`
        FOREIGN KEY (`auth_user_id`)
            REFERENCES `piae_v1_db`.`user` (`user_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;

INSERT INTO workplace (workplace_id, wrk_enabled, wrk_abbrevation, wrk_name)
VALUES (1, 1, "FAV", "Fakulta aplikovaných věd");

INSERT INTO employee (employee_id, emp_enabled, emp_workplace_id, emp_first_name, emp_last_name, emp_orion_login,
                      emp_email)
VALUES (1, 1, 1, "Admin", "Admin", "admin", "admin@admin.adm");
INSERT INTO user (user_id, password, enabled, us_employee, us_is_temp)
VALUES (1, '$2a$10$lP26wQKz2CF9DsFPXPqd1euMPDNzVC6NVOgogsrMtBKyzsY0ypSby', 1, 1, 1);
INSERT INTO authority (authority_id, auth_user_id, auth_authotity)
VALUES (1, 1, 'ADMIN');
INSERT INTO authority (authority_id, auth_user_id, auth_authotity)
VALUES (2, 1, 'SECRETARIAT');

INSERT INTO employee (employee_id, emp_enabled, emp_workplace_id, emp_first_name, emp_last_name, emp_orion_login,
                      emp_email)
VALUES (2, 1, 1, "Sekretariát", "Sekretatiát", "sekretariat", "sekretariat@sekr.sekr");
INSERT INTO user (user_id, password, enabled, us_employee, us_is_temp)
VALUES (2, '$2a$10$lP26wQKz2CF9DsFPXPqd1euMPDNzVC6NVOgogsrMtBKyzsY0ypSby', 1, 2, 1);
INSERT INTO authority (authority_id, auth_user_id, auth_authotity)
VALUES (3, 2, 'SECRETARIAT');

INSERT INTO employee (employee_id, emp_enabled, emp_workplace_id, emp_first_name, emp_last_name, emp_orion_login,
                      emp_email)
VALUES (3, 1, 1, "Vedoucí", "projektu", "projekt", "vedproject@test.te");
INSERT INTO user (user_id, password, enabled, us_employee, us_is_temp)
VALUES (3, '$2a$10$lP26wQKz2CF9DsFPXPqd1euMPDNzVC6NVOgogsrMtBKyzsY0ypSby', 1, 3, 0);
INSERT INTO authority (authority_id, auth_user_id, auth_authotity)
VALUES (4, 3, 'USER');

INSERT INTO employee (employee_id, emp_enabled, emp_workplace_id, emp_first_name, emp_last_name, emp_orion_login,
                      emp_email)
VALUES (4, 1, 1, "Nadřízený", "pracovník", "nadriz", "nadriz@test.te");
INSERT INTO user (user_id, password, enabled, us_employee, us_is_temp)
VALUES (4, '$2a$10$lP26wQKz2CF9DsFPXPqd1euMPDNzVC6NVOgogsrMtBKyzsY0ypSby', 1, 4, 0);
INSERT INTO authority (authority_id, auth_user_id, auth_authotity)
VALUES (5, 4, 'USER');

INSERT INTO employee (employee_id, emp_enabled, emp_workplace_id, emp_first_name, emp_last_name, emp_orion_login,
                      emp_email)
VALUES (5, 1, 1, "Pracovník", "První", "prvni", "prvni@test.te");
INSERT INTO user (user_id, password, enabled, us_employee, us_is_temp)
VALUES (5, '$2a$10$lP26wQKz2CF9DsFPXPqd1euMPDNzVC6NVOgogsrMtBKyzsY0ypSby', 1, 5, 0);
INSERT INTO authority (authority_id, auth_user_id, auth_authotity)
VALUES (6, 5, 'USER');

INSERT INTO employee (employee_id, emp_enabled, emp_workplace_id, emp_first_name, emp_last_name, emp_orion_login,
                      emp_email)
VALUES (6, 1, 1, "Pracovník", "Druhý", "druhy", "druhy@test.te");
INSERT INTO user (user_id, password, enabled, us_employee, us_is_temp)
VALUES (6, '$2a$10$lP26wQKz2CF9DsFPXPqd1euMPDNzVC6NVOgogsrMtBKyzsY0ypSby', 1, 6, 0);
INSERT INTO authority (authority_id, auth_user_id, auth_authotity)
VALUES (7, 6, 'USER');

UPDATE `piae_v1_db`.`workplace`
SET `wrk_manager_id` = '1'
WHERE (`workplace_id` = '1');

INSERT INTO `piae_v1_db`.`superior` (`superior_id`, `sup_enabled`, `sup_superior_id`, `sup_employee_id`)
VALUES ('1', '1', '4', '5');
INSERT INTO `piae_v1_db`.`superior` (`superior_id`, `sup_enabled`, `sup_superior_id`, `sup_employee_id`)
VALUES ('1', '1', '4', '6');

INSERT INTO `piae_v1_db`.`project` (`project_id`, `pro_enabled`, `pro_name`, `pro_manager_id`, `pro_workplace_id`,
                                    `pro_active_from`, `pro_active_until`, `pro_description`)
VALUES ('1', '1', 'Testovací projekt', '3', '1', '2010-01-01', '2030-01-01',
        'testovací projekt pro účely aslespoň nějakých dat nebo tak něco');

INSERT INTO `piae_v1_db`.`project_employee` (`project_employee_id`, `pre_enabled`, `pre_project_id`, `pre_employee_id`)
VALUES ('1', '1', '1', '5');
INSERT INTO `piae_v1_db`.`project_employee` (`project_employee_id`, `pre_enabled`, `pre_project_id`, `pre_employee_id`)
VALUES ('2', '1', '1', '6');

INSERT INTO `piae_v1_db`.`assignment` (`assignment_id`, `ass_enabled`, `ass_employee_id`, `ass_project_id`, `ass_active_from`, `ass_active_until`, `ass_scope`, `ass_description`, `ass_active`) VALUES ('1', '1', '5', '1', '2020-01-01', '2025-01-01', '600', 'al1', '1');
INSERT INTO `piae_v1_db`.`assignment` (`assignment_id`, `ass_enabled`, `ass_employee_id`, `ass_project_id`, `ass_active_from`, `ass_active_until`, `ass_scope`, `ass_description`, `ass_active`) VALUES ('2', '1', '5', '1', '2023-01-01', '2024-01-01', '1000', 'al2', '1');
INSERT INTO `piae_v1_db`.`assignment` (`assignment_id`, `ass_enabled`, `ass_employee_id`, `ass_project_id`, `ass_active_from`, `ass_active_until`, `ass_scope`, `ass_description`, `ass_active`) VALUES ('3', '1', '6', '1', '2026-01-01', '2027-01-01', '500', 'al3', '1');
INSERT INTO `piae_v1_db`.`assignment` (`assignment_id`, `ass_enabled`, `ass_employee_id`, `ass_project_id`, `ass_active_from`, `ass_active_until`, `ass_scope`, `ass_description`, `ass_active`) VALUES ('4', '1', '6', '1', '2022-01-01', '2025-01-01', '400', 'al3', '1');


