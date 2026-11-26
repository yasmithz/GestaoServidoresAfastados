


CREATE DATABASE IF NOT EXISTS gestao_servidores_afastados
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gestao_servidores_afastados;


CREATE TABLE servidor (
    id_servidor INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    matricula VARCHAR(20) NOT NULL,
    cpf CHAR(11) NOT NULL,
    departamento VARCHAR(100) NOT NULL,
    cargo VARCHAR(100) NOT NULL,

    PRIMARY KEY (id_servidor),
    UNIQUE (matricula),
    UNIQUE (cpf),

    CHECK (cpf REGEXP '^[0-9]{11}$')
) ENGINE=InnoDB;



CREATE TABLE afastamento (
    id_afastamento INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_servidor INT UNSIGNED NOT NULL,
    tipo_afastamento VARCHAR(100) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim_prevista DATE NOT NULL,
    data_fim_efetiva DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO',
    observacoes TEXT NULL,

    PRIMARY KEY (id_afastamento),

    FOREIGN KEY (id_servidor)
        REFERENCES servidor(id_servidor)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CHECK (status IN (
        'PROGRAMADO',
        'EM_ANDAMENTO',
        'CONCLUIDO',
        'CANCELADO'
    )),

    CHECK (data_fim_prevista >= data_inicio),

    CHECK (
        data_fim_efetiva IS NULL
        OR data_fim_efetiva >= data_inicio
    ),

    CHECK (
        (status = 'CONCLUIDO' AND data_fim_efetiva IS NOT NULL)
        OR
        (status <> 'CONCLUIDO' AND data_fim_efetiva IS NULL)
    )
) ENGINE=InnoDB;



CREATE TABLE relatorio_semestral (
    id_relatorio INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_afastamento INT UNSIGNED NOT NULL,
    arquivo_relatorio VARCHAR(500) NULL,
    status_entrega VARCHAR(15) NOT NULL DEFAULT 'PENDENTE',
    ano_referencia SMALLINT UNSIGNED NOT NULL,
    semestre TINYINT UNSIGNED NOT NULL,
    data_limite_submissao DATE NOT NULL,
    data_entrega DATE NULL,

    PRIMARY KEY (id_relatorio),

    UNIQUE (id_afastamento, ano_referencia, semestre),
    UNIQUE (id_relatorio, id_afastamento),

    FOREIGN KEY (id_afastamento)
        REFERENCES afastamento(id_afastamento)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CHECK (ano_referencia BETWEEN 1000 AND 9999),
    CHECK (semestre IN (1, 2)),
    CHECK (status_entrega IN ('PENDENTE', 'ENTREGUE')),

    CHECK (
        (status_entrega = 'PENDENTE'
            AND data_entrega IS NULL)
        OR
        (status_entrega = 'ENTREGUE'
            AND data_entrega IS NOT NULL
            AND arquivo_relatorio IS NOT NULL
            AND CHAR_LENGTH(TRIM(arquivo_relatorio)) > 0)
    )
) ENGINE=InnoDB;



CREATE TABLE pendencia (
    id_pendencia INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_afastamento INT UNSIGNED NOT NULL,
    id_relatorio INT UNSIGNED NULL,
    descricao TEXT NOT NULL,
    data_abertura DATE NOT NULL,
    data_resolucao DATE NULL,
    status_pendencia VARCHAR(15) NOT NULL DEFAULT 'ABERTA',

    PRIMARY KEY (id_pendencia),

    FOREIGN KEY (id_afastamento)
        REFERENCES afastamento(id_afastamento)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    FOREIGN KEY (id_relatorio, id_afastamento)
        REFERENCES relatorio_semestral(id_relatorio, id_afastamento)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CHECK (status_pendencia IN (
        'ABERTA',
        'EM_ANALISE',
        'RESOLVIDA',
        'CANCELADA'
    )),

    CHECK (
        data_resolucao IS NULL
        OR data_resolucao >= data_abertura
    ),

    CHECK (
        (status_pendencia = 'RESOLVIDA'
            AND data_resolucao IS NOT NULL)
        OR
        (status_pendencia <> 'RESOLVIDA'
            AND data_resolucao IS NULL)
    )
) ENGINE=InnoDB;



CREATE TABLE alerta_prazo (
    id_alerta INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_pendencia INT UNSIGNED NOT NULL,
    mensagem_alerta TEXT NOT NULL,
    dias_antecedencia SMALLINT UNSIGNED NOT NULL,
    status_envio VARCHAR(15) NOT NULL DEFAULT 'PENDENTE',
    data_envio DATETIME NULL,

    PRIMARY KEY (id_alerta),

    FOREIGN KEY (id_pendencia)
        REFERENCES pendencia(id_pendencia)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CHECK (status_envio IN (
        'PENDENTE',
        'ENVIADO',
        'FALHOU',
        'CANCELADO'
    )),

    CHECK (
        (status_envio = 'ENVIADO'
            AND data_envio IS NOT NULL)
        OR
        (status_envio <> 'ENVIADO'
            AND data_envio IS NULL)
    )
) ENGINE=InnoDB;


USE gestao_servidores_afastados;

START TRANSACTION;


INSERT INTO servidor
(nome, matricula, cpf, departamento, cargo)
VALUES
('Ana Souza', '2026001', '12345678901',
 'Direção de Ensino', 'Professora'),

('Bruno Lima', '2026002', '23456789012',
 'Secretaria Acadêmica', 'Assistente Administrativo'),

('Carla Mendes', '2026003', '34567890123',
 'Coordenação Pedagógica', 'Coordenadora'),

('Diego Santos', '2026004', '45678901234',
 'Biblioteca', 'Bibliotecário'),

('Elisa Costa', '2026005', '56789012345',
 'Direção Geral', 'Diretora');


-- Recuperar os IDs gerados automaticamente

SET @s1 = (SELECT id_servidor FROM servidor WHERE matricula = '2026001');
SET @s2 = (SELECT id_servidor FROM servidor WHERE matricula = '2026002');
SET @s3 = (SELECT id_servidor FROM servidor WHERE matricula = '2026003');
SET @s4 = (SELECT id_servidor FROM servidor WHERE matricula = '2026004');
SET @s5 = (SELECT id_servidor FROM servidor WHERE matricula = '2026005');



INSERT INTO afastamento
(id_servidor, tipo_afastamento, data_inicio,
 data_fim_prevista, data_fim_efetiva, status, observacoes)
VALUES
(@s1, 'Mestrado', '2026-01-10', '2026-12-31',
 NULL, 'EM_ANDAMENTO', 'Afastamento para qualificação profissional'),

(@s2, 'Capacitação', '2025-01-10', '2025-12-20',
 '2025-12-18', 'CONCLUIDO', 'Capacitação concluída'),

(@s3, 'Licença para estudo', '2026-12-01', '2027-05-31',
 NULL, 'PROGRAMADO', 'Afastamento programado'),

(@s4, 'Licença médica', '2026-03-01', '2026-11-30',
 NULL, 'EM_ANDAMENTO', 'Afastamento em andamento'),

(@s5, 'Licença particular', '2026-04-01', '2026-06-30',
 NULL, 'CANCELADO', 'Solicitação cancelada');


SET @a1 = (SELECT id_afastamento FROM afastamento WHERE id_servidor = @s1);
SET @a2 = (SELECT id_afastamento FROM afastamento WHERE id_servidor = @s2);
SET @a3 = (SELECT id_afastamento FROM afastamento WHERE id_servidor = @s3);
SET @a4 = (SELECT id_afastamento FROM afastamento WHERE id_servidor = @s4);
SET @a5 = (SELECT id_afastamento FROM afastamento WHERE id_servidor = @s5);



INSERT INTO relatorio_semestral
(id_afastamento, arquivo_relatorio, status_entrega,
 ano_referencia, semestre, data_limite_submissao, data_entrega)
VALUES
(@a1, NULL, 'PENDENTE', 2026, 1, '2026-07-31', NULL),

(@a2, 'relatorios/bruno_2025_2.pdf', 'ENTREGUE',
 2025, 2, '2026-01-31', '2026-01-25'),

(@a3, NULL, 'PENDENTE', 2026, 2, '2026-12-15', NULL),

(@a4, 'relatorios/diego_2026_1.pdf', 'ENTREGUE',
 2026, 1, '2026-07-31', '2026-07-20'),

(@a5, NULL, 'PENDENTE', 2026, 1, '2026-10-31', NULL);


SET @r1 = (SELECT id_relatorio FROM relatorio_semestral WHERE id_afastamento = @a1);
SET @r2 = (SELECT id_relatorio FROM relatorio_semestral WHERE id_afastamento = @a2);
SET @r3 = (SELECT id_relatorio FROM relatorio_semestral WHERE id_afastamento = @a3);
SET @r4 = (SELECT id_relatorio FROM relatorio_semestral WHERE id_afastamento = @a4);
SET @r5 = (SELECT id_relatorio FROM relatorio_semestral WHERE id_afastamento = @a5);



INSERT INTO pendencia
(id_afastamento, id_relatorio, descricao,
 data_abertura, data_resolucao, status_pendencia)
VALUES
(@a1, @r1, 'Relatório semestral não entregue',
 '2026-08-01', NULL, 'ABERTA'),

(@a2, @r2, 'Conferência dos documentos entregues',
 '2026-01-20', '2026-01-26', 'RESOLVIDA'),

(@a3, @r3, 'Aguardando documentação complementar',
 '2026-11-01', NULL, 'EM_ANALISE'),

(@a4, @r4, 'Conferência de registro do relatório',
 '2026-07-22', NULL, 'CANCELADA'),

(@a5, @r5, 'Relatório semestral em atraso',
 '2026-11-01', NULL, 'ABERTA');


SET @p1 = (SELECT id_pendencia FROM pendencia WHERE id_afastamento = @a1);
SET @p2 = (SELECT id_pendencia FROM pendencia WHERE id_afastamento = @a2);
SET @p3 = (SELECT id_pendencia FROM pendencia WHERE id_afastamento = @a3);
SET @p4 = (SELECT id_pendencia FROM pendencia WHERE id_afastamento = @a4);
SET @p5 = (SELECT id_pendencia FROM pendencia WHERE id_afastamento = @a5);



INSERT INTO alerta_prazo
(id_pendencia, mensagem_alerta, dias_antecedencia,
 status_envio, data_envio)
VALUES
(@p1, 'Relatório pendente: verificar entrega', 7,
 'PENDENTE', NULL),

(@p2, 'Pendência resolvida: documentos conferidos', 5,
 'ENVIADO', '2026-01-20 09:00:00'),

(@p3, 'Solicitação de documentação complementar', 10,
 'FALHOU', NULL),

(@p4, 'Alerta cancelado devido ao cancelamento da pendência', 3,
 'CANCELADO', NULL),

(@p5, 'Atenção: relatório semestral em atraso', 7,
 'PENDENTE', NULL);


COMMIT;

-- Consultar os cinco servidores
SELECT * FROM servidor;

-- Consultar os cinco afastamentos
SELECT * FROM afastamento;

-- Consultar os cinco relatórios
SELECT * FROM relatorio_semestral;

-- Consultar as cinco pendências
SELECT * FROM pendencia;

-- Consultar os cinco alertas
SELECT * FROM alerta_prazo;


SELECT
    s.nome AS servidor,
    s.matricula,
    a.tipo_afastamento,
    r.ano_referencia,
    r.semestre,
    r.data_limite_submissao,
    DATEDIFF(CURDATE(), r.data_limite_submissao)
        AS dias_atraso

FROM relatorio_semestral r

INNER JOIN afastamento a
    ON a.id_afastamento = r.id_afastamento

INNER JOIN servidor s
    ON s.id_servidor = a.id_servidor

WHERE r.status_entrega = 'PENDENTE'
    AND r.data_limite_submissao < CURDATE()
    AND a.status <> 'CANCELADO'





