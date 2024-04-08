-- changeset 20240406-3
-- preconditions onFail:MARK_RAN onError:MARK_RAN
INSERT INTO app_user (first_name, last_name, password, email, phone, address, account_active, role, created_date)
VALUES
    ('John', 'Doe', '$2a$12$F1Wf.Y1.a.iCjijVWguCded0w6BTQQmYRFeE4YdvRwxyE0QR3FAde', 'john@example.com', '1234567890', '123 Street', true, 'ADMIN', CURRENT_TIMESTAMP),
    ('Jane', 'Doe', '$2a$12$F1Wf.Y1.a.iCjijVWguCded0w6BTQQmYRFeE4YdvRwxyE0QR3FAde', 'jane@example.com', '0987654321', '456 Avenue', true, 'MEMBER', CURRENT_TIMESTAMP),
    ('Alice', 'Smith', '$2a$12$F1Wf.Y1.a.iCjijVWguCded0w6BTQQmYRFeE4YdvRwxyE0QR3FAde', 'alice@example.com', '5555555555', '789 Boulevard', true, 'ADMIN', CURRENT_TIMESTAMP),
    ('Bob', 'Johnson', '$2a$12$F1Wf.Y1.a.iCjijVWguCded0w6BTQQmYRFeE4YdvRwxyE0QR3FAde', 'bob@example.com', '1111111111', '101 Main St', true, 'MEMBER', CURRENT_TIMESTAMP),
    ('Eve', 'Adams', '$2a$12$F1Wf.Y1.a.iCjijVWguCded0w6BTQQmYRFeE4YdvRwxyE0QR3FAde', 'eve@example.com', '2222222222', '202 Park Ave', true, 'MEMBER', CURRENT_TIMESTAMP);