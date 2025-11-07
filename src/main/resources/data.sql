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

INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(1, false, 4, 'player1', 'hola@gmail.com');
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(2, false, 5, 'player2', 'adios@gmail.com');
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(3, false, 6, 'player3', 'player3@gmail.com');
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(4, false, 7, 'player4', 'player4@gmail.com');
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(5, false, 8, 'player5', 'player5@gmail.com');
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(6, false, 9, 'player6', 'player6@gmail.com');
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(7, false, 10, 'player7', 'player7@gmail.com');
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(8, false, 11, 'player8', 'player8@gmail.com');
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(9, false, 12, 'player9', 'player9@gmail.com');
INSERT INTO players(id, is_currently_in_match, user_id, nickname, email) VALUES(10, false, 13, 'player10', 'player10@gmail.com');

INSERT INTO statistics(id, name, valor) VALUES (1, 'games_played', 10);
INSERT INTO statistics(id, name, valor) VALUES (2, 'games_won', 7);
INSERT INTO statistics(id, name, valor) VALUES (3, 'highest_score', 150);
INSERT INTO statistics(id, name, valor) VALUES (4, 'total_score', 1200);
INSERT INTO statistics(id, name, valor) VALUES (5, 'power_ups_used', 50);
INSERT INTO statistics(id, name, valor) VALUES (6, 'lines_cleared', 1000);

INSERT INTO achievements(id, name, description, valor, statistic_name, image) VALUES (1, 'First Win', 'Win your first game', 1, 'games_won', 'imagelin.png');
INSERT INTO achievements(id, name, description, valor, statistic_name, image) VALUES (2, 'High Scorer', 'Reach a total score of 1000', 1000, 'total_score', 'highscore.png');
INSERT INTO achievements(id, name, description, valor, statistic_name, image) VALUES (3, 'Line Master', 'Clear 500 lines', 500, 'lines_cleared', 'linemaster.png');
INSERT INTO achievements(id, name, description, valor, statistic_name, image) VALUES (4, 'Power Up!', 'Use 25 power-ups', 25, 'power_ups_used', 'powerup.png');

INSERT INTO matches(id, code, created_at, started_at, ended_at, creator_id, player1_id, player2_id, turn, turn_type, player1score, player2score, winner)
    VALUES (1, 'TRJU', '2025-10-25 14:41:00', '2025-10-25 14:42:00', '2025-10-25 14:52:00', 1, 1, 2, 3, 1, 9, 5, 1),
           (2, 'FNSW', '2025-10-25 14:50:00', '2025-10-25 14:50:10', '2025-10-25 15:00:10', 2, 2, 1, 0, 0, 7, 9, 1),
           (3, 'ABCD', '2025-10-26 10:00:00', '2025-10-26 10:01:00', '2025-10-26 10:11:00', 3, 3, 4, 5, 0, 8, 0, 1),
           (4, 'EFGH', '2025-10-26 11:00:00', '2025-10-26 11:01:00', '2025-10-26 11:11:00', 4, 4, 5, 2, 1, 5, 9, 2),
           (5, 'IJKL', '2025-10-26 12:00:00', '2025-10-26 12:01:00', '2025-10-26 12:11:00', 5, 5, 6, 8, 0, 9, 9, 1),
           (6, 'MNOP', '2025-10-26 13:00:00', '2025-10-26 13:01:00', '2025-10-26 13:11:00', 6, 6, 7, 1, 1, 8, 9, 2),
           (7, 'QRST', '2025-10-26 14:00:00', '2025-10-26 14:01:00', '2025-10-26 14:11:00', 7, 7, 8, 4, 0, 9, 1, 1),
           (8, 'UVWX', '2025-10-26 15:00:00', null, null, 8, null, null, 0, 0, 0, 0, null),
           (9, 'YZAB', '2025-10-26 16:00:00', null, null, 9, null, null, 0, 0, 0, 0, null),
           (10, 'CDEF', '2025-10-26 17:00:00', '2025-10-26 17:01:00', null, 10, 10, 1, 7, 1, 3, 4, null);
