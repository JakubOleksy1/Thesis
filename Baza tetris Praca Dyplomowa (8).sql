CREATE TABLE `Machine` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `owner_id` int,
  `addres` varchar(255),
  `status` bool
);

CREATE TABLE `Action` (
  `action_id` int PRIMARY KEY AUTO_INCREMENT,
  `machine_id` int,
  `game_action1` double,
  `game_action2` int,
  `give_prize` bool,
  `points` int
);

CREATE TABLE `User` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255)
);

ALTER TABLE `Machine` ADD FOREIGN KEY (`id`) REFERENCES `Action` (`machine_id`);

ALTER TABLE `User` ADD FOREIGN KEY (`id`) REFERENCES `Machine` (`owner_id`);
