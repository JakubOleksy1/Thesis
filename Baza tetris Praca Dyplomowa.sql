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

ALTER TABLE `Action` ADD FOREIGN KEY (`machine_id`) REFERENCES `Machine` (`id`);

ALTER TABLE `Machine` ADD FOREIGN KEY (`owner_id`) REFERENCES `User` (`id`);
