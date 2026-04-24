-- Create database
CREATE DATABASE IF NOT EXISTS musichub;
USE musichub;

-- =========================
-- MUSIC TRACKS TABLE
-- =========================
DROP TABLE IF EXISTS music_tracks;
CREATE TABLE IF NOT EXISTS music_tracks
(
    songId INT NOT NULL AUTO_INCREMENT,
    songTitle VARCHAR(120) NOT NULL,
    BPM INT NOT NULL,
    durationInSeconds DOUBLE NOT NULL,
    audioFile BLOB,
    file_name VARCHAR(255),
    content_type VARCHAR(100),
    file_size INT,
    PRIMARY KEY (songId)
    );

-- Seed data for music_tracks
INSERT INTO music_tracks (songTitle, BPM, durationInSeconds, audioFile, file_name, content_type, file_size)
VALUES
    ('Song One', 120, 180.0, NULL, 'song_one.mp3', 'audio/mpeg', 3600000),
    ('Song Two', 128, 200.5, NULL, 'song_two.wav', 'audio/wav', 4200000),
    ('Song Three', 95, 210.0, NULL, 'song_three.mp3', 'audio/mpeg', 2800000),
    ('Song Four', 140, 175.0, NULL, 'song_four.flac', 'audio/flac', 5500000),
    ('Song Five', 110, 190.0, NULL, 'song_five.mp3', 'audio/mpeg', 3100000),
    ('Song Six', 105, 220.5, NULL, 'song_six.wav', 'audio/wav', 4600000),
    ('Song Seven', 130, 205.0, NULL, 'song_seven.mp3', 'audio/mpeg', 3900000),
    ('Song Eight', 100, 180.0, NULL, 'song_eight.mp3', 'audio/mpeg', 3300000),
    ('Song Nine', 115, 195.0, NULL, 'song_nine.wav', 'audio/wav', 4100000),
    ('Song Ten', 125, 215.0, NULL, 'song_ten.mp3', 'audio/mpeg', 3700000);

-- =========================
-- STUDIOS TABLE
-- =========================
DROP TABLE IF EXISTS studios;
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

-- =========================
-- MUSIC PRODUCERS TABLE
-- =========================
DROP TABLE IF EXISTS music_producers;
CREATE TABLE IF NOT EXISTS music_producers
(
    producer_id INT NOT NULL,
    stage_name VARCHAR(120) NOT NULL,
    tracks_uploaded INT NOT NULL,
    average_rating DOUBLE NOT NULL,
    PRIMARY KEY (producer_id)
    );

INSERT INTO music_producers (producer_id, stage_name, tracks_uploaded, average_rating)
VALUES
    (1, 'DJ Nova', 25, 4.5),
    (2, 'BeatMasterX', 40, 4.8),
    (3, 'EchoWave', 15, 4.2),
    (4, 'SynthLord', 60, 4.9),
    (5, 'BassHunter', 30, 4.3),
    (6, 'RhythmRider', 22, 4.1),
    (7, 'SoundCrafter', 18, 4.6),
    (8, 'PulseMaker', 50, 4.7),
    (9, 'VibeSmith', 27, 4.4),
    (10, 'TrackWizard', 35, 4.85);

