/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.dao;

import com.centralmandados.connection.ConnectionFactory;
import com.centralmandados.model.Oficial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Laris
 */
public class OficialDAO {
    public void salvar(Oficial oficial) {
        String sql = "INSERT INTO oficial (nome) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, oficial.getNome());
            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Oficial> listarTodos() {
        List<Oficial> lista = new ArrayList<>();
        String sql = "SELECT * FROM oficial";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Oficial o = new Oficial();
                o.setId(rs.getInt("id"));
                o.setNome(rs.getString("nome"));
                lista.add(o);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }
}
