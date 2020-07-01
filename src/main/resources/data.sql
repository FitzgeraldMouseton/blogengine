Insert into users(id, code, email, is_moderator, name, password, photo, reg_time) values
(1, '0000', 'ernest@gmail.com', 0, 'Эрнест Хемингуэй', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-12-26 00:00:01'),
(2, '0000', 'gleb@gmail.com', 1, 'Глеб Успенский', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-02-15 00:00:01'),
(3, '0000', 'gabriel@gmail.com', 1, 'Габриэль Гарсия Маркес', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-01-26 00:00:01'),
(4, '0000', 'flannery@gmail.com', 0, 'Фланнери О\'Коннор', '$2a$10$U9rK/SGnVm2fRcPvS6Lif.LUGDKF37dIfptIz504wnc29Z4hwS/hi', null, '2019-03-08 00:00:01'),
(5, '0000', 'franz@gmail.com', 0, 'Франц Кафка', '123456', null, '2020-03-01 00:00:01');

Insert into posts(id, is_active, moderation_status, title, time, text, view_count, moderator_id, user_id) values
(1, 1, 'ACCEPTED', "По ком звонит колокол", '2020-04-01 00:00:01', 'Он лежал на устланной сосновыми иглами бурой земле', 5, 3, 1),
(2, 1, 'ACCEPTED', "Нравы Растеряевой улицы", '2020-04-02 00:00:01', 'В городе Т. существует Растеряева улица. Принадлежа к числу захолустий', 10, 3, 2),
(3, 1, 'ACCEPTED', "Сто лет одиночества", '2020-04-03 00:00:01', 'Много лет спустя, перед самым расстрелом, полковник Аурелиано Буэндия', 15, 3, 3),
(4, 1, 'ACCEPTED', "Царство Небесное силою берется", '2020-04-05 00:00:01', 'Не прошло и дня с тех пор, как дед Фрэнсиса Мариона Таруотера помер', 20, 3, 4),
(5, 1, 'ACCEPTED', "Замок", '2020-04-06 00:00:01', 'К. прибыл  поздно вечером', 50, 3, 5),
(6, 0, 'NEW', "Превращение", '2020-04-07 00:00:01', 'Проснувшись однажды утром после беспокойного сна', 0, 3, 5),
(7, 1, 'ACCEPTED', 'Старик и море', '2020-04-08 00:00:01', 'Старик рыбачил один  на своей  лодке в Гольфстриме', 40, 3, 1),
(8, 1, 'ACCEPTED', "Процесс", '2020-04-09 00:00:01', 'Кто-то, по-видимому, оклеветал Йозефа К., потому  что,  не
сделав   ничего  дурного,  он  попал  под  арест.  Кухарка  его
квартирной хозяйки,  фрау  Грубах,  ежедневно  приносившая  ему
завтрак около восьми, на этот раз не явилась. Такого случая еще
не  бывало. К. немного подождал, поглядел с кровати на старуху,
живущую напротив, - она смотрела из окна с  каким-то  необычным
для  нее  любопытством - и потом, чувствуя и голод, и некоторое
недоумение, позвонил. Тотчас же  раздался  стук,  и  в  комнату
вошел  какой-то  человек. К. никогда раньше в этой квартире его
не видел. Он был худощав и вместе с тем крепко сбит,  в  хорошо
пригнанном черном костюме, похожем на дорожное платье - столько
на  нем  было разных вытачек, карманов, пряжек, пуговиц и сзади
хлястик, - от этого костюм казался  особенно  практичным,  хотя
трудно было сразу сказать, для чего все это нужно.
     - Ты кто такой? - спросил К. и приподнялся на кровати.
     Но  тот  ничего не ответил, как будто его появление было в
порядке вещей, и только спросил:
     - Вы звонили?
     - Пусть Анна принесет мне завтрак,  -  сказал  К.  и  стал
молча   разглядывать   этого   человека,  пытаясь  прикинуть  и
сообразить, кто же он, в сущности, такой? Но тот  не  дал  себя
особенно рассматривать и, подойдя к двери, немного приоткрыл ее
и сказал кому-то, очевидно стоявшему тут же, за порогом:
     - Он хочет, чтобы Анна подала ему завтрак.
     Из  соседней  комнаты послышался короткий смешок; по звуку
трудно было угадать, один там человек или их несколько. И  хотя
незнакомец  явно  не  мог  услыхать  ничего для себя нового, он
заявил К. официальным тоном:
     - Это не положено!
     - Вот еще новости! -  сказал  К.,  соскочил  с  кровати  и
торопливо  натянул  брюки.  - Сейчас взгляну, что там за люди в
соседней комнате.  Посмотрим,  как  фрау  Грубах  объяснит  это
вторжение.
     Правда,  он тут же подумал, что не стоило высказывать свои
мысли вслух, - выходило так, будто этими словами он в  какой-то
мере признает за незнакомцем право надзора; впрочем, сейчас это
было  неважно. Но видно, незнакомец так его и понял, потому что
сразу сказал:
     - Может быть, вам лучше остаться тут?
     - И не останусь, и разговаривать с вами не желаю, пока  вы
не скажете, кто вы такой.
     - Зря обижаетесь, - сказал незнакомец и сам открыл дверь.
     В соседней комнате, куда К. прошел медленнее, чем ему того
хотелось, на первый взгляд со вчерашнего вечера почти ничего не
изменилось.  Это  была  гостиная  фрау  Грубах,  загроможденная
мебелью, коврами,  фарфором  и  фотографиями;  пожалуй,  в  ней
сейчас  стало  немного  просторнее,  хотя  это  не  сразу  было
заметно, тем более что главная перемена заключалась в том,  что
там  находился  какой-то человек. Он сидел с книгой у открытого
окна и сейчас, подняв глаза, сказал:
     - Вам следовало остаться у себя в комнате! Разве Франц вам
ничего не говорил?
     - Что вам, наконец, нужно? - спросил К., переводя взгляд с
нового посетителя на того,  кого  назвали  Франц  (он  стоял  в
дверях),  и  снова  на первого.  В  открытое окно видна была та
старуха: в припадке старческого любопытства она уже  перебежала
к другому окну - посмотреть, что дальше.
     - Вот  сейчас я спрошу фрау Грубах, - сказал К. И, хотя он
стоял поодаль от тех двоих, но сделал  движение,  словно  хотел
вырваться у них из рук, и уже пошел было из комнаты.
     - Нет,  -  сказал человек у окна, бросил книжку на столе и
встал: - Вам нельзя уходить. Ведь вы арестованы.
     - Похоже на то, - сказал К. и добавил: - А за что?
     - Мы не  уполномочены  давать  объяснения.  Идите  в  свою
комнату  и  ждите.  Начало вашему делу положено, и в надлежащее
время  вы  все  узнаете.  Я  и  так  нарушаю  свои  полномочия,
разговаривая с вами по-дружески. Но надеюсь, что, кроме Франца,
никто  нас  не  слышит,  а  он  и сам вопреки всем предписаниям
слишком любезен с вами. Если вам  и  дальше  так  повезет,  как
повезло с назначением стражи, то можете быть спокойны.
     К. хотел было сесть, но увидел что в комнате, кроме кресла
у окна, сидеть не на чем.
     - Вы еще поймете - какие это верные слова, сказал Франц, и
вдруг  оба  сразу  подступили  к  нему.  Второй  был много выше
ростом, чем К. Он  все  похлопывал  его  по  плечу.  Они  стали
ощупывать  ночную  рубашку  К.,  приговаривая,  что  теперь ему
придется  надеть  рубаху  куда  хуже,  но  эту  рубашку  и  все
остальное  его  белье  они приберегут, и, если дело обернется в
его пользу, ему все отдадут обратно.
     - Лучше отдайте вещи нам, чем на склад, - говорили они.  -
На  складе  вещи подменяют, а кроме того, через некоторое время
все вещи распродают - все равно, окончилось дело или нет. А  вы
знаете,  как  долго тянутся такие процессы, особенно в нынешнее
время! Конечно, склад  вам  в  конце  концов  вернет  стоимость
вещей,  но, во-первых, сама по себе сумма ничтожная, потому что
при распродаже цену вещи назначают не по  их  стоимости,  а  за
взятки,  да  и  вырученные  деньги  тают,  они  ведь что ни год
переходят из рук в руки.', 35, 3, 5),
(9, 0, 'NEW', "Осень патриарха", '2020-04-10 00:00:01', 'На  исходе  недели  стервятники-грифы  разодрали  металлические оконные сетки', 0, 3, 3),
(10, 1, 'ACCEPTED', "Власть земли", '2020-03-12 00:00:01', 'Морозный зимний день в полном блеске', 25, 3, 2),
(11, 1, 'ACCEPTED', "Любовь во время чумы", '2020-03-11 00:00:01', 'Так было всегда: запах горького миндаля наводил на мысль о несчастной любви', 17, 3, 3),
(12, 1, 'ACCEPTED', "Прощай, оружие", '2020-03-12 00:00:01', 'В  тот год поздним летом  мы стояли  в деревне, в домике, откуда  видны', 25, 3, 1),
(13, 1, 'ACCEPTED', "Мудрая кровь", '2020-03-13 00:00:01', 'Хейзел Моутс сидел на зеленом плюше диванчика купе', 12, 3, 4),
(14, 1, 'ACCEPTED', "Генерал в своем лабиринте", '2020-03-14 00:00:01', 'Хосе Паласиос, самый старый из его слуг', 26, 3, 3),
(15, 1, 'ACCEPTED', "Будка", '2020-03-15 00:00:01', ' На углу двух весьма глухих и бедных переулков уездного города', 31, 3, 2),
(16, 1, 'ACCEPTED', "Земной рай", '2020-03-16 00:00:01', 'В числе знакомых Нади было, между прочим, семейство Печкиных', 7, 3, 2),
(17, 1, 'ACCEPTED', "Снега Килиманджаро", '2020-03-17 00:00:01', 'Килиманджаро - покрытый  вечными снегами горный массив', 14, 3, 1),
(18, 1, 'ACCEPTED', "Исследования одной собаки", '2020-03-18 00:00:01', ' Насколько изменилась моя жизнь и  насколько же, по сути, не изменилась!', 33, 3, 5),
(19, 1, 'ACCEPTED', "Сон", '2020-03-19 00:00:01', 'Стоял  погожий день и К. захотел прогуляться', 41, 3, 5),
(20, 1, 'ACCEPTED', "На вершине все тропы сходятся", '2020-03-20 00:00:01', 'Доктор сказал матери Джулиана, что ей надо похудеть', 25, 3, 4);


Insert into post_comments(id, text, time, comment_id, post_id, user_id) values
(1, '11111', now(), null, 1, 1),
(2, '22222', now(), null, 1, 2),
(3, '33333', now(), null, 1, 3),
(4, '44444', now(), null, 2, 4),
(5, '55555', now(), null, 4, 5),
(6, '66666', now(), null, 4, 2),
(7, '77777', now(), null, 4, 4),
(8, '88888', now(), null, 4, 2),
(9, '99999', now(), null, 4, 1),
(10, '11111', now(), null, 12, 1),
(11, '22222', now(), null, 12, 2),
(12, '33333', now(), null, 13, 3),
(13, '44444', now(), null, 14, 4),
(14, '55555', now(), null, 14, 5),
(15, '66666', now(), null, 14, 2),
(16, '77777', now(), null, 15, 4),
(17, '88888', now(), null, 15, 2),
(18, '99999', now(), null, 16, 1),
(19, '11111', now(), null, 17, 1),
(20, '22222', now(), null, 17, 2),
(21, '33333', now(), null, 17, 3),
(22, '44444', now(), null, 17, 4),
(23, '55555', now(), null, 17, 5),
(24, '66666', now(), null, 17, 2),
(25, '77777', now(), null, 20, 4),
(26, '88888', now(), null, 20, 2),
(27, '99999', now(), null, 20, 1);

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
(14, now(), 1, 8, 4),
(15, now(), 1, 10, 1),
(16, now(), -1, 10, 2),
(17, now(), -1, 11, 3),
(18, now(), 1, 11, 5),
(19, now(), 1, 11, 2),
(20, now(), 1, 11, 1),
(21, now(), 1, 14, 4),
(22, now(), 1, 15, 4),
(23, now(), -1, 16, 1),
(24, now(), 1, 17, 2),
(25, now(), 1, 18, 3),
(26, now(), 1, 19, 2),
(27, now(), 1, 20, 1),
(28, now(), 1, 12, 4);

Insert into tags(id, name) values
(1, 'книга'),
(2, 'хэмингуэй'),
(3, 'успенский'),
(4, 'маркес'),
(5, 'о\'коннор'),
(6, 'кафка');

Insert into posts_tags(posts_id, tags_id) values
(1, 2),
(2, 3),
(3, 4),
(4, 5),
(5, 6),
(6, 6),
(7, 2),
(8, 6),
(9, 4),
(10, 3),
(11, 4),
(12, 2),
(13, 5),
(14, 4),
(15, 3),
(16, 3),
(17, 2),
(18, 6),
(19, 6),
(20, 5);