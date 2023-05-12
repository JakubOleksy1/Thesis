CREATE TABLE `users` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(255),
  `created_at` timestamp
);

CREATE TABLE `vendmachine` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `status` bool,
  `game_id` integer,
  `created_at` timestamp
);

CREATE TABLE `game` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `user_id` integer,
  `points` integer,
  `duringgame` bool,
  `prize` bool,
  `created_at` timestamp
);

ALTER TABLE `game` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `vendmachine` ADD FOREIGN KEY (`game_id`) REFERENCES `game` (`id`);

