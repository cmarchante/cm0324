CREATE TABLE IF NOT EXISTS `rates` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `type` VARCHAR(100) NOT NULL,
    `daily_charge` DECIMAL(10, 2) NOT NULL,
    `weekday_charge` TINYINT(1) NOT NULL,
    `weekend_charge` TINYINT(1) NOT NULL,
    `holiday_charge` TINYINT(1) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `type_idx` (`type`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `products` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                                          `name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(500),
    `rate_id` BIGINT NOT NULL,
    `brand` VARCHAR(50) NOT NULL,
    `version` BIGINT NOT NULL,
    `sku` VARCHAR(64),
    `available` TINYINT(1) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`rate_id`) REFERENCES `rates`(`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `customers` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                                           `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(320),
    `password` VARCHAR(50),
    `address` VARCHAR(200) NOT NULL,
    `phone` VARCHAR(10) NOT NULL,
    `state` VARCHAR(2) NOT NULL,
    `zip` VARCHAR(10) NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `rentals` (
                                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                                         `product_id` BIGINT NOT NULL,
                                         `customer_id` BIGINT NOT NULL,
                                         `rented_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `expected_return_time` TIMESTAMP NULL,
                                         `return_time` TIMESTAMP NULL,
                                         `discount` INT NOT NULL,
                                         PRIMARY KEY (`id`),
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`),
    FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
