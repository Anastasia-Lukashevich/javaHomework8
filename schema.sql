DROP TABLE IF EXISTS ACCOUNTS;
CREATE TABLE IF NOT EXISTS ACCOUNTS (
id INT NOT NULL PRIMARY KEY,
holder VARCHAR(50) NOT NULL,
amount INT NOT NULL
);