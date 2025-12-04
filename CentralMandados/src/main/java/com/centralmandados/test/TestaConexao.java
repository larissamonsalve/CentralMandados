/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.test;

import com.centralmandados.connection.ConnectionFactory;
import java.sql.Connection;
/**
 *
 * @author Laris
 */
public class TestaConexao {
    public static void main(String[] args) {
        try (Connection con = ConnectionFactory.getConnection()) {
            System.out.println("✅ Conexão realizada com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar:");
            e.printStackTrace();
        }
    }
}
