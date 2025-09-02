CREATE TABLE resource
(
    id                INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY   NOT NULL,
    resource_location VARCHAR(500)                                       NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);