-- Create database
CREATE DATABASE IF NOT EXISTS musichub;
USE musichub;

-- =========================
-- MUSIC TRACKS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS music_tracks
(
    songId INT NOT NULL AUTO_INCREMENT,
    songTitle VARCHAR(120) NOT NULL,
    BPM INT NOT NULL,
    durationInSeconds DOUBLE NOT NULL,
    audioFile BLOB,
    PRIMARY KEY (songId)
    );

-- Seed data for music_tracks
INSERT INTO music_tracks (songTitle, BPM, durationInSeconds)
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

-- =========================
-- STUDIOS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS studios
(
    studio_id INT NOT NULL AUTO_INCREMENT,
    location_name VARCHAR(120) NOT NULL,
    room_capacity INT NOT NULL,
    hourly_rate DOUBLE NOT NULL,
    PRIMARY KEY (studio_id)
    );

-- Seed data for studios
INSERT INTO studios (location_name, room_capacity, hourly_rate)
VALUES
    ('Dublin Central Studio', 10, 50.0),
    ('Cork Sound Lab', 8, 40.0),
    ('Galway Beats Studio', 12, 60.0),
    ('Limerick Pro Audio', 6, 35.0),
    ('Waterford Music Hub', 15, 70.0);