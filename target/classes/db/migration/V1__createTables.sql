-- Create table for user entities
CREATE TABLE IF NOT EXISTS user_entities (
    id           UUID DEFAULT random_uuid() PRIMARY KEY,
    username     VARCHAR(50) UNIQUE NOT NULL,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(50) NOT NULL DEFAULT 'COPYWRITER',
    last_name    VARCHAR(50) NOT NULL,
    first_name   VARCHAR(50) NOT NULL,
    email        VARCHAR(50),
    phone        VARCHAR(50),
    telegram     VARCHAR(50) NOT NULL
);

-- Create table for images
CREATE TABLE IF NOT EXISTS images (
    id           UUID DEFAULT random_uuid() PRIMARY KEY,
    image_name   VARCHAR(255) NOT NULL,
    image_type   VARCHAR(255) NOT NULL,
    image_data   BYTEA NOT NULL
);

-- Create table for articles
CREATE TABLE IF NOT EXISTS articles (
    id               UUID DEFAULT random_uuid() PRIMARY KEY,
    title            VARCHAR(300) UNIQUE NOT NULL,
    content          TEXT NOT NULL,
    creation_date    TIMESTAMP NOT NULL,
    category         VARCHAR(50) NOT NULL,
    image_title      VARCHAR(255),
    card_number      VARCHAR(50),
    card_owner       VARCHAR(50),
    mono_link        VARCHAR(400),
    author_id        UUID NOT NULL,
    image_id         UUID,

    FOREIGN KEY (author_id) REFERENCES user_entities(id),
    FOREIGN KEY (image_id) REFERENCES images(id)
);

-- Insert data into the user_entities table with bcrypt-ed passwords
INSERT INTO user_entities (username, password, role, first_name, last_name, telegram)
VALUES ('@superadmin', '$2a$12$fqKO8tGiz2HXe4kGXb0ZH.K5G56JH2M5Ub9maSZk51hIavM2njriu', 'SUPER_ADMIN', 'Oleh', 'Buhaienko', '@olehbuhaienko');

INSERT INTO user_entities (username, password, role, first_name, last_name, telegram)
VALUES ('@copywriter', '$2a$12$kbR5rrQESgnrIUfm5CFnM.x9hX5aXrd..xrKu0F5UWybPRr.lkp.u', 'COPYWRITER', 'Copy', 'Writer', '@copywriter');
