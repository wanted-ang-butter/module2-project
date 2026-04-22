-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: naeil_db
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `instructor_applications`
--

DROP TABLE IF EXISTS `instructor_applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `instructor_applications` (
  `application_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `account_number` varchar(255) DEFAULT NULL,
  `career` text,
  `face_img` varchar(255) DEFAULT NULL,
  `introduction` varchar(255) DEFAULT NULL,
  `proof_file` varchar(255) DEFAULT NULL,
  `reject_reason` text,
  `status` enum('APPROVED','PENDING','REJECTED') DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `category_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`application_id`),
  KEY `FKdi3ipwhrvthiy92op2ig0bj0r` (`category_id`),
  KEY `FKnt6ss9ppkuhjay2s1t5l9n3ff` (`user_id`),
  CONSTRAINT `FKdi3ipwhrvthiy92op2ig0bj0r` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`),
  CONSTRAINT `FKnt6ss9ppkuhjay2s1t5l9n3ff` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1021 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instructor_applications`
--

LOCK TABLES `instructor_applications` WRITE;
/*!40000 ALTER TABLE `instructor_applications` DISABLE KEYS */;
INSERT INTO `instructor_applications` VALUES (1001,'2026-04-22 07:41:00.000000',NULL,'2026-04-22 07:41:00.000000','110-1001-999999','4 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 001',1,1001),(1002,'2026-04-22 07:42:00.000000',NULL,'2026-04-22 07:42:00.000000','110-1002-999999','5 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 002',2,1002),(1003,'2026-04-22 07:43:00.000000',NULL,'2026-04-22 07:43:00.000000','110-1003-999999','6 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 003',3,1003),(1004,'2026-04-22 07:44:00.000000',NULL,'2026-04-22 07:44:00.000000','110-1004-999999','7 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 004',4,1004),(1005,'2026-04-22 07:45:00.000000',NULL,'2026-04-22 07:45:00.000000','110-1005-999999','8 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 005',5,1005),(1006,'2026-04-22 07:46:00.000000',NULL,'2026-04-22 07:46:00.000000','110-1006-999999','9 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 006',6,1006),(1007,'2026-04-22 07:47:00.000000',NULL,'2026-04-22 07:47:00.000000','110-1007-999999','10 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 007',1,1007),(1008,'2026-04-22 07:48:00.000000',NULL,'2026-04-22 07:48:00.000000','110-1008-999999','11 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 008',2,1008),(1009,'2026-04-22 07:49:00.000000',NULL,'2026-04-22 07:49:00.000000','110-1009-999999','12 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 009',3,1009),(1010,'2026-04-22 07:50:00.000000',NULL,'2026-04-22 07:50:00.000000','110-1010-999999','13 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 010',4,1010),(1011,'2026-04-22 07:51:00.000000',NULL,'2026-04-22 07:51:00.000000','110-1011-999999','14 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 011',5,1011),(1012,'2026-04-22 07:52:00.000000',NULL,'2026-04-22 07:52:00.000000','110-1012-999999','15 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 012',6,1012),(1013,'2026-04-22 07:53:00.000000',NULL,'2026-04-22 07:53:00.000000','110-1013-999999','16 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 013',1,1013),(1014,'2026-04-22 07:54:00.000000',NULL,'2026-04-22 07:54:00.000000','110-1014-999999','17 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 014',2,1014),(1015,'2026-04-22 07:55:00.000000',NULL,'2026-04-22 07:55:00.000000','110-1015-999999','18 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 015',3,1015),(1016,'2026-04-22 07:56:00.000000',NULL,'2026-04-22 07:56:00.000000','110-1016-999999','19 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 016',4,1016),(1017,'2026-04-22 07:57:00.000000',NULL,'2026-04-22 07:57:00.000000','110-1017-999999','20 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 017',5,1017),(1018,'2026-04-22 07:58:00.000000',NULL,'2026-04-22 07:58:00.000000','110-1018-999999','21 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 018',6,1018),(1019,'2026-04-22 07:59:00.000000',NULL,'2026-04-22 07:59:00.000000','110-1019-999999','22 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 019',1,1019),(1020,'2026-04-22 08:00:00.000000',NULL,'2026-04-22 08:00:00.000000','110-1020-999999','23 years of practical development and mentoring experience.','/uploads/profiles/default-instructor.png','Practice-first instructor profile.','/uploads/proofs/sample-proof.pdf',NULL,'APPROVED','Instructor Application 020',2,1020);
/*!40000 ALTER TABLE `instructor_applications` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-22 14:25:26
