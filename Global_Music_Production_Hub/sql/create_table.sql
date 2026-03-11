-- Create database
CREATE DATABASE IF NOT EXISTS musichub;
USE taskhub;

-- Create table
CREATE TABLE IF NOT EXISTS music_tracks

(
     song_id INT NOT NULL AUTO_INCREMENT,
     song_title VARCHAR(120) NOT NULL,
     BPM INT NOT NULL,
     duration_in_seconds DOUBLE NOT NULL,
     audio_file BLOB,
     PRIMARY KEY (song_id)

    );

-- Insert at least 10 seed rows
INSERT INTO music_tracks (song_title, BPM, duration_in_seconds)
VALUES

    ('Song One', 120, 180.0),
    ('Song Two', 128, 200.5),
    ('Song Three', 95, 210.0),
    ('Song Four', 140, 175.0),
    ('Song Five', 110, 190.0),
    ('Song Six', 105, 220.5),
    ('Song Seven', 130, 205.0),
    ('Song Eight', 100, 180.0),
    ('Song Nine', 115, 195.0),
    ('Song Ten', 125, 215.0);