Insert into USERS (ID, CODE, EMAIL, IS_MODERATOR, NAME, PASSWORD, PHOTO, REG_TIME) values
(1, null , 'ernest@gmail.com', 0, 'Эрнест Хемингуэй', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-12-26 00:00:01'),
(2, null , 'gleb@gmail.com', 0, 'Глеб Успенский', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-02-15 00:00:01'),
(3, null , 'gabriel@gmail.com', 1, 'Габриэль Гарсия Маркес', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-01-26 00:00:01'),
(4, null , 'flannery@gmail.com', 0, 'Фланнери О''Коннор', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-03-08 00:00:01'),
(5, null , 'franz@gmail.com', 0, 'Франц Кафка', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2020-03-01 00:00:01');

Insert into posts(id, is_active, moderation_status, title, time, text, view_count, moderator_id, user_id) values
(1, 1, 'NEW',      '', '2020-01-01 00:00:01', '', 15, 3, 1),
(2, 1, 'DECLINE',  '', '2020-01-02 00:06:01', '', 10, 3, 1),
(3, 0, 'ACCEPTED', '', '2020-01-03 10:56:01', '', 15, 3, 1),
(4, 1, 'ACCEPTED', '', '2020-01-04 00:00:01', '', 20, 3, 1),
(5, 1, 'ACCEPTED', '', '2020-01-05 00:00:01', '', 50, 3, 1),

(6, 1, 'ACCEPTED', '', '2020-01-06 00:00:01', '', 20, 3, 2),
(7, 1, 'ACCEPTED', '', '2020-01-07 00:00:01', '', 15, 3, 3),
(8, 1, 'ACCEPTED', '', '2020-01-08 00:00:01', '', 25, 3, 4),
(8, 1, 'ACCEPTED', '', '2020-01-08 00:00:01', '', 25, 3, 5),
(8, 1, 'ACCEPTED', '', '2020-01-08 00:00:01', '', 25, 3, 5);

Insert into post_comments (id, text, time, comment_id, post_id, user_id) values
(1, '11111', now(), null, 1, 1);

Insert into post_votes (id, time, value, post_id, user_id) values
(1, now(), -1, 4, 1),
(2, now(), -1, 4, 4),
(3, now(), 1, 4, 3),
(4, now(), 1, 5, 5),
(5, now(), 1, 5, 2),
(6, now(), 1, 3, 1),
(6, now(), 1, 2, 1),
(6, now(), 1, 1, 1),

(7, now(), 1, 5, 4),
(8, now(), 1, 5, 4),
(9, now(), -1, 8, 1);

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
