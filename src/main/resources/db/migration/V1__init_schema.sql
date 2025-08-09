-- ===== 1. Таблица структурных подразделений =====
CREATE TABLE departments (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(100) NOT NULL,
                             is_contractor BOOLEAN DEFAULT FALSE
);

-- ===== 2. Таблица пользователей =====
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       last_name VARCHAR(50) NOT NULL,
                       first_name VARCHAR(50) NOT NULL,
                       middle_name VARCHAR(50),
                       position VARCHAR(100) NOT NULL,
                       department_id INT REFERENCES departments(id) ON DELETE SET NULL
);

-- ===== 3. История руководителей подразделений =====
CREATE TABLE department_managers (
                                     id SERIAL PRIMARY KEY,
                                     department_id INT NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
                                     user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                     start_date DATE NOT NULL,
                                     end_date DATE
);

-- ===== 4. Таблица проектов =====
CREATE TABLE projects (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          manager_id INT REFERENCES users(id) ON DELETE SET NULL,
                          curator_id INT REFERENCES users(id) ON DELETE SET NULL,
                          start_date DATE NOT NULL,
                          end_date DATE NOT NULL,
                          is_active BOOLEAN DEFAULT TRUE
);

-- ===== 5. Таблица связей проектов с подрядчиками =====
CREATE TABLE project_contractors (
                                     id SERIAL PRIMARY KEY,
                                     project_id INT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                                     department_id INT NOT NULL REFERENCES departments(id) ON DELETE CASCADE
);

-- ===== 6. Таблица участия пользователей в проектах =====
CREATE TABLE participation (
                               id SERIAL PRIMARY KEY,
                               user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               project_id INT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                               role VARCHAR(100),
                               participation_percentage NUMERIC(5,2) CHECK (participation_percentage >= 0 AND participation_percentage <= 100),
                               start_date DATE,
                               end_date DATE
);
