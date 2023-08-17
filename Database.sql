CREATE TABLE `Machine` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int,
  `gave_prize` bool,
  `addres` varchar(255)
);

CREATE TABLE `Action` (
  `action_id` int PRIMARY KEY AUTO_INCREMENT,
  `machine_id` int,
  `points` int,
  `pause` bool,
  `give_prize` bool,
  `code` varchar(255),
  `is_used` bool
);

CREATE TABLE `User` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(255),
  `password` varchar(255),
  `created_at` timestamp
);

ALTER TABLE `Action` ADD FOREIGN KEY (`machine_id`) REFERENCES `Machine` (`id`);

ALTER TABLE `Machine` ADD FOREIGN KEY (`user_id`) REFERENCES `User` (`id`);
