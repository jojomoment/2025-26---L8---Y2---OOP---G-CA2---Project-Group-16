-- mysqlSetup.sql
-- Recreates the schema required for Stage 3 binary BLOB + metadata handling.

-- Create database
CREATE DATABASE IF NOT EXISTS musichub;
USE musichub;

-- MUSIC TRACKS TABLE (Stage 3 binary extension)
CREATE TABLE IF NOT EXISTS music_tracks
(
    songId INT NOT NULL AUTO_INCREMENT,
    songTitle VARCHAR(120) NOT NULL,
    BPM INT NOT NULL,
    durationInSeconds DOUBLE NOT NULL,
    audio_file BLOB,

    -- Metadata (required by F17)
    file_name VARCHAR(255),
    content_type VARCHAR(100),
    file_size INT,

    PRIMARY KEY (songId)
);

-- Ensure columns exist (idempotent)
ALTER TABLE music_tracks ADD COLUMN IF NOT EXISTS audio_file BLOB;
ALTER TABLE music_tracks ADD COLUMN IF NOT EXISTS file_name VARCHAR(255);
ALTER TABLE music_tracks ADD COLUMN IF NOT EXISTS content_type VARCHAR(100);
ALTER TABLE music_tracks ADD COLUMN IF NOT EXISTS file_size INT;

-- STUDIOS TABLE
CREATE TABLE IF NOT EXISTS studios
(
    studio_id INT NOT NULL AUTO_INCREMENT,
    location_name VARCHAR(120) NOT NULL,
    room_capacity INT NOT NULL,
    hourly_rate DOUBLE NOT NULL,
    PRIMARY KEY (studio_id)
);

-- MUSIC PRODUCERS TABLE
CREATE TABLE IF NOT EXISTS music_producers
(
    producer_id INT NOT NULL,
    stage_name VARCHAR(120) NOT NULL,
    tracks_uploaded INT NOT NULL,
    average_rating DOUBLE NOT NULL,
    PRIMARY KEY (producer_id)
);

