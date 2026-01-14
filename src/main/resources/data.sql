-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO appusers(id,username,password,authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);

-- Ten player users, named player1 with passwor 0wn3r
INSERT INTO authorities(id,authority) VALUES (2,'PLAYER');
INSERT INTO appusers(id,username,password,authority) VALUES (4,'player1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (5,'player2','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (6,'player3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (7,'player4','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (8,'player5','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (9,'player6','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (10,'player7','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (11,'player8','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (12,'player9','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (13,'player10','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (14,'FBN5868','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (15,'KDR0901','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (16,'BRD3895','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (17,'RXW1248','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (18,'WHS7046','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (19,'WTS5677','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);

INSERT INTO statistics(id, games_played, games_won, time_played, sarcinas_created, bacterias_created) 
VALUES (1, 24, 16, 14400, 20, 230), 
(2, 22, 12, 13200, 16, 211), 
(3, 20, 9, 12000, 14, 186),
(4, 18, 8, 10800, 12, 170),
(5, 14, 7, 8400, 9, 130),
(6, 12, 6, 7200, 8, 110),
(7, 10, 5, 6000, 6, 95),
(8, 8, 4, 4800, 5, 75),
(9, 6, 0, 3600, 2, 47),
(10, 2, 0, 1200, 1, 14),
(11, 8, 3, 147, 2, 28);

INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(1, false, 4, 'player1', 'hola@gmail.com', 1);
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(2, false, 5, 'player2', 'adios@gmail.com', 2);
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(3, false, 6, 'player3', 'player3@gmail.com', 3);
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(4, false, 7, 'player4', 'player4@gmail.com', 4);
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(5, false, 8, 'player5', 'player5@gmail.com', 5);
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(6, false, 9, 'player6', 'player6@gmail.com', 6);
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(7, false, 10, 'player7', 'player7@gmail.com', 7);
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(8, false, 11, 'player8', 'player8@gmail.com', 8);
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(9, false, 12, 'player9', 'player9@gmail.com', 9);
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email, statistics_id) VALUES(10,false ,13 , 'player10' , 'player10@gmail.com' , 10);



INSERT INTO achievements(id, name, description, valor, statistic_name, image) VALUES (1, 'Primera Victoria', 'Gana tu primera partida', 1, 'games_won', 'imagelin.png'),
                                                                                    (2, 'Creador de sarcinas', 'Crea 5 sarcinas', 5, 'sarcinas_created', 'imagelin.png'),
                                                                                    (3, 'Gamer', 'Juega 10 partidas', 10, 'games_played', 'imagelin.png'),
                                                                                    (4, 'Adicto', 'Juega por 5 horas', 300, 'time_played', 'imagelin.png'),
                                                                                    (5, 'Maestro de bacterias', 'Crea 20 bacterias', 20, 'bacterias_created', 'imagelin.png'),
                                                                                    (6, 'En racha', 'Gana 5 partidas seguidas', 5, 'games_won', 'imagelin.png'),
                                                                                    (7, 'Experto en sarcinas', 'Crea 15 sarcinas', 15, 'sarcinas_created', 'imagelin.png'),
                                                                                    (8, 'Dedicacion científica', 'Juega 50 partidas', 50, 'games_played', 'imagelin.png'),
                                                                                    (9, 'Pegado a la silla', 'Juega por 20 horas', 1200, 'time_played', 'imagelin.png'),
                                                                                    (10, 'Dominador bacteria', 'Crea 50 bacterias', 50, 'bacterias_created', 'imagelin.png');
                                                                                    
INSERT INTO players_achievements(player_id, achievements_id)
VALUES (1, 1), (1, 2), (1, 3), (1,4), (1,5), (1,6), (1,7), (1,8), (1,9), (1,10),
       (2, 1), (2, 2), (2, 3), (2,4), (2,5), (2,6), (2,7), (2,8), (2,9), (2,10),
       (3, 1), (3, 2), (3, 3), (3,4), (3,5), (3,6), (3,7), (3,8), (3,9), (3,10),
       (4, 1), (4, 2), (4, 3), (4,4), (4,5), (4,6), (4,7), (4,8), (4,9), (4,10),
       (5, 1), (5, 2), (5, 3), (5,4), (5,5), (5,6), (5,7), (5,8), (5,9), (5,10),
       (6, 1), (6, 2), (6, 3), (6,4), (6,5), (6,6), (6,7), (6,8), (6,9), (6,10),
       (7, 1), (7, 2), (7, 3), (7,4), (7,5), (7,6), (7,7), (7,8), (7,9), (7,10),
       (8, 1), (8, 2), (8, 3), (8,4), (8,5), (8,6), (8,7), (8,8), (8,9), (8,10),
       (9, 1), (9, 2), (9, 3), (9,4), (9,5), (9,6), (9,7), (9,8), (9,9), (9,10),
       (10,1), (10,2), (10,3), (10,4), (10,5), (10,6), (10,7), (10,8), (10,9), (10,10);


INSERT INTO matches(id, code, created_at, started_at, ended_at, creator_id, player1_id, player2_id, turn, turn_type, player1score, player2score, winner)
    VALUES (1, 'TRJU', '2025-10-25 14:41:00', '2025-10-25 14:42:00', '2025-10-25 14:52:00', 1, 1, 2, 30, 1, 9, 5, 2),
           (2, 'FNSW', '2025-10-25 14:50:00', '2025-10-25 14:50:10', '2025-10-25 15:00:10', 2, 2, 1, 34, 0, 7, 9, 1),
           (3, 'ABCD', '2025-10-26 10:00:00', '2025-10-26 10:01:00', '2025-10-26 10:11:00', 3, 3, 4, 35, 0, 8, 0, 2),
           (4, 'EFGH', '2025-10-26 11:00:00', '2025-10-26 11:01:00', '2025-10-26 11:11:00', 4, 4, 5, 29, 1, 5, 9, 1),
           (5, 'IJKL', '2025-10-26 12:00:00', '2025-10-26 12:01:00', '2025-10-26 12:11:00', 5, 5, 6, 28, 0, 9, 9, 1),
           (6, 'MNOP', '2025-10-26 13:00:00', '2025-10-26 13:01:00', '2025-10-26 13:11:00', 6, 6, 7, 31, 1, 8, 9, 1),
           (7, 'QRST', '2025-10-26 14:00:00', '2025-10-26 14:01:00', '2025-10-26 14:11:00', 7, 7, 8, 40, 0, 9, 1, 2),
           (8, 'UVWX', '2025-10-26 15:00:00', null, null, 8, null, null, 0, 0, 0, 0, null),
           (9, 'YZAB', '2025-10-26 16:00:00', null, null, 9, null, null, 0, 0, 0, 0, null),
           (10, 'CDEF', '2025-10-26 17:00:00', '2025-10-26 17:01:00', null, 10, 10, 3, 7, 1, 3, 4, null),
           (11, 'QWER', '2025-10-27 10:00:00', '2025-10-27 10:01:00', '2025-10-27 10:11:00', 1, 1, 2, 23, 0, 5, 7, 1),
           (12, 'ASDF', '2025-10-27 10:02:00', '2025-10-27 10:03:00', '2025-10-27 10:13:00', 1, 3, 4, 13, 0, 1, 5, 1),
           (13, 'ZXCV', '2025-10-27 10:04:00', '2025-10-27 10:05:00', '2025-10-27 10:15:00', 1, 5, 6, 19, 0, 4, 6, 1),
           (14, 'POIU', '2025-10-27 10:06:00', '2025-10-27 10:07:00', '2025-10-27 10:17:00', 1, 7, 8, 26, 0, 4, 5, 1),
           (15, 'LKJH', '2025-10-27 10:08:00', '2025-10-27 10:09:00', '2025-10-27 10:19:00', 1, 9, 10, 12, 0, 0, 4, 1),
           (16, 'MNBV', '2025-10-27 10:10:00', '2025-10-27 10:11:00', '2025-10-27 10:21:00', 1, 1, 3, 23, 0, 5, 6, 1),
           (17, 'TYUI', '2025-10-27 10:12:00', '2025-10-27 10:13:00', '2025-10-27 10:23:00', 1, 2, 4, 23, 0, 8, 9, 2),
           (18, 'GHJK', '2025-10-27 10:14:00', '2025-10-27 10:15:00', '2025-10-27 10:25:00', 1, 5, 2, 28, 0, 6, 9, 2),
           (19, 'BNNM', '2025-10-27 10:16:00', '2025-10-27 10:17:00', '2025-10-27 10:27:00', 1, 3, 6, 29, 0, 5, 8, 2),
           (20, 'RFVT', '2025-10-27 10:18:00', '2025-10-27 10:19:00', '2025-10-27 10:29:00', 1, 4, 7, 36, 0, 2, 3, 2),
           (21, 'EDCX', '2025-10-27 10:20:00', '2025-10-27 10:21:00', '2025-10-27 10:31:00', 1, 8, 9, 20, 0, 4, 7, 2),
           (22, 'WSAQ', '2025-10-27 10:22:00', '2025-10-27 10:23:00', '2025-10-27 10:33:00', 1, 1, 5, 21, 0, 5, 6, 1),
           (23, 'PLKM', '2025-10-27 10:24:00', '2025-10-27 10:25:00', '2025-10-27 10:35:00', 1, 2, 6, 21, 0, 5, 9, 1),
           (24, 'OIUJ', '2025-10-27 10:26:00', '2025-10-27 10:27:00', '2025-10-27 10:37:00', 1, 3, 7, 21, 0, 6, 3, 2),
           (25, 'NHYT', '2025-10-27 10:28:00', '2025-10-27 10:29:00', '2025-10-27 10:39:00', 1, 4, 8, 34, 0, 5, 1, 2),
           (26, 'BGVF', '2025-10-27 10:30:00', '2025-10-27 10:31:00', '2025-10-27 10:41:00', 1, 5, 9, 23, 0, 4, 2, 2),
           (27, 'CDEX', '2025-10-27 10:32:00', '2025-10-27 10:33:00', '2025-10-27 10:43:00', 1, 6, 10, 23, 0, 3, 1, 2),
           (28, 'VFRB', '2025-10-27 10:34:00', '2025-10-27 10:35:00', '2025-10-27 10:45:00', 1, 7, 1, 13, 0, 2, 0, 2),
           (29, 'TGBY', '2025-10-27 10:36:00', '2025-10-27 10:37:00', '2025-10-27 10:47:00', 1, 8, 2, 6, 0, 1, 0, 2),
           (30, 'YHNJ', '2025-10-27 10:38:00', '2025-10-27 10:39:00', '2025-10-27 10:49:00', 1, 9, 3, 15, 0, 3, 0, 2),
           (31, 'UJMZ', '2025-10-27 10:40:00', '2025-10-27 10:41:00', '2025-10-27 10:51:00', 1, 1, 4, 23, 0, 4, 6, 1),
           (32, 'IKOL', '2025-10-27 10:42:00', '2025-10-27 10:43:00', '2025-10-27 10:53:00', 1, 2, 5, 23, 0, 4, 7, 1),
           (33, 'PLMN', '2025-10-27 10:44:00', '2025-10-27 10:45:00', '2025-10-27 10:55:00', 1, 3, 6, 25, 0, 3, 6, 1),
           (34, 'QAZX', '2025-10-27 10:46:00', '2025-10-27 10:47:00', '2025-10-27 10:57:00', 1, 4, 7, 26, 0, 1, 5, 1),
           (35, 'WSXC', '2025-10-27 10:48:00', '2025-10-27 10:49:00', '2025-10-27 10:59:00', 1, 5, 8, 27, 0, 6, 1, 2),
           (36, 'EDCV', '2025-10-27 10:50:00', '2025-10-27 10:51:00', '2025-10-27 11:01:00', 1, 6, 9, 29, 0, 5, 1, 2),
           (37, 'RFVB', '2025-10-27 10:52:00', '2025-10-27 10:53:00', '2025-10-27 11:03:00', 1, 7, 10, 17, 0, 4, 2, 2),
           (38, 'TGHB', '2025-10-27 10:54:00', '2025-10-27 10:55:00', '2025-10-27 11:05:00', 1, 1, 8, 36, 0, 3, 6, 1),
           (39, 'YJMK', '2025-10-27 10:56:00', '2025-10-27 10:57:00', '2025-10-27 11:07:00', 1, 2, 9, 37, 0, 8, 0, 2),
           (40, 'UIOP', '2025-10-27 10:58:00', '2025-10-27 10:59:00', '2025-10-27 11:09:00', 1, 3, 10, 33, 0, 7, 1, 2),
           (41, 'ASZX', '2025-10-27 11:00:00', '2025-10-27 11:01:00', '2025-10-27 11:11:00', 1, 4, 1, 23, 0, 5, 1, 2),
           (42, 'QWER', '2025-10-27 11:02:00', '2025-10-27 11:03:00', '2025-10-27 11:13:00', 1, 5, 2, 23, 0, 4, 0, 2),
           (43, 'ZXCQ', '2025-10-27 11:04:00', '2025-10-27 11:05:00', '2025-10-27 11:15:00', 1, 6, 3, 23, 0, 3, 2, 2),
           (44, 'PLAS', '2025-10-27 11:06:00', '2025-10-27 11:07:00', '2025-10-27 11:17:00', 1, 7, 4, 29, 0, 2, 1, 2),
           (45, 'QWAS', '2025-10-27 11:08:00', '2025-10-27 11:09:00', '2025-10-27 11:19:00', 1, 8, 5, 29, 0, 1, 0, 2),
           (46, 'EDAS', '2025-10-27 11:10:00', '2025-10-27 11:11:00', '2025-10-27 11:21:00', 1, 9, 6, 15, 0, 6, 1, 2),
           (47, 'RFAS', '2025-10-27 11:12:00', '2025-10-27 11:13:00', '2025-10-27 11:23:00', 1, 10, 1, 23, 0, 6, 4, 2),
           (48, 'TGAS', '2025-10-27 11:14:00', '2025-10-27 11:15:00', '2025-10-27 11:25:00', 1, 2, 3, 23, 0, 8, 1, 2),
           (49, 'YHAS', '2025-10-27 11:16:00', '2025-10-27 11:17:00', '2025-10-27 11:27:00', 1, 4, 5, 22, 0, 9, 8, 2),
           (50, 'UIAS', '2025-10-27 11:18:00', '2025-10-27 11:19:00', '2025-10-27 11:29:00', 1, 6, 7, 24, 0, 4, 1, 2),
           (51, 'QAZW', '2025-10-28 10:00:00', '2025-10-28 10:01:00', '2025-10-28 10:11:00', 1, 8, 9, 25, 0, 4, 1, 2),
           (52, 'WSXC', '2025-10-28 10:02:00', '2025-10-28 10:03:00', '2025-10-28 10:13:00', 1, 10, 1, 23, 0, 6, 1, 2),
           (53, 'EDCV', '2025-10-28 10:04:00', '2025-10-28 10:05:00', '2025-10-28 10:15:00', 1, 2, 4, 23, 0, 7, 0, 2),
           (54, 'RFVB', '2025-10-28 10:06:00', '2025-10-28 10:07:00', '2025-10-28 10:17:00', 1, 3, 5, 28, 0, 4, 0, 2),
           (55, 'TGHB', '2025-10-28 10:08:00', '2025-10-28 10:09:00', '2025-10-28 10:19:00', 1, 6, 8, 28, 0, 4, 1, 2),
           (56, 'YJMK', '2025-10-28 10:10:00', '2025-10-28 10:11:00', '2025-10-28 10:21:00', 1, 7, 9, 28, 0, 4, 1, 2),
           (57, 'UIOP', '2025-10-28 10:12:00', '2025-10-28 10:13:00', '2025-10-28 10:23:00', 1, 1, 5, 29, 0, 2, 6, 1),
           (58, 'ASDF', '2025-10-28 10:14:00', '2025-10-28 10:15:00', '2025-10-28 10:25:00', 1, 2, 6, 31, 0, 3, 7, 1),
           (59, 'QWER', '2025-10-28 10:16:00', '2025-10-28 10:17:00', '2025-10-28 10:27:00', 1, 3, 7, 32, 0, 3, 6, 1),
           (60, 'ZXCV', '2025-10-28 10:18:00', '2025-10-28 10:19:00', '2025-10-28 10:29:00', 1, 4, 8, 32, 0, 3, 5, 1),
           (61, 'POIU', '2025-10-28 10:20:00', '2025-10-28 10:21:00', '2025-10-28 10:31:00', 1, 9, 10, 23, 0, 2, 4, 1),
           (62, 'LKJH', '2025-10-28 10:22:00', '2025-10-28 10:23:00', '2025-10-28 10:33:00', 1, 1, 2, 23, 0, 1, 5, 1),
           (63, 'MNBV', '2025-10-28 10:24:00', '2025-10-28 10:25:00', '2025-10-28 10:35:00', 1, 3, 4, 23, 0, 3, 6, 1),
           (64, 'TYUI', '2025-10-28 10:26:00', '2025-10-28 10:27:00', '2025-10-28 10:37:00', 1, 5, 6, 23, 0, 2, 7, 1),
           (65, 'GHJK', '2025-10-28 10:28:00', '2025-10-28 10:29:00', '2025-10-28 10:39:00', 1, 7, 8, 23, 0, 5, 6, 1),
           (66, 'BNNM', '2025-10-28 10:30:00', '2025-10-28 10:31:00', '2025-10-28 10:41:00', 1, 9, 1, 25, 0, 4, 1, 2),
           (67, 'RFVT', '2025-10-28 10:32:00', '2025-10-28 10:33:00', '2025-10-28 10:43:00', 1, 2, 3, 24, 0, 3, 1, 2),
           (68, 'EDCX', '2025-10-28 10:34:00', '2025-10-28 10:35:00', '2025-10-28 10:45:00', 1, 4, 5, 25, 0, 2, 0, 2),
           (69, 'WSAQ', '2025-10-28 10:36:00', '2025-10-28 10:37:00', '2025-10-28 10:47:00', 1, 6, 7, 25, 0, 1, 0, 2),
           (70, 'PLKM', '2025-10-28 10:38:00', '2025-10-28 10:39:00', '2025-10-28 10:49:00', 1, 8, 9, 23, 0, 5, 0, 2);

INSERT INTO friend(id, receiver_id, requester_id, status) VALUES (1, 1, 2, 1);
INSERT INTO friend(id,  receiver_id,  requester_id, status) VALUES (2, 1, 3, 0);
