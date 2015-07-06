# restaurant schema
# --- !Ups

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `address_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `address_id` (`address_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`) ON DELETE CASCADE
) ;


CREATE TABLE `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `house_number` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `address_ibfk_2` (`user_id`)
);

CREATE TABLE `TableSit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `maxCapacity` int(11) DEFAULT NULL,
  `minCapacity` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `occupied` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `products` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `price` float DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `name` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE `PaymentType` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typeffPayment` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;
CREATE TABLE `CustPaymentOrder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customerOrderID` int(10) NOT NULL,
  `paymentTypeID` int(11) DEFAULT NULL,
  `totalPrice` float DEFAULT NULL,
  `customerId` int(11) DEFAULT NULL,
  `pricePaid` float DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `CustOrder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `number_of_people` int(11) DEFAULT NULL,
  `server_Id` int(11) DEFAULT NULL,
  `table_Id` int(11) DEFAULT NULL,
  `user_Id` int(11) DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `custorder_ibfk_t` (`user_Id`)
);


CREATE TABLE `CustomerOrderItems` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(11) DEFAULT NULL,
  `item_quantity` int(11) DEFAULT NULL,
  `customerOrder_Id` int(11) DEFAULT NULL,
  `comments` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `customerOrder_Id` (`customerOrder_Id`),
  CONSTRAINT `customerorderitems_ibfk_1` FOREIGN KEY (`customerOrder_Id`) REFERENCES `custOrder` (`id`)
) ;


# --- !Downs
Drop Table user;
Drop Table address;

