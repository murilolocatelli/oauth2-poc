// senha - 123456
INSERT
INTO
    oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri,
    authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES
    ('cliente-app', 'customerapi', '$2a$10$nI56vH9GlzCR3DPij.A9rOYIVNJQLEtkPuLCY7CRPnSNFLeO/0Y82', 'read,write',
     'password,authorization_code,refresh_token', 'http://localhost:9000/callback','read,write', 3000, -1, NULL, 'false');
GO

// senha - abc
INSERT
INTO
    oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri,
    authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES
    ('resource-server', 'customerapi', '$2a$10$Mw0EACwvKxYhiVgOfYqpk.9D.MuIpbOFULml/ankypwJot7GTfiSO', 'read,write',
     'password,authorization_code,refresh_token', 'http://localhost:9000/callback','read,write,introspection', 3000, -1, NULL, 'false');
GO

insert into authority (description) values ('read');
GO

insert into authority (description) values ('write');
GO

// senha - abc123
insert into credential (email, password) values ('test@test.com', '$2a$10$yzbhDLvC4rHJ4vj27S25b.Gy3JukiuS9hnxvZ90cHQBIwlDH876hW');
GO

insert into credential_authorities (credential_id, authorities_id) values (1, 1);
GO

insert into credential_authorities (credential_id, authorities_id) values (1, 2);
GO
