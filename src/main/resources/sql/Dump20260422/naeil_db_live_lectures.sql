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
-- Table structure for table `live_lectures`
--

DROP TABLE IF EXISTS `live_lectures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `live_lectures` (
  `live_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `current_count` int NOT NULL,
  `description` text,
  `end_at` datetime(6) DEFAULT NULL,
  `max_capacity` int NOT NULL,
  `reservation_start_at` datetime(6) DEFAULT NULL,
  `start_at` datetime(6) DEFAULT NULL,
  `status` enum('APPROVED','CANCELLED','ENDED','IN_PROGRESS','PENDING','REJECTED') NOT NULL,
  `streaming_url` varchar(500) DEFAULT NULL,
  `title` varchar(150) NOT NULL,
  `instructor_id` bigint NOT NULL,
  PRIMARY KEY (`live_id`),
  KEY `FKtjc4juy9gmx8mqvuolgymoluk` (`instructor_id`),
  CONSTRAINT `FKtjc4juy9gmx8mqvuolgymoluk` FOREIGN KEY (`instructor_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `live_lectures`
--

LOCK TABLES `live_lectures` WRITE;
/*!40000 ALTER TABLE `live_lectures` DISABLE KEYS */;
INSERT INTO `live_lectures` VALUES (1,'2026-04-21 19:05:16.805691',NULL,'2026-04-21 19:14:13.612662',0,'Spring JPA를 학습하면서 자주 헷갈리는 영속성 컨텍스트, 연관관계 매핑, 지연 로딩, N+1 문제를 실시간으로 함께 정리하는 라이브 강의입니다.\r\n\r\n사전에 받은 질문을 중심으로 실무에서 자주 발생하는 JPA 설계 고민을 풀어보고, 강의 중 실시간 Q&A를 통해 개별 상황에 맞는 해결 방향도 함께 안내합니다.','2026-04-23 20:20:00.000000',20,'2026-04-21 19:10:00.000000','2026-04-23 18:00:00.000000','APPROVED','https://youtu.be/ibNor8gfMI0?si=r1Fk4FJfEj9Kza6o','Spring JPA 실전 Q&A 라이브',3),(2,'2026-04-21 19:06:34.679038',NULL,'2026-04-21 19:14:14.205543',0,'라쿤의 인형 뽑기 VLOG','2026-04-23 18:00:00.000000',10,'2026-04-21 19:15:00.000000','2026-04-23 12:00:00.000000','APPROVED','https://youtu.be/eS3CA5TTl2M?si=qHS0mn1R2EOlYI_h','라쿤의 인형 뽑기 VLOG',3),(1001,'2026-04-29 11:21:00.000000',NULL,'2026-04-29 11:21:00.000000',13,'Dummy live lecture 1 description for reservation and room testing.','2026-04-23 23:00:00.000000',40,'2026-04-20 21:00:00.000000','2026-04-23 21:00:00.000000','APPROVED','https://example.com/live/1001','Dummy Live Lecture 01',1001),(1002,'2026-04-29 11:22:00.000000',NULL,'2026-04-22 09:53:54.051162',17,'Dummy live lecture 2 description for reservation and room testing.','2026-04-25 00:00:00.000000',50,'2026-04-21 22:00:00.000000','2026-04-24 22:00:00.000000','APPROVED','https://example.com/live/1002','Dummy Live Lecture 02',1002),(1003,'2026-04-29 11:23:00.000000',NULL,'2026-04-29 11:23:00.000000',19,'Dummy live lecture 3 description for reservation and room testing.','2026-04-26 01:00:00.000000',60,'2026-04-22 23:00:00.000000','2026-04-25 23:00:00.000000','APPROVED','https://example.com/live/1003','Dummy Live Lecture 03',1003),(1004,'2026-04-29 11:24:00.000000',NULL,'2026-04-29 11:24:00.000000',22,'Dummy live lecture 4 description for reservation and room testing.','2026-04-27 02:00:00.000000',30,'2026-04-24 00:00:00.000000','2026-04-27 00:00:00.000000','APPROVED','https://example.com/live/1004','Dummy Live Lecture 04',1004),(1005,'2026-04-29 11:25:00.000000',NULL,'2026-04-29 11:25:00.000000',25,'Dummy live lecture 5 description for reservation and room testing.','2026-04-28 03:00:00.000000',40,'2026-04-25 01:00:00.000000','2026-04-28 01:00:00.000000','APPROVED','https://example.com/live/1005','Dummy Live Lecture 05',1005),(1006,'2026-04-29 11:26:00.000000',NULL,'2026-04-29 11:26:00.000000',28,'Dummy live lecture 6 description for reservation and room testing.','2026-04-28 22:00:00.000000',50,'2026-04-25 20:00:00.000000','2026-04-28 20:00:00.000000','APPROVED','https://example.com/live/1006','Dummy Live Lecture 06',1006),(1007,'2026-04-29 11:27:00.000000',NULL,'2026-04-29 11:27:00.000000',31,'Dummy live lecture 7 description for reservation and room testing.','2026-04-21 21:23:00.000000',60,'2026-04-18 19:23:00.000000','2026-04-21 19:23:00.000000','IN_PROGRESS','https://example.com/live/1007','Dummy Live Lecture 07',1007),(1008,'2026-04-29 11:28:00.000000',NULL,'2026-04-29 11:28:00.000000',28,'Dummy live lecture 8 description for reservation and room testing.','2026-04-21 21:22:00.000000',30,'2026-04-18 19:22:00.000000','2026-04-21 19:22:00.000000','IN_PROGRESS','https://example.com/live/1008','Dummy Live Lecture 08',1008),(1009,'2026-04-29 11:29:00.000000',NULL,'2026-04-29 11:29:00.000000',0,'Dummy live lecture 9 description for reservation and room testing.','2026-05-02 01:00:00.000000',40,'2026-04-28 23:00:00.000000','2026-05-01 23:00:00.000000','PENDING','https://example.com/live/1009','Dummy Live Lecture 09',1009),(1010,'2026-04-29 11:30:00.000000',NULL,'2026-04-29 11:30:00.000000',0,'Dummy live lecture 10 description for reservation and room testing.','2026-05-03 02:00:00.000000',50,'2026-04-30 00:00:00.000000','2026-05-03 00:00:00.000000','REJECTED','https://example.com/live/1010','Dummy Live Lecture 10',1010);
/*!40000 ALTER TABLE `live_lectures` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-22 14:25:24
