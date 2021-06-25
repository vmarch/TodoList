-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 22. Jun 2021 um 12:54
-- Server-Version: 10.4.19-MariaDB
-- PHP-Version: 8.0.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `java2`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `todoliste`
--

DROP TABLE IF EXISTS `todoliste`;
CREATE TABLE IF NOT EXISTS `todoliste` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(40) COLLATE utf8_bin NOT NULL,
  `task` varchar(50) COLLATE utf8_bin NOT NULL,
  `deadline` date NOT NULL,
  `priority` int(2) NOT NULL,
  `state` int(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Daten für Tabelle `todoliste`
--

INSERT INTO `todoliste` (`id`, `title`, `task`, `deadline`, `priority`, `state`) VALUES
(1, 'Einkaufen', 'Kartoffeln', '2021-06-23', 0, 0),
(2, 'Java Lernen', 'Pattern finden', '2021-06-23', 0, 0),
(3, 'Java Lernen', 'Pattern lesen', '2021-06-23', 0, 0),
(4, 'Java Lernen', 'Pattern finden', '2021-06-23', 0, 0),
(5, 'Java Lernen', 'Pattern lesen', '2021-06-23', 0, 0);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
