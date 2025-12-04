-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 15/07/2025 às 01:51
-- Versão do servidor: 10.4.32-MariaDB
-- Versão do PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `centralmandados`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `historico_status`
--

CREATE TABLE `historico_status` (
  `id` int(11) NOT NULL,
  `id_mandado` int(11) NOT NULL,
  `status_anterior` varchar(20) NOT NULL,
  `status_novo` varchar(20) NOT NULL,
  `data_alteracao` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `mandado`
--

CREATE TABLE `mandado` (
  `id` int(11) NOT NULL,
  `numero_mandado` varchar(50) NOT NULL,
  `data_emissao` date NOT NULL,
  `prazo_cumprimento` date NOT NULL,
  `status` varchar(20) NOT NULL CHECK (`status` in ('Pendente','Cumprido','Devolvido')),
  `anotacoes` text DEFAULT NULL,
  `id_processo` int(11) NOT NULL,
  `id_oficial` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `mandado`
--

INSERT INTO `mandado` (`id`, `numero_mandado`, `data_emissao`, `prazo_cumprimento`, `status`, `anotacoes`, `id_processo`, `id_oficial`) VALUES
(1, '1', '2025-07-13', '2025-07-14', 'Cumprido', 'Testes', 1, 2),
(2, '2', '2025-07-13', '2025-08-15', 'Cumprido', 'Teste2', 4, 4),
(3, '3', '2026-07-14', '2026-07-14', 'Pendente', 'aa', 1, NULL),
(4, '5', '2025-07-15', '2025-07-16', 'Devolvido', 'abc', 1, 1),
(5, '9', '2025-07-13', '2025-07-16', 'Pendente', 'ooo', 1, NULL),
(6, '4', '2025-07-13', '2025-08-20', 'Cumprido', '', 2, 2),
(7, '10', '2026-08-25', '2026-08-28', 'Cumprido', '', 4, 5),
(8, '20', '2025-08-31', '2025-09-05', 'Cumprido', 'pp', 1, 3),
(9, '90', '2025-01-15', '2025-01-18', 'Devolvido', 'www', 2, 1),
(10, '12', '2025-01-14', '2025-01-16', 'Pendente', 'aqw', 2, NULL),
(11, '100', '2025-07-13', '2025-07-14', 'Pendente', 'bb', 5, NULL),
(12, '500', '2025-07-14', '2025-07-15', 'Cumprido', 'teste1', 1, 13),
(13, '600', '2025-07-14', '2025-07-14', 'Cumprido', 'tt', 1, 5),
(14, '30', '2025-07-14', '2025-07-15', 'Cumprido', '', 3, 2),
(15, '60', '2025-07-14', '2025-07-16', 'Devolvido', '', 4, 9),
(16, '80', '2025-07-14', '2025-07-15', 'Pendente', '', 3, NULL),
(17, '90', '2025-07-17', '2025-07-18', 'Pendente', '', 4, NULL),
(19, '1209837', '1979-03-21', '2000-08-08', 'Cumprido', '????????A', 1, 13),
(20, '1000', '2000-07-20', '2000-07-20', 'Cumprido', 'oi', 1, 9);

-- --------------------------------------------------------

--
-- Estrutura para tabela `oficial`
--

CREATE TABLE `oficial` (
  `id` int(11) NOT NULL,
  `nome` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `oficial`
--

INSERT INTO `oficial` (`id`, `nome`) VALUES
(1, 'Carlos Silva'),
(2, 'Fernanda Souza'),
(3, 'João Pereira'),
(4, 'Mariana Lima'),
(5, 'Rafael Oliveira'),
(9, 'Larissa Monsalve'),
(10, 'Katllen Lizandra'),
(11, 'Pedro Henrique'),
(13, 'Taynara Ribeiro'),
(14, 'Sanna Kellen'),
(15, 'Lucas Ferreira'),
(16, 'Pedro Eduardo de Souza Monsalve');

-- --------------------------------------------------------

--
-- Estrutura para tabela `processo`
--

CREATE TABLE `processo` (
  `id` int(11) NOT NULL,
  `numero_processo` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `processo`
--

INSERT INTO `processo` (`id`, `numero_processo`) VALUES
(1, '0001234-56.2025.8.01.0001'),
(6, '0001234-56.2025.8.01.0006'),
(2, '0005678-90.2025.8.01.0002'),
(3, '0012345-67.2025.8.01.0003'),
(4, '0016789-01.2025.8.01.0004'),
(5, '0023456-78.2025.8.01.0005');

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuario`
--

CREATE TABLE `usuario` (
  `id` int(11) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `login` varchar(50) NOT NULL,
  `senha` varchar(100) NOT NULL,
  `tipo` varchar(20) NOT NULL DEFAULT 'comum'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `usuario`
--

INSERT INTO `usuario` (`id`, `nome`, `login`, `senha`, `tipo`) VALUES
(1, 'Usuario', 'usuario', '123', 'comum'),
(2, 'Admin', 'admin', 'admin', 'admin');

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `historico_status`
--
ALTER TABLE `historico_status`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_mandado` (`id_mandado`);

--
-- Índices de tabela `mandado`
--
ALTER TABLE `mandado`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_processo` (`id_processo`),
  ADD KEY `id_oficial` (`id_oficial`);

--
-- Índices de tabela `oficial`
--
ALTER TABLE `oficial`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `processo`
--
ALTER TABLE `processo`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `numero_processo` (`numero_processo`);

--
-- Índices de tabela `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login` (`login`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `historico_status`
--
ALTER TABLE `historico_status`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `mandado`
--
ALTER TABLE `mandado`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de tabela `oficial`
--
ALTER TABLE `oficial`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de tabela `processo`
--
ALTER TABLE `processo`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de tabela `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `historico_status`
--
ALTER TABLE `historico_status`
  ADD CONSTRAINT `historico_status_ibfk_1` FOREIGN KEY (`id_mandado`) REFERENCES `mandado` (`id`);

--
-- Restrições para tabelas `mandado`
--
ALTER TABLE `mandado`
  ADD CONSTRAINT `mandado_ibfk_1` FOREIGN KEY (`id_processo`) REFERENCES `processo` (`id`),
  ADD CONSTRAINT `mandado_ibfk_2` FOREIGN KEY (`id_oficial`) REFERENCES `oficial` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
