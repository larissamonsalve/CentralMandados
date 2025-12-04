/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.dao;

import com.centralmandados.connection.ConnectionFactory;
import com.centralmandados.model.Usuario;

import java.sql.*;
/**
 *
 * @author Laris
 */
public class UsuarioDAO {
     public Usuario autenticar(String login, String senha) {
        Usuario usuario = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionFactory.getConnection();
            String sql = "SELECT id, nome, login, senha, tipo FROM usuario WHERE login = ? AND senha = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, senha);
            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setLogin(rs.getString("login"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipo(rs.getString("tipo"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }

        return usuario;
    } 
    
    public boolean salvar(Usuario u) {
        String sql = "INSERT INTO usuario (nome, login, senha, tipo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getLogin());
            stmt.setString(3, u.getSenha());
            stmt.setString(4, u.getTipo()); 
            
            int linhas = stmt.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.err.println("Login já existe: " + u.getLogin());
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }
    
    public boolean existeLogin(String login) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE login = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public boolean excluir(String login) {
        String sql = "DELETE FROM usuario WHERE login = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
