 
DELETE TABLE instances;

CREATE TABLE IF NOT EXISTS instances
(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    xml MEDIUMTEXT
);


DELETE TABLE sessions;

CREATE TABLE IF NOT EXISTS sessions
(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    instance INT,
    FOREIGN KEY (instance) REFERENCES instances(id)

 );

DELETE TABLE audio_prompts;

CREATE TABLE IF NOT EXISTS audio_prompts
(
    prompt VARCHAR(10000) PRIMARY KEY,
    data BLOB
);

 

