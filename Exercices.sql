CREATE DATABASE IF NOT EXISTS Rallye;
USE Rallye;
CREATE TABLE  tempspartour (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombreDeTours INT NOT NULL,
    temps TIME NOT NULL
);
INSERT INTO tempspartour (nombreDeTours, temps) VALUES
(1, '00:5:05'),
(2, '00:10:05'),
(3, '00:15:30'),
(4, '00:20:15'),
(5, '00:25:45'),
(6, '00:32:10');



SELECT 
    SEC_TO_TIME(AVG(TIME_TO_SEC(diff_temps))) AS moyenne_temps_par_tour
FROM (
    SELECT TIMEDIFF(t1.temps, t2.temps) AS diff_temps
    FROM tempspartour t1
    JOIN tempspartour t2 ON t1.id = t2.id + 1
) AS differences_temps;
