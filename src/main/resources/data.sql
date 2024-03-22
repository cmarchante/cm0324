-- Insert into 'rates' only if it is empty
INSERT INTO `rates` (`type`, `daily_charge`, `weekday_charge`, `weekend_charge`, `holiday_charge`)
SELECT 'Ladder', 1.99, 1, 1, 0 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `rates`)
UNION ALL
SELECT 'Chainsaw', 1.49, 1, 0, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `rates`)
UNION ALL
SELECT 'Jackhammer', 2.99, 1, 0, 0 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `rates`);

-- Insert into 'products' only if it is empty
INSERT INTO `products` (`name`, `description`, `rate_id`, `brand`, `version`, `sku`, `available`)
SELECT 'Chainsaw', 'Large Chainsaw', 2, 'Stihl', 1, 'CHNS', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `products`)
UNION ALL
SELECT 'Ladder', 'A 12-foot Ladder', 1, 'Werner', 1, 'LADW', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `products`)
UNION ALL
SELECT 'Jackhammer A', 'Strongest Hammer', 3, 'DeWalt', 1, 'JAKD', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `products`)
UNION ALL
SELECT 'Jackhammer B', 'Strong Hammer', 3, 'Ridgid', 1, 'JAKR', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `products`);

-- Insert into 'customers' only if it is empty
INSERT INTO `customers` (`first_name`, `last_name`, `email`, `password`, `address`, `phone`, `state`, `zip`)
SELECT 'John', 'Doe', 'john.doe@example.com', 'password123', '123 Elm Street', '1234567890', 'CA', '90210' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `customers`)
UNION ALL
SELECT 'Jane', 'Smith', 'jane.smith@example.com', 'password123', '456 Maple Avenue', '0987654321', 'NY', '10001' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `customers`)
UNION ALL
SELECT 'Alice', 'Johnson', 'alice.johnson@example.com', 'password123', '789 Oak Lane', '2345678901', 'TX', '73301' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `customers`);