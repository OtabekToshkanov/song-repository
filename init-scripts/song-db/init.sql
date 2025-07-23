CREATE TABLE song
(
    id       INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,
    name     VARCHAR(100)                                     NOT NULL,
    artist   VARCHAR(100)                                     NOT NULL,
    album    VARCHAR(100)                                     NOT NULL,
    duration VARCHAR(5)                                       NOT NULL,
    year     VARCHAR(4)                                       NOT NULL
);