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
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `post_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category` enum('FREE','QNA') NOT NULL,
  `content` text NOT NULL,
  `is_public` bit(1) NOT NULL,
  `is_resolved` bit(1) NOT NULL,
  `title` varchar(100) NOT NULL,
  `view_count` int NOT NULL,
  `course_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`post_id`),
  KEY `FKm2hjujpdly9emepkb7cdi05t3` (`course_id`),
  KEY `FK5lidm6cqbc7u4xhqpxm898qme` (`user_id`),
  CONSTRAINT `FK5lidm6cqbc7u4xhqpxm898qme` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKm2hjujpdly9emepkb7cdi05t3` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1061 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,'2026-04-21 19:17:22.159409',NULL,'2026-04-21 19:17:22.159409','QNA','요즘 처음 개발 했는데 N+1 문제가 뭐에여? 궁금해요',_binary '',_binary '\0','N+1 문제가 뭐죠?',0,NULL,1),(2,'2026-04-21 19:20:45.077361',NULL,'2026-04-21 19:21:54.902811','FREE','요즘 그렇게 힘들다며~ 쿠쿠르삥뽕',_binary '\0',_binary '\0','요즘 개발자들 취업 안된다던데 ~ ㅋㅋㅋ',7,NULL,5),(1001,'2026-04-28 02:01:00.000000',NULL,'2026-04-28 02:01:00.000000','FREE','Dummy post content 1.',_binary '',_binary '\0','Dummy post title 1',7,NULL,2004),(1002,'2026-04-28 02:02:00.000000',NULL,'2026-04-28 02:02:00.000000','QNA','Dummy post content 2.',_binary '',_binary '\0','Dummy post title 2',14,NULL,2007),(1003,'2026-04-28 02:03:00.000000',NULL,'2026-04-28 02:03:00.000000','FREE','Dummy post content 3.',_binary '',_binary '\0','Dummy post title 3',21,1003,2010),(1004,'2026-04-28 02:04:00.000000',NULL,'2026-04-28 02:04:00.000000','QNA','Dummy post content 4.',_binary '',_binary '','Dummy post title 4',28,NULL,2013),(1005,'2026-04-28 02:05:00.000000',NULL,'2026-04-28 02:05:00.000000','FREE','Dummy post content 5.',_binary '\0',_binary '\0','Dummy post title 5',35,NULL,2016),(1006,'2026-04-28 02:06:00.000000',NULL,'2026-04-21 20:08:17.541846','QNA','Dummy post content 6.',_binary '',_binary '\0','Dummy post title 6',43,1006,2019),(1007,'2026-04-28 02:07:00.000000',NULL,'2026-04-28 02:07:00.000000','FREE','Dummy post content 7.',_binary '',_binary '\0','Dummy post title 7',49,NULL,2022),(1008,'2026-04-28 02:08:00.000000',NULL,'2026-04-28 02:08:00.000000','QNA','Dummy post content 8.',_binary '',_binary '','Dummy post title 8',56,NULL,2025),(1009,'2026-04-28 02:09:00.000000',NULL,'2026-04-28 02:09:00.000000','FREE','Dummy post content 9.',_binary '',_binary '\0','Dummy post title 9',63,1009,2028),(1010,'2026-04-28 02:10:00.000000',NULL,'2026-04-28 02:10:00.000000','QNA','Dummy post content 10.',_binary '\0',_binary '\0','Dummy post title 10',70,NULL,2031),(1011,'2026-04-28 02:11:00.000000',NULL,'2026-04-28 02:11:00.000000','FREE','Dummy post content 11.',_binary '',_binary '\0','Dummy post title 11',77,NULL,2034),(1012,'2026-04-28 02:12:00.000000',NULL,'2026-04-28 02:12:00.000000','QNA','Dummy post content 12.',_binary '',_binary '','Dummy post title 12',84,1012,2037),(1013,'2026-04-28 02:13:00.000000',NULL,'2026-04-28 02:13:00.000000','FREE','Dummy post content 13.',_binary '',_binary '\0','Dummy post title 13',91,NULL,2040),(1014,'2026-04-28 02:14:00.000000',NULL,'2026-04-28 02:14:00.000000','QNA','Dummy post content 14.',_binary '',_binary '\0','Dummy post title 14',98,NULL,2043),(1015,'2026-04-28 02:15:00.000000',NULL,'2026-04-28 02:15:00.000000','FREE','Dummy post content 15.',_binary '\0',_binary '\0','Dummy post title 15',105,1015,2046),(1016,'2026-04-28 02:16:00.000000',NULL,'2026-04-28 02:16:00.000000','QNA','Dummy post content 16.',_binary '',_binary '','Dummy post title 16',112,NULL,2049),(1017,'2026-04-28 02:17:00.000000',NULL,'2026-04-28 02:17:00.000000','FREE','Dummy post content 17.',_binary '',_binary '\0','Dummy post title 17',119,NULL,2052),(1018,'2026-04-28 02:18:00.000000',NULL,'2026-04-28 02:18:00.000000','QNA','Dummy post content 18.',_binary '',_binary '\0','Dummy post title 18',126,1018,2055),(1019,'2026-04-28 02:19:00.000000',NULL,'2026-04-28 02:19:00.000000','FREE','Dummy post content 19.',_binary '',_binary '\0','Dummy post title 19',133,NULL,2058),(1020,'2026-04-28 02:20:00.000000',NULL,'2026-04-28 02:20:00.000000','QNA','Dummy post content 20.',_binary '\0',_binary '','Dummy post title 20',140,NULL,2061),(1021,'2026-04-28 02:21:00.000000',NULL,'2026-04-28 02:21:00.000000','FREE','Dummy post content 21.',_binary '',_binary '\0','Dummy post title 21',147,1021,2064),(1022,'2026-04-28 02:22:00.000000',NULL,'2026-04-28 02:22:00.000000','QNA','Dummy post content 22.',_binary '',_binary '\0','Dummy post title 22',154,NULL,2067),(1023,'2026-04-28 02:23:00.000000',NULL,'2026-04-28 02:23:00.000000','FREE','Dummy post content 23.',_binary '',_binary '\0','Dummy post title 23',161,NULL,2070),(1024,'2026-04-28 02:24:00.000000',NULL,'2026-04-28 02:24:00.000000','QNA','Dummy post content 24.',_binary '',_binary '','Dummy post title 24',168,1024,2073),(1025,'2026-04-28 02:25:00.000000',NULL,'2026-04-28 02:25:00.000000','FREE','Dummy post content 25.',_binary '\0',_binary '\0','Dummy post title 25',175,NULL,2076),(1026,'2026-04-28 02:26:00.000000',NULL,'2026-04-28 02:26:00.000000','QNA','Dummy post content 26.',_binary '',_binary '\0','Dummy post title 26',182,NULL,2079),(1027,'2026-04-28 02:27:00.000000',NULL,'2026-04-28 02:27:00.000000','FREE','Dummy post content 27.',_binary '',_binary '\0','Dummy post title 27',189,1002,2082),(1028,'2026-04-28 02:28:00.000000',NULL,'2026-04-28 02:28:00.000000','QNA','Dummy post content 28.',_binary '',_binary '','Dummy post title 28',196,NULL,2085),(1029,'2026-04-28 02:29:00.000000',NULL,'2026-04-28 02:29:00.000000','FREE','Dummy post content 29.',_binary '',_binary '\0','Dummy post title 29',3,NULL,2088),(1030,'2026-04-28 02:30:00.000000',NULL,'2026-04-28 02:30:00.000000','QNA','Dummy post content 30.',_binary '\0',_binary '\0','Dummy post title 30',10,1005,2091),(1031,'2026-04-28 02:31:00.000000',NULL,'2026-04-28 02:31:00.000000','FREE','Dummy post content 31.',_binary '',_binary '\0','Dummy post title 31',17,NULL,2094),(1032,'2026-04-28 02:32:00.000000',NULL,'2026-04-28 02:32:00.000000','QNA','Dummy post content 32.',_binary '',_binary '','Dummy post title 32',24,NULL,2097),(1033,'2026-04-28 02:33:00.000000',NULL,'2026-04-28 02:33:00.000000','FREE','Dummy post content 33.',_binary '',_binary '\0','Dummy post title 33',31,1008,2100),(1034,'2026-04-28 02:34:00.000000',NULL,'2026-04-28 02:34:00.000000','QNA','Dummy post content 34.',_binary '',_binary '\0','Dummy post title 34',38,NULL,2103),(1035,'2026-04-28 02:35:00.000000',NULL,'2026-04-28 02:35:00.000000','FREE','Dummy post content 35.',_binary '\0',_binary '\0','Dummy post title 35',45,NULL,2106),(1036,'2026-04-28 02:36:00.000000',NULL,'2026-04-28 02:36:00.000000','QNA','Dummy post content 36.',_binary '',_binary '','Dummy post title 36',52,1011,2109),(1037,'2026-04-28 02:37:00.000000',NULL,'2026-04-28 02:37:00.000000','FREE','Dummy post content 37.',_binary '',_binary '\0','Dummy post title 37',59,NULL,2112),(1038,'2026-04-28 02:38:00.000000',NULL,'2026-04-28 02:38:00.000000','QNA','Dummy post content 38.',_binary '',_binary '\0','Dummy post title 38',66,NULL,2115),(1039,'2026-04-28 02:39:00.000000',NULL,'2026-04-28 02:39:00.000000','FREE','Dummy post content 39.',_binary '',_binary '\0','Dummy post title 39',73,1014,2118),(1040,'2026-04-28 02:40:00.000000',NULL,'2026-04-28 02:40:00.000000','QNA','Dummy post content 40.',_binary '\0',_binary '','Dummy post title 40',80,NULL,2121),(1041,'2026-04-28 02:41:00.000000',NULL,'2026-04-28 02:41:00.000000','FREE','Dummy post content 41.',_binary '',_binary '\0','Dummy post title 41',87,NULL,2124),(1042,'2026-04-28 02:42:00.000000',NULL,'2026-04-28 02:42:00.000000','QNA','Dummy post content 42.',_binary '',_binary '\0','Dummy post title 42',94,1017,2127),(1043,'2026-04-28 02:43:00.000000',NULL,'2026-04-28 02:43:00.000000','FREE','Dummy post content 43.',_binary '',_binary '\0','Dummy post title 43',101,NULL,2130),(1044,'2026-04-28 02:44:00.000000',NULL,'2026-04-28 02:44:00.000000','QNA','Dummy post content 44.',_binary '',_binary '','Dummy post title 44',108,NULL,2133),(1045,'2026-04-28 02:45:00.000000',NULL,'2026-04-28 02:45:00.000000','FREE','Dummy post content 45.',_binary '\0',_binary '\0','Dummy post title 45',115,1020,2136),(1046,'2026-04-28 02:46:00.000000',NULL,'2026-04-28 02:46:00.000000','QNA','Dummy post content 46.',_binary '',_binary '\0','Dummy post title 46',122,NULL,2139),(1047,'2026-04-28 02:47:00.000000',NULL,'2026-04-28 02:47:00.000000','FREE','Dummy post content 47.',_binary '',_binary '\0','Dummy post title 47',129,NULL,2142),(1048,'2026-04-28 02:48:00.000000',NULL,'2026-04-28 02:48:00.000000','QNA','Dummy post content 48.',_binary '',_binary '','Dummy post title 48',136,1023,2145),(1049,'2026-04-28 02:49:00.000000',NULL,'2026-04-28 02:49:00.000000','FREE','Dummy post content 49.',_binary '',_binary '\0','Dummy post title 49',143,NULL,2148),(1050,'2026-04-28 02:50:00.000000',NULL,'2026-04-28 02:50:00.000000','QNA','Dummy post content 50.',_binary '\0',_binary '\0','Dummy post title 50',150,NULL,2151),(1051,'2026-04-28 02:51:00.000000',NULL,'2026-04-28 02:51:00.000000','FREE','Dummy post content 51.',_binary '',_binary '\0','Dummy post title 51',157,1001,2154),(1052,'2026-04-28 02:52:00.000000',NULL,'2026-04-28 02:52:00.000000','QNA','Dummy post content 52.',_binary '',_binary '','Dummy post title 52',164,NULL,2157),(1053,'2026-04-28 02:53:00.000000',NULL,'2026-04-28 02:53:00.000000','FREE','Dummy post content 53.',_binary '',_binary '\0','Dummy post title 53',171,NULL,2160),(1054,'2026-04-28 02:54:00.000000',NULL,'2026-04-28 02:54:00.000000','QNA','Dummy post content 54.',_binary '',_binary '\0','Dummy post title 54',178,1004,2163),(1055,'2026-04-28 02:55:00.000000',NULL,'2026-04-28 02:55:00.000000','FREE','Dummy post content 55.',_binary '\0',_binary '\0','Dummy post title 55',185,NULL,2166),(1056,'2026-04-28 02:56:00.000000',NULL,'2026-04-28 02:56:00.000000','QNA','Dummy post content 56.',_binary '',_binary '','Dummy post title 56',192,NULL,2169),(1057,'2026-04-28 02:57:00.000000',NULL,'2026-04-21 20:08:20.654732','FREE','Dummy post content 57.',_binary '',_binary '\0','Dummy post title 57',200,1007,2172),(1058,'2026-04-28 02:58:00.000000',NULL,'2026-04-28 02:58:00.000000','QNA','Dummy post content 58.',_binary '',_binary '\0','Dummy post title 58',6,NULL,2175),(1059,'2026-04-28 02:59:00.000000',NULL,'2026-04-28 02:59:00.000000','FREE','Dummy post content 59.',_binary '',_binary '\0','Dummy post title 59',13,NULL,2178),(1060,'2026-04-28 03:00:00.000000',NULL,'2026-04-28 03:00:00.000000','QNA','Dummy post content 60.',_binary '\0',_binary '','Dummy post title 60',20,1010,2181);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
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
