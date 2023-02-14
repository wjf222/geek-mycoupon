delimiter ?
CREATE DEFINER=`root`@`%` FUNCTION `randStr`(n INT) RETURNS varchar(255) CHARSET utf8mb4
     DETERMINISTIC
     BEGIN
     DECLARE chars_str varchar(100) DEFAULT 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
     DECLARE return_str varchar(255) DEFAULT '' ;
     DECLARE i INT DEFAULT 0;
     WHILE i < n DO
             SET return_str = concat(return_str, substring(chars_str, FLOOR(1 + RAND() * 62), 1));
         SET i = i + 1;
     END WHILE;
    RETURN return_str;
END ?


CREATE DEFINER=`root`@`%` FUNCTION `randDataTime`(sd DATETIME,ed DATETIME) RETURNS datetime
         DETERMINISTIC
     BEGIN
         DECLARE sub INT DEFAULT 0;
     DECLARE ret DATETIME;
     SET sub = ABS(UNIX_TIMESTAMP(ed)-UNIX_TIMESTAMP(sd));
     SET ret = DATE_ADD(sd,INTERVAL FLOOR(1+RAND()*(sub-1)) SECOND);
     RETURN ret;
END ?

delimiter ;

CREATE DEFINER=`root`@`%` PROCEDURE `add_t_user_memory`(IN n int)
     BEGIN
         DECLARE i INT DEFAULT 1;
     WHILE (i <= n) DO
         INSERT INTO coupon_template (available, name, description,type, shop_id,created_time,rule) VALUES (uuid(), randStr(20), FLOOR(RAND() * 1000), FLOOR(RAND() * 100), NOW(),randStr(20));
         SET i = i + 1;
     END WHILE;
 END
?