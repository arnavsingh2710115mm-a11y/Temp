-- =====================================================================
--  PetFeet - Find a Paw, Give a Home  |  MySQL / MariaDB schema + demo data
--  Run:  mysql -u root -p < database.sql
--  DEMO PASSWORD for every sample account below:  Demo@123
-- =====================================================================
DROP DATABASE IF EXISTS petfeet_db;
CREATE DATABASE petfeet_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE petfeet_db;

-- ---------- users ----------
CREATE TABLE users (
  id                 INT AUTO_INCREMENT PRIMARY KEY,
  name               VARCHAR(100) NOT NULL,
  email              VARCHAR(150) NOT NULL UNIQUE,
  password           VARCHAR(255) NOT NULL,              -- PBKDF2 hash (iterations:salt:hash), never plain text
  role               ENUM('ADMIN','SHELTER','ADOPTER') NOT NULL,
  phone              VARCHAR(20),
  address            VARCHAR(255),
  city               VARCHAR(80),
  preferred_species  VARCHAR(40),                        -- used by the recommendation engine
  preferred_max_age  INT,
  is_active          BOOLEAN NOT NULL DEFAULT TRUE,
  created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------- pets ----------
CREATE TABLE pets (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  shelter_id   INT NOT NULL,
  name         VARCHAR(80) NOT NULL,
  species      VARCHAR(40) NOT NULL,
  breed        VARCHAR(80) NOT NULL,
  age          INT NOT NULL DEFAULT 0,                   -- in years (0 = under 1 year)
  gender       ENUM('Male','Female') NOT NULL,
  location     VARCHAR(100) NOT NULL,
  description  TEXT,
  image_url    VARCHAR(255),
  status       ENUM('PENDING','AVAILABLE','ADOPTED','REJECTED') NOT NULL DEFAULT 'PENDING',
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pet_shelter FOREIGN KEY (shelter_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_pet_status (status),
  INDEX idx_pet_species (species)
) ENGINE=InnoDB;

-- ---------- adoption_applications ----------
CREATE TABLE adoption_applications (
  id                INT AUTO_INCREMENT PRIMARY KEY,
  pet_id            INT NOT NULL,
  adopter_id        INT NOT NULL,
  application_date  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  message           TEXT,
  status            ENUM('PENDING','APPROVED','REJECTED','COMPLETED') NOT NULL DEFAULT 'PENDING',
  applicant_name    VARCHAR(100) NOT NULL,
  phone             VARCHAR(20)  NOT NULL,
  email             VARCHAR(150) NOT NULL,
  address           VARCHAR(255) NOT NULL,
  reason            TEXT NOT NULL,
  experience        VARCHAR(255),
  home_type         VARCHAR(40),
  has_other_pets    BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_app_pet     FOREIGN KEY (pet_id)     REFERENCES pets(id)  ON DELETE CASCADE,
  CONSTRAINT fk_app_adopter FOREIGN KEY (adopter_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_app_status (status)
) ENGINE=InnoDB;

-- ---------- messages ----------
CREATE TABLE messages (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  sender_id    INT NOT NULL,
  receiver_id  INT NOT NULL,
  message      TEXT NOT NULL,
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  is_read      BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_msg_sender   FOREIGN KEY (sender_id)   REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_msg_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- notifications ----------
CREATE TABLE notifications (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  user_id     INT NOT NULL,
  message     VARCHAR(500) NOT NULL,
  is_read     BOOLEAN NOT NULL DEFAULT FALSE,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- system_settings ----------
CREATE TABLE system_settings (
  setting_key    VARCHAR(60) PRIMARY KEY,
  setting_value  VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- ---------- adoption_history (written inside the approval transaction) ----------
CREATE TABLE adoption_history (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  application_id  INT NOT NULL,
  pet_id          INT NOT NULL,
  adopter_id      INT NOT NULL,
  shelter_id      INT NOT NULL,
  adopted_on      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_hist_app     FOREIGN KEY (application_id) REFERENCES adoption_applications(id) ON DELETE CASCADE,
  CONSTRAINT fk_hist_pet     FOREIGN KEY (pet_id)         REFERENCES pets(id)  ON DELETE CASCADE,
  CONSTRAINT fk_hist_adopter FOREIGN KEY (adopter_id)     REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_hist_shelter FOREIGN KEY (shelter_id)     REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- favorites (extra feature) ----------
CREATE TABLE favorites (
  user_id     INT NOT NULL,
  pet_id      INT NOT NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, pet_id),
  CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_fav_pet  FOREIGN KEY (pet_id)  REFERENCES pets(id)  ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- activity_logs (admin "Activity Logs" section) ----------
CREATE TABLE activity_logs (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  user_id     INT NULL,
  action      VARCHAR(255) NOT NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================================
--  DEMO DATA   (all passwords: Demo@123)
-- =====================================================================
INSERT INTO users (id, name, email, password, role, phone, address, city, preferred_species, preferred_max_age) VALUES
 (1, 'PetFeet Admin',        'admin@petfeet.com',    '65536:UGV0RmVldFNhbHQyMDI2:RsroOYYCy+Qwyt+1Sz8or5R/z3IZuKYx2xqkVBL8BUk=', 'ADMIN',   '9000000001', 'PetFeet HQ, Knowledge Park III', 'Greater Noida', NULL, NULL),
 (2, 'Happy Tails Shelter',  'shelter@petfeet.com',  '65536:UGV0RmVldFNhbHQyMDI2:RsroOYYCy+Qwyt+1Sz8or5R/z3IZuKYx2xqkVBL8BUk=', 'SHELTER', '9000000002', 'Sector 62',        'Greater Noida', NULL, NULL),
 (3, 'Paws & Whiskers Rescue','paws@petfeet.com',    '65536:UGV0RmVldFNhbHQyMDI2:RsroOYYCy+Qwyt+1Sz8or5R/z3IZuKYx2xqkVBL8BUk=', 'SHELTER', '9000000003', 'Lajpat Nagar',     'Delhi',         NULL, NULL),
 (4, 'Noida Animal Care',    'care@petfeet.com',     '65536:UGV0RmVldFNhbHQyMDI2:RsroOYYCy+Qwyt+1Sz8or5R/z3IZuKYx2xqkVBL8BUk=', 'SHELTER', '9000000004', 'Sector 18',        'Noida',         NULL, NULL),
 (5, 'Aarav Sharma',         'adopter@petfeet.com',  '65536:UGV0RmVldFNhbHQyMDI2:RsroOYYCy+Qwyt+1Sz8or5R/z3IZuKYx2xqkVBL8BUk=', 'ADOPTER', '9000000005', 'B-12, Alpha 1',    'Greater Noida', 'Dog', 5),
 (6, 'Meera Iyer',           'meera@petfeet.com',    '65536:UGV0RmVldFNhbHQyMDI2:RsroOYYCy+Qwyt+1Sz8or5R/z3IZuKYx2xqkVBL8BUk=', 'ADOPTER', '9000000006', 'Sector 50',        'Noida',         'Cat', 3),
 (7, 'Kabir Singh',          'kabir@petfeet.com',    '65536:UGV0RmVldFNhbHQyMDI2:RsroOYYCy+Qwyt+1Sz8or5R/z3IZuKYx2xqkVBL8BUk=', 'ADOPTER', '9000000007', 'Indirapuram',      'Ghaziabad',     'Dog', 4);

INSERT INTO pets (id, shelter_id, name, species, breed, age, gender, location, description, image_url, status, created_at) VALUES
 (1, 2, 'Bruno',  'Dog', 'Golden Retriever', 2, 'Male',   'Greater Noida', 'Bruno is a gentle, sunny boy who loves fetch and belly rubs. Vaccinated, house-trained and great with kids.', 'images/pets/dog-golden.svg',   'AVAILABLE', NOW() - INTERVAL 20 DAY),
 (2, 2, 'Luna',   'Dog', 'Labrador',         1, 'Female', 'Noida',         'Luna is a playful young Lab who adores water and long walks. She is still learning leash manners and learns fast.', 'images/pets/dog-labrador.svg', 'AVAILABLE', NOW() - INTERVAL 18 DAY),
 (3, 3, 'Milo',   'Cat', 'Persian',          3, 'Male',   'Delhi',         'Milo is a calm, fluffy lap cat. He enjoys quiet evenings and needs regular brushing.', 'images/pets/cat-persian.svg',   'AVAILABLE', NOW() - INTERVAL 16 DAY),
 (4, 3, 'Bella',  'Dog', 'Beagle',           4, 'Female', 'Ghaziabad',     'Bella is a curious Beagle with an amazing nose. Loves sniffing walks and is friendly with other dogs.', 'images/pets/dog-beagle.svg',    'AVAILABLE', NOW() - INTERVAL 15 DAY),
 (5, 2, 'Max',    'Dog', 'German Shepherd',  5, 'Male',   'Greater Noida', 'Max is a loyal, well-trained German Shepherd. He has just found his forever family!', 'images/pets/dog-gsd.svg',       'ADOPTED',   NOW() - INTERVAL 40 DAY),
 (6, 4, 'Coco',   'Dog', 'Indie Dog',        1, 'Female', 'Noida',         'Coco is a clever, street-smart Indie pup with endless energy. Indies are healthy, loyal and low maintenance.', 'images/pets/dog-indie.svg',    'AVAILABLE', NOW() - INTERVAL 12 DAY),
 (7, 3, 'Simba',  'Cat', 'Siamese',          2, 'Male',   'Delhi',         'Simba is a chatty Siamese who will tell you all about his day. Very affectionate and loves to climb.', 'images/pets/cat-siamese.svg',   'AVAILABLE', NOW() - INTERVAL 11 DAY),
 (8, 4, 'Daisy',  'Dog', 'Pomeranian',       3, 'Female', 'Gurugram',      'Daisy is a fluffy, confident Pom who loves attention. Perfect for apartment living.', 'images/pets/dog-pom.svg',       'AVAILABLE', NOW() - INTERVAL 9 DAY),
 (9, 2, 'Rocky',  'Dog', 'Labrador',         6, 'Male',   'Greater Noida', 'Rocky is a calm senior Lab who just wants a soft bed and a gentle family. Senior pets make wonderful companions.', 'images/pets/dog-labrador.svg', 'AVAILABLE', NOW() - INTERVAL 8 DAY),
 (10,4, 'Oreo',   'Cat', 'Indie Cat',        1, 'Female', 'Noida',         'Oreo is a black-and-white kitten who loves chasing shadows and curling up in laundry baskets.', 'images/pets/cat-indie.svg',     'AVAILABLE', NOW() - INTERVAL 6 DAY),
 (11,3, 'Snowy',  'Rabbit','Holland Lop',    1, 'Female', 'Delhi',         'Snowy is a soft, quiet bunny who enjoys fresh greens and gentle cuddles.', 'images/pets/rabbit.svg',        'PENDING',   NOW() - INTERVAL 2 DAY),
 (12,4, 'Pepper', 'Dog', 'Indie Dog',        0, 'Male',   'Ghaziabad',     'Pepper is a tiny rescue puppy found near a market. Dewormed and ready to be spoiled.', 'images/pets/dog-pepper.svg',    'PENDING',   NOW() - INTERVAL 1 DAY),
 (13,2, 'Shadow', 'Cat', 'Domestic Shorthair',4,'Male',   'Greater Noida', 'Shadow is a shy black cat. Listing returned for a clearer photo (demo of the REJECTED status).', 'images/pets/cat-black.svg',    'REJECTED',  NOW() - INTERVAL 5 DAY);

INSERT INTO adoption_applications (id, pet_id, adopter_id, application_date, message, status, applicant_name, phone, email, address, reason, experience, home_type, has_other_pets) VALUES
 (1, 1, 5, NOW() - INTERVAL 3 DAY, 'We have a big garden and work from home.',      'PENDING',   'Aarav Sharma', '9000000005', 'adopter@petfeet.com', 'B-12, Alpha 1, Greater Noida', 'Looking for an active companion for our family.', 'Grew up with a Labrador', 'House with garden', FALSE),
 (2, 3, 5, NOW() - INTERVAL 2 DAY, 'Milo looks like a perfect fit.',               'PENDING',   'Aarav Sharma', '9000000005', 'adopter@petfeet.com', 'B-12, Alpha 1, Greater Noida', 'My mother wants a calm companion cat.',        'None',                    'Apartment',         FALSE),
 (3, 5, 6, NOW() - INTERVAL 30 DAY,'Max would love our big terrace.',              'COMPLETED', 'Meera Iyer',   '9000000006', 'meera@petfeet.com',   'Sector 50, Noida',            'We lost our dog last year and are ready again.', '10 years with dogs',      'House with garden', FALSE),
 (4, 2, 6, NOW() - INTERVAL 4 DAY, 'Would Luna get along with a cat?',            'PENDING',   'Meera Iyer',   '9000000006', 'meera@petfeet.com',   'Sector 50, Noida',            'I would love a young dog to go jogging with.', 'Cats and dogs',           'Apartment',         TRUE),
 (5, 4, 7, NOW() - INTERVAL 10 DAY,'Bella seems lovely.',                          'REJECTED',  'Kabir Singh',  '9000000007', 'kabir@petfeet.com',   'Indirapuram, Ghaziabad',      'Want a pet for my kids.',                   'None',                    'Apartment',         FALSE),
 (6, 7, 7, NOW() - INTERVAL 1 DAY, 'Simba sounds adorable!',                       'PENDING',   'Kabir Singh',  '9000000007', 'kabir@petfeet.com',   'Indirapuram, Ghaziabad',      'Kids love cats.',                           'Had a cat as a child',    'Apartment',         FALSE);

INSERT INTO adoption_history (application_id, pet_id, adopter_id, shelter_id, adopted_on) VALUES (3, 5, 6, 2, NOW() - INTERVAL 28 DAY);

INSERT INTO messages (sender_id, receiver_id, message, created_at, is_read) VALUES
 (5, 2, 'Hi! Is Bruno good with small children?', NOW() - INTERVAL 2 DAY, FALSE),
 (2, 5, 'Hello Aarav! Yes, Bruno is great with kids. You are welcome to visit this weekend.', NOW() - INTERVAL 1 DAY, FALSE),
 (6, 3, 'Hello, is Milo still available?', NOW() - INTERVAL 3 DAY, TRUE);

INSERT INTO notifications (user_id, message, is_read, created_at) VALUES
 (2, 'New adoption application received for Bruno.', FALSE, NOW() - INTERVAL 3 DAY),
 (5, 'Welcome to PetFeet! Browse pets and find your new friend.', FALSE, NOW() - INTERVAL 5 DAY),
 (6, 'Congratulations! Your adoption of Max is complete.', TRUE, NOW() - INTERVAL 28 DAY),
 (7, 'Your application for Bella was not approved this time.', FALSE, NOW() - INTERVAL 9 DAY),
 (1, 'Pet listing "Snowy" is waiting for approval.', FALSE, NOW() - INTERVAL 2 DAY);

INSERT INTO favorites (user_id, pet_id) VALUES (5, 1), (5, 6), (6, 3), (7, 8);

INSERT INTO system_settings (setting_key, setting_value) VALUES
 ('site_name', 'PetFeet'),
 ('contact_email', 'hello@petfeet.com'),
 ('allow_registration', 'true'),
 ('require_pet_approval', 'true'),
 ('max_active_applications', '5');

INSERT INTO activity_logs (user_id, action, created_at) VALUES
 (1, 'System initialised with demo data', NOW() - INTERVAL 30 DAY),
 (2, 'Listed pet Bruno', NOW() - INTERVAL 20 DAY),
 (6, 'Adoption completed for Max', NOW() - INTERVAL 28 DAY);
