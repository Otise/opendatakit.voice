 
DELETE TABLE instance;

CREATE TABLE IF NOT EXISTS instance
(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    xml MEDIUMTEXT
);

DELETE TABLE instance_binary;

CREATE TABLE IF NOT EXISTS instance_binary
(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    instanceid INT,
    data MEDIUMBLOB,
    FOREIGN KEY (instanceid) REFERENCES instance(id)
);

DELETE TABLE form;

CREATE TABLE IF NOT EXISTS form
(
    name VARCHAR(100) NOT NULL PRIMARY KEY,
    xml MEDIUMTEXT
 );

DELETE TABLE audio_prompt;

CREATE TABLE IF NOT EXISTS audio_prompt
(
    prompt VARCHAR(10000) PRIMARY KEY,
    data BLOB
);

 

