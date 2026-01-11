-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: autoparts_store
-- ------------------------------------------------------
-- Server version	8.0.38

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
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sku` varchar(60) NOT NULL,
  `name` varchar(200) NOT NULL,
  `description` text,
  `category_id` bigint NOT NULL,
  `manufacturer_id` bigint NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `vat_rate` decimal(5,2) NOT NULL DEFAULT '19.00',
  `stock_qty` int NOT NULL DEFAULT '0',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sku` (`sku`),
  KEY `idx_products_name` (`name`),
  KEY `idx_products_category` (`category_id`),
  KEY `idx_products_manufacturer` (`manufacturer_id`),
  KEY `idx_products_price` (`price`),
  KEY `idx_products_stock` (`stock_qty`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `fk_product_manufacturer` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (6,'FIL-001','Oil Filter MANN W 712/95','High quality oil filter for various engines.',10,5,39.90,19.00,50,1,'2026-01-07 16:08:09',NULL),(7,'FIL-002','Air Filter Bosch S 0123','Air filter for improved airflow and protection.',10,4,59.90,19.00,24,1,'2026-01-07 16:08:09','2026-01-09 20:04:08'),(8,'SPK-001','NGK Spark Plug BKR6E','Standard spark plug for gasoline engines.',11,6,24.50,19.00,100,1,'2026-01-07 16:08:09',NULL),(9,'BRK-001','Bosch Brake Pads Front Set','Front brake pad set with low noise compound.',12,4,219.00,19.00,15,1,'2026-01-07 16:08:09',NULL),(10,'BRK-002','Bosch Brake Pads Rear Set','Rear brake pad set.',12,4,189.00,19.00,10,1,'2026-01-07 16:08:09',NULL);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-10 21:55:43
