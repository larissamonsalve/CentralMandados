/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.dao;

import com.centralmandados.connection.ConnectionFactory;
import com.centralmandados.model.Mandado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 *
 * @author Laris
 */
public class MandadoDAO {
    public void salvarMandado(Mandado mandado) {
        String sql = "INSERT INTO mandado (numero_mandado, data_emissao, prazo_cumprimento, status, anotacoes, id_processo, id_oficial) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, mandado.getNumeroMandado());
            stmt.setDate(2, mandado.getDataEmissao());
            stmt.setDate(3, mandado.getPrazoCumprimento());
            stmt.setString(4, mandado.getStatus());
            stmt.setString(5, mandado.getAnotacoes());
            stmt.setInt(6, mandado.getIdProcesso());

            if (mandado.getIdOficial() != null) {
                stmt.setInt(7, (int) mandado.getIdOficial());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();
            System.out.println("Mandado salvo com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean atualizar(Mandado mandado) {
        String sql = "UPDATE mandado SET numero_mandado = ?, data_emissao = ?, prazo_cumprimento = ?, status = ?, anotacoes = ?, id_processo = ?, id_oficial = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mandado.getNumeroMandado());
            stmt.setDate(2, mandado.getDataEmissao());
            stmt.setDate(3, mandado.getPrazoCumprimento());
            stmt.setString(4, mandado.getStatus());
            stmt.setString(5, mandado.getAnotacoes());

            if (mandado.getProcesso() != null) {
                stmt.setInt(6, mandado.getProcesso().getId());
            } else {
                throw new SQLException("Processo não pode ser nulo ao atualizar mandado.");
            }

            if (mandado.getOficial() != null) {
                stmt.setInt(7, mandado.getOficial().getId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.setInt(8, mandado.getId());

            int linhasAtualizadas = stmt.executeUpdate();
            return linhasAtualizadas > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
