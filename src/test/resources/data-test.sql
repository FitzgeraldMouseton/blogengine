Insert into USERS (ID, CODE, EMAIL, IS_MODERATOR, NAME, PASSWORD, PHOTO, REG_TIME) values
(1, '0000', 'ernest@gmail.com', 0, 'Эрнест Хемингуэй', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', '/avatars/uxyv/rmwm/ocwf/photo.jpg', '2019-12-26 00:00:01'),
(2, '0000', 'gleb@gmail.com', 0, 'Глеб Успенский', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-02-15 00:00:01'),
(3, '0000', 'gabriel@gmail.com', 1, 'Габриэль Гарсия Маркес', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-01-26 00:00:01'),
(4, '0000', 'flannery@gmail.com', 0, 'Фланнери О''Коннор', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-03-08 00:00:01'),
(5, '0000', 'franz@gmail.com', 0, 'Франц Кафка', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2020-03-01 00:00:01');

Insert into posts(id, is_active, moderation_status, title, time, text, view_count, moderator_id, user_id) values
(1, 1, 'ACCEPTED', 'По ком звонит колокол', '2020-04-06 00:00:01', 'Он лежал на устланной сосновыми иглами бурой земле', 5, 3, 1),
(2, 1, 'ACCEPTED', 'Нравы Растеряевой улицы', '2020-04-05 00:06:01', 'В городе Т. существует Растеряева улица. Принадлежа к числу захолустий', 10, 3, 2),
(3, 1, 'ACCEPTED', 'Сто лет одиночества', '2020-04-01 10:56:01', 'Много лет спустя, перед самым расстрелом, полковник Аурелиано Буэндия', 15, 3, 3),
(4, 1, 'ACCEPTED', 'Царство Небесное силою берется', '2020-04-01 00:00:01', 'Не прошло и дня с тех пор, как дед Фрэнсиса Мариона Таруотера помер', 20, 3, 4),
(5, 1, 'DECLINE', 'Крестьянин и крестьянский труд', '2020-04-02 00:00:01', 'Вот уже почти год, как я живу в деревне и нахожусь в ежедневном общении с хорошей крестьянской семьей', 50, 3, 2),
(6, 0, 'ACCEPTED', 'Будка', '2020-03-11 00:00:01', ' На углу двух весьма глухих и бедных переулков уездного города', 7, 3, 2),
(7, 1, 'NEW', 'Земной рай', '2020-03-16 00:00:01', 'В числе знакомых Нади было, между прочим, семейство Печкиных', 8, 3, 2),
(8, 1, 'ACCEPTED', 'Власть земли', '2020-03-12 00:00:01', 'Морозный зимний день в полном блеске', 25, 3, 2);

Insert into post_comments (id, text, time, parent_id, post_id, user_id) values
(1, '11111', now(), null, 1, 1),
(2, '22222', now(), null, 1, 3),

(3, '33333', now(), null, 2, 4),
(4, '44444', now(), null, 3, 4),
(5, '55555', now(), null, 3, 5),
(6, '66666', now(), null, 3, 2),
(7, '77777', now(), null, 3, 4),
(8, '88888', now(), null, 4, 2),
(9, '99999', now(), null, 4, 1),
(10, '11111', now(), null, 5, 1),
(11, '22222', now(), null, 5, 2),
(12, '22222', now(), null, 6, 2),
(13, '22222', now(), null, 6, 2),
(14, '22222', now(), null, 7, 2),
(15, '22222', now(), null, 7, 2),
(16, '22222', now(), null, 8, 2),
(17, '22222', now(), null, 8, 2);

Insert into post_votes (id, time, value, post_id, user_id) values
(1, now(), 1, 1, 1),
(2, now(), -1, 1, 4),
(3, now(), -1, 1, 3),

(4, now(), 1, 2, 5),
(5, now(), 1, 3, 2),
(6, now(), 1, 3, 1),
(7, now(), 1, 8, 4),
(8, now(), 1, 5, 4),
(9, now(), 1, 5, 1);

Insert into tags(id, name) values
(1, 'книга'),
(2, 'ХЭМИНГУЭЙ'),
(3, 'УСПЕНСКИЙ'),
(4, 'МАРКЕС'),
(5, 'О''КОННОР'),
(6, 'КАФКА');

Insert into posts_tags(post_id, tag_id) values
(1, 2),
(2, 3),
(3, 4),
(4, 5),
(5, 6);

insert into global_settings(id, code, name, value) values
(1, 'STATISTICS_IS_PUBLIC', 'Показывать всем статистику блога', 0),
(2, 'POST_PREMODERATION', 'Премодерация постов', 0),
(3, 'MULTIUSER_MODE', 'Многопользовательский режим', 0);
