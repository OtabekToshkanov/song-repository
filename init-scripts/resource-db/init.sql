CREATE TABLE resource
(
    id            INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY   NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    resource_data BYTEA                                              NOT NULL
);