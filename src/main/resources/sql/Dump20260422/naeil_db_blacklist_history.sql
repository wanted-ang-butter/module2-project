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
-- Table structure for table `blacklist_history`
--

DROP TABLE IF EXISTS `blacklist_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blacklist_history` (
  `blacklist_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `reason` text NOT NULL,
  `release_reason` text,
  `admin_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`blacklist_id`),
  KEY `FK1rineiy07efo1u3foq267e5k2` (`admin_id`),
  KEY `FKbqtt6ob1qkuoqvgdq2ti9fixt` (`user_id`),
  CONSTRAINT `FK1rineiy07efo1u3foq267e5k2` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKbqtt6ob1qkuoqvgdq2ti9fixt` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1016 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blacklist_history`
--

LOCK TABLES `blacklist_history` WRITE;
/*!40000 ALTER TABLE `blacklist_history` DISABLE KEYS */;
INSERT INTO `blacklist_history` VALUES (1001,'2026-04-26 00:01:00.000000',NULL,'2026-04-26 00:01:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2016),(1002,'2026-04-26 00:02:00.000000',NULL,'2026-04-26 00:02:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2017),(1003,'2026-04-26 00:03:00.000000',NULL,'2026-04-26 00:03:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2018),(1004,'2026-04-26 00:04:00.000000',NULL,'2026-04-26 00:04:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2019),(1005,'2026-04-26 00:05:00.000000',NULL,'2026-04-26 00:05:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2020),(1006,'2026-04-26 00:06:00.000000',NULL,'2026-04-26 00:06:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2021),(1007,'2026-04-26 00:07:00.000000',NULL,'2026-04-26 00:07:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2022),(1008,'2026-04-26 00:08:00.000000',NULL,'2026-04-26 00:08:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2023),(1009,'2026-04-26 00:09:00.000000',NULL,'2026-04-26 00:09:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2024),(1010,'2026-04-26 00:10:00.000000',NULL,'2026-04-26 00:10:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2025),(1011,'2026-04-26 00:11:00.000000',NULL,'2026-04-26 00:11:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2026),(1012,'2026-04-26 00:12:00.000000',NULL,'2026-04-26 00:12:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2027),(1013,'2026-04-26 00:13:00.000000',NULL,'2026-04-26 00:13:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2028),(1014,'2026-04-26 00:14:00.000000',NULL,'2026-04-26 00:14:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2029),(1015,'2026-04-26 00:15:00.000000',NULL,'2026-04-26 00:15:00.000000','Auto blacklist due to repeated live lecture reservation cancellation.',NULL,NULL,2030);
/*!40000 ALTER TABLE `blacklist_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-22 14:25:25
