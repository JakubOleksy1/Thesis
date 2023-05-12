CREATE TABLE `users` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(255),
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

CREATE TABLE `vendmachine` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `game_id` integer,
  `status` bool,
  `created_at` timestamp
);

ALTER TABLE `game` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `game` ADD FOREIGN KEY (`id`) REFERENCES `vendmachine` (`game_id`);
