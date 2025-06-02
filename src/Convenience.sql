drop database if exists Convenience;
CREATE DATABASE Convenience;
USE Convenience;


CREATE TABLE UserGroup (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100)
);


CREATE TABLE User (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE,
    role VARCHAR(20) DEFAULT "member",
    groupId INT,
    FOREIGN KEY (groupId) REFERENCES UserGroup(id)
);


CREATE TABLE fridge (
    fridgeId INT PRIMARY KEY,
    groupId INT,
    FOREIGN KEY (groupId) REFERENCES UserGroup(id)
);

CREATE TABLE ingredient (
  ingredientName VARCHAR(50),
  quantity DECIMAL(10, 2),
  unitType VARCHAR(20),
  ingredientId INT AUTO_INCREMENT,
  expirationDate DATE,
  fridgeId INT,
  PRIMARY KEY (ingredientId),
  FOREIGN KEY (fridgeId) REFERENCES fridge(fridgeId)
);

CREATE TABLE dish (
  dishName VARCHAR(100) UNIQUE,
  dishId INT AUTO_INCREMENT,
  instruction TEXT,
  eatTime ENUM('breakfast', 'lunch', 'dinner'),
  eatDate INT , -- 0 -> 6
  PRIMARY KEY (dishId)
);

CREATE TABLE meal_plan (
  id INT AUTO_INCREMENT,
  eatTime ENUM('breakfast', 'lunch', 'dinner'),
  eatDate INT CHECK (eatDate BETWEEN 0 AND 6),
  groupId INT,
  PRIMARY KEY (id),
  FOREIGN KEY (groupId) REFERENCES UserGroup(id)
);

CREATE TABLE dish_use_ingredient (
  dishId INT,
  ingredientName VARCHAR(50),
    quantity DECIMAL(10, 2),
    unitType VARCHAR(20),
  PRIMARY KEY (dishId, ingredientName),
  FOREIGN KEY (dishId) REFERENCES dish(dishId)
);

CREATE TABLE meal_has_dish (
  mealId INT,
  dishId INT,
  PRIMARY KEY (mealId, dishId),
  FOREIGN KEY (mealId) REFERENCES meal_plan(id),
  FOREIGN KEY (dishId) REFERENCES dish(dishId)
);

CREATE TABLE shoppingList (
  shoppingListId INT AUTO_INCREMENT,
  buyDate DATE,
  groupId INT,
  userId INT,
  PRIMARY KEY (shoppingListId),
  FOREIGN KEY (groupId) REFERENCES UserGroup(id),
  FOREIGN KEY (userId) REFERENCES User(id)
);

CREATE TABLE buy_ingredient (
  shoppingListId INT,
  ingredientName VARCHAR(50),
  quantity DECIMAL(10, 2),
  unitType VARCHAR(20),
  PRIMARY KEY (shoppingListId, ingredientName),
  FOREIGN KEY (shoppingListId) REFERENCES shoppingList(shoppingListId)
);
DELIMITER //

CREATE TRIGGER create_fridge_after_usergroup
AFTER INSERT ON UserGroup
FOR EACH ROW
BEGIN
    INSERT INTO fridge (fridgeId, groupId)
    VALUES (NEW.id, NEW.id);
END //

DELIMITER ;


INSERT INTO UserGroup (name) VALUES ('Gia đình A'), ('Nhóm bạn B');
-- INSERT INTO fridge (fridgeId, groupId) VALUES (1,1), (2,2);

INSERT INTO dish (dishName, instruction, eatTime, eatDate) VALUES
('Cơm rang dưa bò', 'Xào cơm với dưa chua và thịt bò', 'lunch', 1),
('Phở bò', 'Ninh xương bò, chần bánh phở, thêm thịt bò thái lát', 'breakfast', 0),
('Bún chả', 'Nướng chả thịt, ăn với bún và nước chấm', 'lunch', 2),
('Gà kho gừng', 'Kho gà với gừng, nước mắm và tiêu', 'dinner', 3),
('Canh chua cá', 'Nấu canh với cá, cà chua, dứa và rau thơm', 'dinner', 4),
('Bún riêu cua', 'Nấu nước dùng với cua, cà chua, đậu phụ', 'lunch', 5),
('Cơm tấm sườn', 'Nướng sườn, ăn với cơm tấm và đồ chua', 'lunch', 6),
('Bánh mì thịt', 'Kẹp thịt, pate, dưa leo và rau thơm vào bánh mì', 'breakfast', 1),
('Cháo gà', 'Nấu cháo từ gạo và gà, thêm hành lá', 'breakfast', 2),
('Mì xào bò', 'Xào mì với thịt bò và rau củ', 'lunch', 3),
('Cá kho tộ', 'Kho cá với nước mắm, tiêu và đường', 'dinner', 4),
('Rau muống xào tỏi', 'Xào rau muống với tỏi băm', 'dinner', 5),
('Trứng chiên', 'Chiên trứng gà với gia vị', 'breakfast', 0),
('Cà ri gà', 'Nấu gà với nước cốt dừa và cà ri', 'dinner', 6),
('Canh bí đỏ nấu tôm', 'Nấu bí đỏ với tôm tươi', 'dinner', 1),
('Nem rán', 'Cuốn nem và rán giòn', 'lunch', 2),
('Bánh cuốn', 'Hấp bột gạo, cuốn với thịt và mộc nhĩ', 'breakfast', 3),
('Bò lúc lắc', 'Xào thịt bò với ớt chuông và hành tây', 'dinner', 4),
('Cơm gà xối mỡ', 'Chiên gà giòn, ăn với cơm', 'lunch', 5),
('Bún bò Huế', 'Ninh xương, nấu nước dùng với sả, ớt', 'breakfast', 6);


-- 1. Cơm rang dưa bò
INSERT INTO dish_use_ingredient VALUES
(1, 'Gạo', 0.5, 'kg'),
(1, 'Thịt bò', 0.4, 'kg'),
(1, 'Dưa chua', 0.3, 'kg'),
(1, 'Trứng', 4, 'pcs');

-- 2. Phở bò
INSERT INTO dish_use_ingredient VALUES
(2, 'Xương bò', 1.0, 'kg'),
(2, 'Thịt bò thăn', 0.5, 'kg'),
(2, 'Bánh phở', 0.4, 'kg'),
(2, 'Hành lá', 0.1, 'kg');

-- 3. Bún chả
INSERT INTO dish_use_ingredient VALUES
(3, 'Thịt ba chỉ', 0.6, 'kg'),
(3, 'Bún', 0.6, 'kg'),
(3, 'Nước mắm', 0.1, 'l'),
(3, 'Tỏi', 0.05, 'kg');

-- 4. Gà kho gừng
INSERT INTO dish_use_ingredient VALUES
(4, 'Thịt gà', 0.8, 'kg'),
(4, 'Gừng', 0.1, 'kg'),
(4, 'Nước mắm', 0.1, 'l');

-- 5. Canh chua cá
INSERT INTO dish_use_ingredient VALUES
(5, 'Cá', 0.6, 'kg'),
(5, 'Dứa', 0.3, 'kg'),
(5, 'Cà chua', 0.2, 'kg'),
(5, 'Rau thơm', 0.05, 'kg');

-- 6. Bún riêu cua
INSERT INTO dish_use_ingredient VALUES
(6, 'Cua đồng', 0.5, 'kg'),
(6, 'Bún', 0.6, 'kg'),
(6, 'Đậu phụ', 0.3, 'kg'),
(6, 'Cà chua', 0.2, 'kg');

-- 7. Cơm tấm sườn
INSERT INTO dish_use_ingredient VALUES
(7, 'Gạo tấm', 0.6, 'kg'),
(7, 'Sườn heo', 0.6, 'kg'),
(7, 'Dưa leo', 0.3, 'kg'),
(7, 'Nước mắm', 0.1, 'l');

-- 8. Bánh mì thịt
INSERT INTO dish_use_ingredient VALUES
(8, 'Bánh mì', 4, 'pcs'),
(8, 'Thịt nguội', 0.4, 'kg'),
(8, 'Pate', 0.2, 'kg'),
(8, 'Dưa leo', 0.2, 'kg');

-- 9. Cháo gà
INSERT INTO dish_use_ingredient VALUES
(9, 'Gạo', 0.4, 'kg'),
(9, 'Thịt gà', 0.6, 'kg'),
(9, 'Hành lá', 0.1, 'kg'),
(9, 'Nước', 2.0, 'l');

-- 10. Mì xào bò
INSERT INTO dish_use_ingredient VALUES
(10, 'Mì sợi', 0.5, 'kg'),
(10, 'Thịt bò', 0.5, 'kg'),
(10, 'Rau cải', 0.3, 'kg'),
(10, 'Tỏi', 0.05, 'kg');

-- 11. Cá kho tộ
INSERT INTO dish_use_ingredient VALUES
(11, 'Cá basa', 0.6, 'kg'),
(11, 'Nước mắm', 0.1, 'l'),
(11, 'Đường', 0.05, 'kg'),
(11, 'Tiêu', 0.01, 'kg');

-- 12. Rau muống xào tỏi
INSERT INTO dish_use_ingredient VALUES
(12, 'Rau muống', 0.6, 'kg'),
(12, 'Tỏi', 0.05, 'kg'),
(12, 'Dầu ăn', 0.1, 'l');

-- 13. Trứng chiên
INSERT INTO dish_use_ingredient VALUES
(13, 'Trứng gà', 4, 'pcs'),
(13, 'Hành lá', 0.05, 'kg'),
(13, 'Dầu ăn', 0.05, 'l');

-- 14. Cà ri gà
INSERT INTO dish_use_ingredient VALUES
(14, 'Thịt gà', 0.8, 'kg'),
(14, 'Nước cốt dừa', 0.3, 'l'),
(14, 'Bột cà ri', 0.05, 'kg'),
(14, 'Khoai tây', 0.3, 'kg');

-- 15. Canh bí đỏ nấu tôm
INSERT INTO dish_use_ingredient VALUES
(15, 'Bí đỏ', 0.6, 'kg'),
(15, 'Tôm', 0.4, 'kg'),
(15, 'Hành lá', 0.05, 'kg');

-- 16. Nem rán
INSERT INTO dish_use_ingredient VALUES
(16, 'Thịt lợn xay', 0.5, 'kg'),
(16, 'Bánh đa nem', 10, 'pcs'),
(16, 'Miến', 0.2, 'kg'),
(16, 'Cà rốt', 0.2, 'kg');

-- 17. Bánh cuốn
INSERT INTO dish_use_ingredient VALUES
(17, 'Bột gạo', 0.4, 'kg'),
(17, 'Thịt lợn xay', 0.3, 'kg'),
(17, 'Mộc nhĩ', 0.1, 'kg'),
(17, 'Hành khô', 0.05, 'kg');

-- 18. Bò lúc lắc
INSERT INTO dish_use_ingredient VALUES
(18, 'Thịt bò', 0.6, 'kg'),
(18, 'Ớt chuông', 0.3, 'kg'),
(18, 'Hành tây', 0.2, 'kg'),
(18, 'Tỏi', 0.05, 'kg');

-- 19. Cơm gà xối mỡ
INSERT INTO dish_use_ingredient VALUES
(19, 'Gạo', 0.5, 'kg'),
(19, 'Đùi gà', 4, 'pcs'),
(19, 'Dầu ăn', 0.5, 'l'),
(19, 'Dưa leo', 0.3, 'kg');

-- 20. Bún bò Huế
INSERT INTO dish_use_ingredient VALUES
(20, 'Xương bò', 1.0, 'kg'),
(20, 'Thịt bò nạm', 0.6, 'kg'),
(20, 'Bún', 0.6, 'kg'),
(20, 'Sả', 0.1, 'kg');

INSERT INTO ingredient (ingredientName, quantity, unitType, expirationDate, fridgeId) VALUES
('Gạo', 2.0, 'kg', DATE_ADD(CURDATE(), INTERVAL 20 DAY), 1),
('Thịt bò', 1.0, 'kg', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 1),
('Thịt bò thăn', 1.2, 'kg', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1),
('Thịt gà', 1.5, 'kg', DATE_ADD(CURDATE(), INTERVAL 8 DAY), 1),
('Đùi gà', 1.2, 'kg', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 1),
('Thịt lợn xay', 1.0, 'kg', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 1),
('Thịt ba chỉ', 1.0, 'kg', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 1),
('Cá basa', 0.8, 'kg', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 1),
('Cá', 1.0, 'kg', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1),
('Cua đồng', 0.6, 'kg', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1),
('Tôm', 0.8, 'kg', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1),
('Trứng', 12, 'pcs', DATE_ADD(CURDATE(), INTERVAL 15 DAY), 1),
('Trứng gà', 8, 'pcs', DATE_ADD(CURDATE(), INTERVAL 14 DAY), 1),
('Bánh phở', 0.8, 'kg', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1),
('Bún', 1.5, 'kg', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1),
('Mì sợi', 1.0, 'kg', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1),
('Gừng', 0.3, 'kg', DATE_ADD(CURDATE(), INTERVAL 12 DAY), 1),
('Hành lá', 0.2, 'kg', DATE_ADD(CURDATE(), INTERVAL 8 DAY), 1),
('Hành khô', 0.3, 'kg', DATE_ADD(CURDATE(), INTERVAL 25 DAY), 1),
('Hành tây', 0.4, 'kg', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1),
('Tỏi', 0.2, 'kg', DATE_ADD(CURDATE(), INTERVAL 20 DAY), 1),
('Ớt chuông', 0.5, 'kg', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 1),
('Rau muống', 1.0, 'kg', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1),
('Rau cải', 0.8, 'kg', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1),
('Rau thơm', 0.2, 'kg', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1),
('Cà chua', 0.5, 'kg', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 1),
('Cà rốt', 0.5, 'kg', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1),
('Khoai tây', 0.7, 'kg', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1),
('Dứa', 0.6, 'kg', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1),
('Dưa leo', 0.6, 'kg', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 1),
('Pate', 0.5, 'kg', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1),
('Thịt nguội', 0.5, 'kg', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 1),
('Miến', 0.5, 'kg', DATE_ADD(CURDATE(), INTERVAL 20 DAY), 1),
('Bột gạo', 1.0, 'kg', DATE_ADD(CURDATE(), INTERVAL 25 DAY), 1),
('Mộc nhĩ', 0.2, 'kg', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 1),
('Nước mắm', 1.0, 'l', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 1),
('Dầu ăn', 1.5, 'l', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 1),
('Đường', 1.0, 'kg', DATE_ADD(CURDATE(), INTERVAL 90 DAY), 1),
('Tiêu', 0.1, 'kg', DATE_ADD(CURDATE(), INTERVAL 90 DAY), 1),
('Sả', 0.2, 'kg', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1),
('Nước cốt dừa', 0.5, 'l', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1),
('Bánh đa nem', 20, 'pcs', DATE_ADD(CURDATE(), INTERVAL 15 DAY), 1);