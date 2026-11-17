IF OBJECT_ID('dbo.solicitacoes_ajuda', 'U') IS NOT NULL DROP TABLE dbo.solicitacoes_ajuda;
IF OBJECT_ID('dbo.pontos_agua', 'U') IS NOT NULL DROP TABLE dbo.pontos_agua;
IF OBJECT_ID('dbo.regioes_afetadas', 'U') IS NOT NULL DROP TABLE dbo.regioes_afetadas;

-- Criar tabela regioes_afetadas
CREATE TABLE regioes_afetadas (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    municipio VARCHAR(255) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    populacao_afetada INT,
    nivel_risco VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    data_registro DATE
);

-- Criar tabela pontos_agua
CREATE TABLE pontos_agua (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(255),
    municipio VARCHAR(255) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    capacidade_litros BIGINT,
    status VARCHAR(20) NOT NULL,
    data_atualizacao DATE,
    regiao_id BIGINT NOT NULL,
    CONSTRAINT fk_regiao_ponto FOREIGN KEY (regiao_id) REFERENCES regioes_afetadas(id) ON DELETE CASCADE
);

-- Criar tabela solicitacoes_ajuda
CREATE TABLE solicitacoes_ajuda (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    protocolo VARCHAR(20) NOT NULL,
    nome_solicitante VARCHAR(255) NOT NULL,
    contato VARCHAR(255),
    descricao VARCHAR(500) NOT NULL,
    pessoas_afetadas INT,
    prioridade INT,
    status VARCHAR(20) NOT NULL,
    data_solicitacao DATE,
    regiao_id BIGINT NOT NULL,
    CONSTRAINT fk_regiao_solicitacao FOREIGN KEY (regiao_id) REFERENCES regioes_afetadas(id) ON DELETE CASCADE
);

-- Inserts iniciais de teste
INSERT INTO regioes_afetadas (nome, municipio, uf, populacao_afetada, nivel_risco, status, latitude, longitude, data_registro)
VALUES ('Baixada Fluminense', 'Nova Iguacu', 'RJ', 25000, 'ALTO', 'ATIVA', -22.759600, -43.451000, '2026-02-10');

INSERT INTO regioes_afetadas (nome, municipio, uf, populacao_afetada, nivel_risco, status, latitude, longitude, data_registro)
VALUES ('Vale do Taquari', 'Lajeado', 'RS', 12000, 'CRITICO', 'ATIVA', -29.466900, -51.961900, '2026-02-12');

INSERT INTO pontos_agua (nome, endereco, municipio, uf, tipo, capacidade_litros, status, data_atualizacao, regiao_id)
VALUES ('Caixa Dagua Central', 'Praca Central, s/n', 'Nova Iguacu', 'RJ', 'CAIXA_DAGUA', 50000, 'DISPONIVEL', '2026-02-11', 1);

INSERT INTO pontos_agua (nome, endereco, municipio, uf, tipo, capacidade_litros, status, data_atualizacao, regiao_id)
VALUES ('Poco Comunitario Sao Jose', 'Rua Sao Jose, 120', 'Lajeado', 'RS', 'POCO', 20000, 'DISPONIVEL', '2026-02-13', 2);

INSERT INTO pontos_agua (nome, endereco, municipio, uf, tipo, capacidade_litros, status, data_atualizacao, regiao_id)
VALUES ('Estacao de Tratamento Lajeado', 'Av. Sicrano, 500', 'Lajeado', 'RS', 'ESTACAO_TRATAMENTO', 0, 'CONTAMINADO', '2026-02-13', 2);

INSERT INTO solicitacoes_ajuda (protocolo, nome_solicitante, contato, descricao, pessoas_afetadas, prioridade, status, data_solicitacao, regiao_id)
VALUES ('NV-2026-0001', 'Maria Silva', '(21) 99999-0001', 'Falta de agua ha 3 dias no bairro Nova Iguacu.', 4, 1, 'ABERTA', '2026-02-11', 1);

INSERT INTO solicitacoes_ajuda (protocolo, nome_solicitante, contato, descricao, pessoas_afetadas, prioridade, status, data_solicitacao, regiao_id)
VALUES ('NV-2026-0002', 'Joao Souza', '(51) 99999-0002', 'Comunidade isolada pela enchente, necessita de agua potavel.', 22, 1, 'EM_ATENDIMENTO', '2026-02-13', 2);
