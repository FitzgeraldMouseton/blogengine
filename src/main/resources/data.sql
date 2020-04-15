Insert into users(id, code, email, is_moderator, name, password, photo, reg_time) values
(1, '0000', 'ernest@gmail.com', 0, 'Ernest Hemingway', '123456', null, '2019-12-26 00:00:01'),
(2, '0000', 'gleb@gmail.com', 0, 'Gleb Uspenskiy', '123456', null, '2019-02-15 00:00:01'),
(3, '0000', 'gabriel@gmail.com', 0, 'Gabriel Markes', '123456', null, '2019-01-26 00:00:01'),
(4, '0000', 'flannery@gmail.com', 0, 'Flannery O\'Connor', '123456', null, '2019-03-08 00:00:01'),
(5, '0000', 'franz@gmail.com', 0, 'Franz Kafka', '123456', null, '2020-03-01 00:00:01');

Insert into posts(id, is_active, moderation_status, title, time, text, view_count, moderator_id, user_id) values
(1, 1, 'ACCEPTED', "По ком звонит колокол", '2020-04-01 00:00:01', 'Он лежал на устланной сосновыми иглами бурой земле...', 5, 3, 1),
(2, 1, 'ACCEPTED', "Нравы Растеряевой улицы", '2020-04-02 00:00:01', 'В городе Т. существует Растеряева улица. Принадлежа к числу захолустий...', 10, 3, 2),
(3, 1, 'ACCEPTED', "Сто лет одиночества", '2020-04-03 00:00:01', 'Много лет спустя, перед самым расстрелом, полковник Аурелиано Буэндия...', 15, 3, 3),
(4, 1, 'ACCEPTED', "Царство Небесное силою берется", '2020-04-05 00:00:01', 'Не прошло и дня с тех пор, как дед Фрэнсиса Мариона Таруотера помер...', 20, 3, 4),
(5, 1, 'ACCEPTED', "Замок", '2020-04-06 00:00:01', 'К. прибыл  поздно вечером...', 50, 3, 5),
(6, 0, 'NEW', "Превращение", '2020-04-07 00:00:01', 'Проснувшись однажды утром после беспокойного сна...', 45, 3, 5),
(7, 1, 'ACCEPTED', 'Старик и море', '2020-04-08 00:00:01', 'Старик рыбачил один  на своей  лодке в Гольфстриме...', 40, 3, 1),
(8, 1, 'ACCEPTED', "Процесс", '2020-04-09 00:00:01', 'Кто-то, по-видимому, оклеветал Йозефа К...', 35, 3, 5),
(9, 0, 'NEW', "Осень патриарха", '2020-04-10 00:00:01', 'На  исходе  недели  стервятники-грифы  разодрали  металлические оконные сетки...', 30, 3, 3),
(10, 1, 'ACCEPTED', "Власть земли", '2020-04-11 00:00:01', 'Морозный зимний день в полном блеске...', 25, 3, 2);

Insert into post_comments(id, text, time, comment_id, post_id, user_id) values
(1, '11111', now(), null, 1, 1),
(2, '22222', now(), null, 1, 2),
(3, '33333', now(), null, 1, 3),
(4, '44444', now(), null, 2, 4),
(5, '55555', now(), null, 4, 5),
(6, '66666', now(), null, 4, 2),
(7, '77777', now(), null, 4, 4),
(8, '88888', now(), null, 4, 2),
(9, '99999', now(), null, 4, 1);

Insert into post_votes(id, time, value, post_id, user_id) values
(1, now(), 1, 1, 1),
(2, now(), -1, 1, 2),
(3, now(), -1, 1, 3),
(4, now(), 1, 2, 5),
(5, now(), 1, 4, 2),
(6, now(), 1, 4, 1),
(7, now(), 1, 5, 4),
(8, now(), 1, 5, 4),
(9, now(), -1, 5, 1),
(10, now(), 1, 6, 2),
(11, now(), 1, 8, 3),
(12, now(), 1, 8, 2),
(13, now(), 1, 8, 1),
(14, now(), 1, 8, 4);

Insert into tags(id, name) values
(1, 'книга'),
(2, 'хэмингуэй'),
(3, 'успенский'),
(4, 'маркес'),
(5, 'о\'коннор'),
(6, 'кафка');

Insert into posts_tags(posts_id, tags_id) values
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 1),
(1, 2),
(2, 3),
(3, 4),
(4, 5),
(5, 6),
(6, 6),
(7, 2),
(8, 6),
(9, 4),
(10, 3);


