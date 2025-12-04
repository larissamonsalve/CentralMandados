/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.dao;

import com.centralmandados.connection.ConnectionFactory;
import com.centralmandados.model.Processo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Laris
 */
public class ProcessoDAO {
    public boolean salvarProcesso(Processo processo) {
        String sql = "INSERT INTO processo (numero_processo) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, processo.getNumeroProcesso());
            stmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean processoJaExiste(String numeroProcesso) {
        String sql = "SELECT COUNT(*) FROM processo WHERE numero_processo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroProcesso);
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


    public List<Processo> listarTodos() {
        List<Processo> lista = new ArrayList<>();
        String sql = "SELECT * FROM processo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Processo p = new Processo();
                p.setId(rs.getInt("id"));
                p.setNumeroProcesso(rs.getString("numero_processo"));
                lista.add(p);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }
}
