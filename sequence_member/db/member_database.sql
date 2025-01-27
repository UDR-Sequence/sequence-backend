-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: db
-- 생성 시간: 24-12-03 14:02
-- 서버 버전: 11.4.4-MariaDB-ubu2404
-- PHP 버전: 8.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 데이터베이스: `member_database`
--

-- --------------------------------------------------------

--
-- 테이블 구조 `award`
--

CREATE TABLE `award` (
  `award_id` bigint(20) NOT NULL,
  `award_description` varchar(255) DEFAULT NULL,
  `award_duration` date DEFAULT NULL,
  `award_name` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- 테이블의 덤프 데이터 `award`
--

INSERT INTO `award` (`award_id`, `award_description`, `award_duration`, `award_name`, `user_id`) VALUES
(1, '억까당하여 수상하지 못하였다', '2020-11-12', '멋사 중앙해커톤 대상', NULL),
(2, '억까당하여 수상하지 못하였다', '2020-11-12', '멋사 중앙해커톤 대상', NULL),
(3, '억까당하여 수상하지 못하였다', '2020-11-12', '멋사 중앙해커톤 대상', NULL),
(4, '억까당하여 수상하지 못하였다', '2020-11-12', '멋사 중앙해커톤 대상', NULL),
(5, '억까당하여 수상하지 못하였다', '2020-11-12', '멋사 중앙해커톤 대상', NULL),
(6, '억까당하여 수상하지 못하였다', '2020-11-12', '멋사 중앙해커톤 대상', NULL),
(7, '억까당하여 수상하지 못하였다', '2020-11-12', '멋사 중앙해커톤 대상', NULL),
(8, '억까당하여 수상하지 못하였다', '2020-11-12', '멋사 중앙해커톤 대상', NULL),
(9, '억까당하여 수상하지 못하였다', '2020-11-12', '멋사 중앙해커톤 대상', NULL);

-- --------------------------------------------------------

--
-- 테이블 구조 `career`
--

CREATE TABLE `career` (
  `career_id` bigint(20) NOT NULL,
  `career_description` varchar(255) DEFAULT NULL,
  `career_duration` date DEFAULT NULL,
  `career_name` varchar(100) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- 테이블의 덤프 데이터 `career`
--

INSERT INTO `career` (`career_id`, `career_description`, `career_duration`, `career_name`, `user_id`) VALUES
(1, '재미있는 밤샘 개발', '2020-11-12', '멋사 중앙 해커톤', NULL),
(2, '재미있는 밤샘 개발', '2020-11-12', '멋사 중앙 해커톤', NULL),
(3, '재미있는 밤샘 개발', '2020-11-12', '멋사 중앙 해커톤', NULL),
(4, '재미있는 밤샘 개발', '2020-11-12', '멋사 중앙 해커톤', NULL),
(5, '재미있는 밤샘 개발', '2020-11-12', '멋사 중앙 해커톤', NULL),
(6, '재미있는 밤샘 개발', '2020-11-12', '멋사 중앙 해커톤', NULL),
(7, '재미있는 밤샘 개발', '2020-11-12', '멋사 중앙 해커톤', NULL),
(8, '재미있는 밤샘 개발', '2020-11-12', '멋사 중앙 해커톤', NULL),
(9, '재미있는 밤샘 개발', '2020-11-12', '멋사 중앙 해커톤', NULL);

-- --------------------------------------------------------

--
-- 테이블 구조 `education`
--

CREATE TABLE `education` (
  `education_id` bigint(20) NOT NULL,
  `degree` enum('DOCTORATE','DROPOUT','ENROLLMENT','EXPELLED','GRADUATION','LEAVE_OF_ABSENCE','MASTER') NOT NULL,
  `desired_job` enum('BACK_END','FRONT_END','PM','UI_UX_DESIGN') NOT NULL,
  `entrance_date` date DEFAULT NULL,
  `graduation_date` date DEFAULT NULL,
  `major` varchar(50) NOT NULL,
  `school_name` varchar(100) NOT NULL,
  `skill_category` enum('ADOBE_ILLUSTRATOR','ADOBE_INDESIGN','ADOBE_PHOTOSHOP','JAVASCRIPT','TYPESCRIPT') NOT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- 테이블의 덤프 데이터 `education`
--

INSERT INTO `education` (`education_id`, `degree`, `desired_job`, `entrance_date`, `graduation_date`, `major`, `school_name`, `skill_category`, `user_id`) VALUES
(1, 'ENROLLMENT', 'FRONT_END', '2019-03-01', '2025-02-28', '컴퓨터융합소프트웨어학과', '고려대학교', 'JAVASCRIPT', NULL),
(2, 'ENROLLMENT', 'FRONT_END', '2019-03-01', '2025-02-28', '컴퓨터융합소프트웨어학과', '고려대학교', 'JAVASCRIPT', NULL),
(3, 'ENROLLMENT', 'FRONT_END', '2019-03-01', '2025-02-28', '컴퓨터융합소프트웨어학과', '고려대학교', 'JAVASCRIPT', NULL),
(4, 'ENROLLMENT', 'FRONT_END', '2019-03-01', '2025-02-28', '컴퓨터융합소프트웨어학과', '고려대학교', 'JAVASCRIPT', NULL),
(5, 'ENROLLMENT', 'FRONT_END', '2019-03-01', '2025-02-28', '컴퓨터융합소프트웨어학과', '고려대학교', 'JAVASCRIPT', NULL),
(6, 'ENROLLMENT', 'FRONT_END', '2019-03-01', '2025-02-28', '컴퓨터융합소프트웨어학과', '고려대학교', 'JAVASCRIPT', NULL),
(7, 'ENROLLMENT', 'FRONT_END', '2019-03-01', '2025-02-28', '컴퓨터융합소프트웨어학과', '고려대학교', 'JAVASCRIPT', NULL),
(8, 'ENROLLMENT', 'FRONT_END', '2019-03-01', '2025-02-28', '컴퓨터융합소프트웨어학과', '고려대학교', 'JAVASCRIPT', NULL),
(9, 'ENROLLMENT', 'FRONT_END', '2019-03-01', '2025-02-28', '컴퓨터융합소프트웨어학과', '고려대학교', 'JAVASCRIPT', NULL);

-- --------------------------------------------------------

--
-- 테이블 구조 `experience`
--

CREATE TABLE `experience` (
  `experience_id` bigint(20) NOT NULL,
  `activity_description` varchar(255) DEFAULT NULL,
  `activity_duration` date DEFAULT NULL,
  `activity_name` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- 테이블의 덤프 데이터 `experience`
--

INSERT INTO `experience` (`experience_id`, `activity_description`, `activity_duration`, `activity_name`, `user_id`) VALUES
(1, '재미있는 개발동아리', '2020-11-11', '언더독레볼루션', NULL),
(2, '재미있는 개발동아리', '2020-11-11', '언더독레볼루션', NULL),
(3, '재미있는 개발동아리', '2020-11-11', '언더독레볼루션', NULL),
(4, '재미있는 개발동아리', '2020-11-11', '언더독레볼루션', NULL),
(5, '재미있는 개발동아리', '2020-11-11', '언더독레볼루션', NULL),
(6, '재미있는 개발동아리', '2020-11-11', '언더독레볼루션', NULL),
(7, '재미있는 개발동아리', '2020-11-11', '언더독레볼루션', NULL),
(8, '재미있는 개발동아리', '2020-11-11', '언더독레볼루션', NULL),
(9, '재미있는 개발동아리', '2020-11-11', '언더독레볼루션', NULL);

-- --------------------------------------------------------

--
-- 테이블 구조 `user`
--

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `address` varchar(150) NOT NULL,
  `birth` date NOT NULL,
  `email` varchar(45) NOT NULL,
  `gender` enum('M','F') NOT NULL,
  `introduction` varchar(255) NOT NULL,
  `name` varchar(45) NOT NULL,
  `phone` varchar(11) NOT NULL,
  `user_id` varchar(45) NOT NULL,
  `user_pwd` varchar(150) NOT NULL,
  `web_url` varchar(150) DEFAULT NULL,
  `random_key` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- 테이블의 덤프 데이터 `user`
--

INSERT INTO `user` (`id`, `address`, `birth`, `email`, `gender`, `introduction`, `name`, `phone`, `user_id`, `user_pwd`, `web_url`, `random_key`) VALUES
(1, '123 Example St', '1990-01-01', 'gw2000sk12@example.com', 'M', '안녕방가방가', 'John Doe', '01012345678', 'gw2000sk12', 'examplePassword', 'www.google.com', NULL),
(2, '123 Example St', '1990-01-01', 'gw2000sk123@example.com', 'M', '안녕방가방가', 'John Doe', '01012345678', 'gw2000sk123', 'examplePassword', 'www.google.com', NULL),
(3, '123 Example St', '1990-01-01', 'gw2000sk1234@example.com', 'M', '안녕방가방가', 'John Doe', '01012345678', 'gw2000sk1234', 'examplePassword', 'www.google.com', NULL),
(4, '123 Example St', '1990-01-01', 'gw2000sk25@example.com', 'M', '안녕방가방가', 'John Doe', '01012345678', 'gw2000sk25', 'examplePassword', 'www.google.com', NULL),
(5, '123 Example St', '1990-01-01', 'gw2000sk253@example.com', 'M', '안녕방가방가', 'John Doe', '01012345678', 'gw2000sk253', 'examplePassword', 'www.google.com', NULL),
(6, '123 Example St', '1990-01-01', 'gw2000sk1253@example.com', 'M', '안녕방가방가', 'John Doe', '01012345678', 'gw2000sk2153', 'examplePassword', 'www.google.com', NULL),
(7, '123 Example St', '1990-01-01', 'gw2000sk23@example.com', 'M', '안녕방가방가', 'John Doe', '01012345678', 'gw2000sk23', 'examplePassword', 'www.google.com', '0713c8ad-ee95-4054-8fd0-8af6efbb09df'),
(8, '123 Example St', '1990-01-01', 'gw2000sk223@example.com', 'M', '안녕방가방가', 'John Doe', '01012345678', 'gw2000sk223', 'examplePassword', 'www.google.com', '455a7c96-5f11-4a21-88a3-8166bcace588'),
(9, '123 Example St', '1990-01-01', 'pgw223@example.com', 'M', '안녕방가방가', 'John Doe', '01012345678', 'pgw', 'examplePassword', 'www.google.com', NULL);

--
-- 덤프된 테이블의 인덱스
--

--
-- 테이블의 인덱스 `award`
--
ALTER TABLE `award`
  ADD PRIMARY KEY (`award_id`),
  ADD KEY `FKr1of3u0w63w4ihesjbpiivuut` (`user_id`);

--
-- 테이블의 인덱스 `career`
--
ALTER TABLE `career`
  ADD PRIMARY KEY (`career_id`),
  ADD KEY `FKp68p71rciqjtwly9h9auk0vyi` (`user_id`);

--
-- 테이블의 인덱스 `education`
--
ALTER TABLE `education`
  ADD PRIMARY KEY (`education_id`),
  ADD UNIQUE KEY `UK9f4iojhis1eml9hi90aowdpyp` (`user_id`);

--
-- 테이블의 인덱스 `experience`
--
ALTER TABLE `experience`
  ADD PRIMARY KEY (`experience_id`),
  ADD KEY `FK41lup37auw1bvwwqpgn0blbic` (`user_id`);

--
-- 테이블의 인덱스 `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  ADD UNIQUE KEY `UKa3imlf41l37utmxiquukk8ajc` (`user_id`);

--
-- 덤프된 테이블의 AUTO_INCREMENT
--

--
-- 테이블의 AUTO_INCREMENT `award`
--
ALTER TABLE `award`
  MODIFY `award_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- 테이블의 AUTO_INCREMENT `career`
--
ALTER TABLE `career`
  MODIFY `career_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- 테이블의 AUTO_INCREMENT `education`
--
ALTER TABLE `education`
  MODIFY `education_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- 테이블의 AUTO_INCREMENT `experience`
--
ALTER TABLE `experience`
  MODIFY `experience_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- 테이블의 AUTO_INCREMENT `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- 덤프된 테이블의 제약사항
--

--
-- 테이블의 제약사항 `award`
--
ALTER TABLE `award`
  ADD CONSTRAINT `FKr1of3u0w63w4ihesjbpiivuut` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- 테이블의 제약사항 `career`
--
ALTER TABLE `career`
  ADD CONSTRAINT `FKp68p71rciqjtwly9h9auk0vyi` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- 테이블의 제약사항 `education`
--
ALTER TABLE `education`
  ADD CONSTRAINT `FKaw3ebf3585a1ndgqnk6k6hosc` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- 테이블의 제약사항 `experience`
--
ALTER TABLE `experience`
  ADD CONSTRAINT `FK41lup37auw1bvwwqpgn0blbic` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
