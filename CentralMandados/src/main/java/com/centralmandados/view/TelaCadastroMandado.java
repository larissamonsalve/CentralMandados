/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.centralmandados.view;


import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import com.centralmandados.connection.ConnectionFactory;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author Laris
 */
public class TelaCadastroMandado extends javax.swing.JInternalFrame {

    public TelaCadastroMandado() {
        initComponents();
        carregarCombos();
    }
    
    private void carregarCombos() {
        try (Connection conn = ConnectionFactory.getConnection()) {
            String sqlOficial = "SELECT id, nome FROM oficial";
            try (PreparedStatement stmt = conn.prepareStatement(sqlOficial);
                ResultSet rs = stmt.executeQuery()) {
                
                cmbOficial.addItem("");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    cmbOficial.addItem(id + " - " + nome);
                }
            }
            
            cmbStatus.addItem("Pendente");
            cmbStatus.addItem("Cumprido");
            cmbStatus.addItem("Devolvido");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados nos comboBox:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getIdSelecionado(javax.swing.JComboBox<String> comboBox) {
        String selecionado = (String) comboBox.getSelectedItem();
        if (selecionado != null && selecionado.contains(" - ")) {
            return Integer.parseInt(selecionado.split(" - ")[0]);
        }
        return -1;
    }
    
    private void limparCampos() {
        txtNumeroMandado.setText("");
        txtDataEmissao.setText("");
        txtPrazoCumprimento.setText("");
        txtAnotacoes.setText("");
        
        txtNumeroMandado.requestFocus();
    }


    private void salvarMandado() {
        String numeroMandado = txtNumeroMandado.getText().trim();
        String dataEmissaoStr = txtDataEmissao.getText().trim();
        String prazoCumprimentoStr = txtPrazoCumprimento.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();
        String anotacoes = txtAnotacoes.getText().trim();

        if (numeroMandado.isEmpty() || dataEmissaoStr.isEmpty() || prazoCumprimentoStr.isEmpty() || status == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String numeroProcesso = txtNumeroProcesso.getText().trim();
        if (numeroProcesso.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o número do processo.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = ConnectionFactory.getConnection()) {
            String sqlProc = "SELECT id FROM processo WHERE numero_processo = ?";
            int idProcesso;
            try (PreparedStatement stmtProc = conn.prepareStatement(sqlProc)) {
                stmtProc.setString(1, numeroProcesso);
                try (ResultSet rsProc = stmtProc.executeQuery()) {
                    if (rsProc.next()) {
                        idProcesso = rsProc.getInt("id");
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Processo não encontrado: " + numeroProcesso,
                            "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            
            String sql = "INSERT INTO mandado (numero_mandado, data_emissao, prazo_cumprimento, status, anotacoes, id_processo, id_oficial) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, numeroMandado);

                // Converte datas
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate dataEmissao = LocalDate.parse(dataEmissaoStr, formatter);
                LocalDate prazoCumprimento = LocalDate.parse(prazoCumprimentoStr, formatter);

                stmt.setDate(2, java.sql.Date.valueOf(dataEmissao));
                stmt.setDate(3, java.sql.Date.valueOf(prazoCumprimento));

                stmt.setString(4, status);
                stmt.setString(5, anotacoes);
                  

                // Pegando os IDs dos comboBox
                int idOficial = getIdSelecionado(cmbOficial);

                stmt.setInt(6, idProcesso);

                // Permitir salvar sem oficial
                if (idOficial > 0) {
                    stmt.setInt(7, idOficial);
                } else {
                    stmt.setNull(7, Types.INTEGER);
                }

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Mandado salvo com sucesso!");
                limparCampos();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar mandado:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painelCadastroMandados = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNumeroMandado = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDataEmissao = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPrazoCumprimento = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cmbOficial = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAnotacoes = new javax.swing.JTextArea();
        btnSalvarMandado = new javax.swing.JButton();
        btnCancelarMandado = new javax.swing.JButton();
        txtNumeroProcesso = new javax.swing.JTextField();

        setTitle("Cadastro de Mandados");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Número do Mandado:");

        txtNumeroMandado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroMandadoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Número do processo:");

        jLabel3.setBackground(new java.awt.Color(0, 102, 51));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Data de Emissão:");

        try {
            txtDataEmissao.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel4.setBackground(new java.awt.Color(0, 102, 51));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Prazo para cumprir:");

        try {
            txtPrazoCumprimento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel5.setBackground(new java.awt.Color(0, 102, 51));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Status:");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione" }));
        cmbStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbStatusActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(0, 102, 51));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Oficial de Justiça:");

        cmbOficial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione" }));

        jLabel7.setBackground(new java.awt.Color(0, 102, 51));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Descrição:");

        txtAnotacoes.setColumns(20);
        txtAnotacoes.setRows(5);
        jScrollPane1.setViewportView(txtAnotacoes);

        btnSalvarMandado.setBackground(new java.awt.Color(0, 102, 51));
        btnSalvarMandado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSalvarMandado.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvarMandado.setText("Salvar");
        btnSalvarMandado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarMandadoActionPerformed(evt);
            }
        });

        btnCancelarMandado.setBackground(new java.awt.Color(0, 102, 51));
        btnCancelarMandado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelarMandado.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelarMandado.setText("Cancelar");
        btnCancelarMandado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarMandadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout painelCadastroMandadosLayout = new javax.swing.GroupLayout(painelCadastroMandados);
        painelCadastroMandados.setLayout(painelCadastroMandadosLayout);
        painelCadastroMandadosLayout.setHorizontalGroup(
            painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelCadastroMandadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbOficial, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNumeroMandado)
                    .addComponent(txtDataEmissao)
                    .addComponent(txtPrazoCumprimento)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                    .addComponent(txtNumeroProcesso))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelCadastroMandadosLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(btnCancelarMandado, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(btnSalvarMandado, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        painelCadastroMandadosLayout.setVerticalGroup(
            painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelCadastroMandadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNumeroMandado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNumeroProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDataEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtPrazoCumprimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cmbOficial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSalvarMandado, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                    .addComponent(btnCancelarMandado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(13, 13, 13))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(painelCadastroMandados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(painelCadastroMandados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNumeroMandadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroMandadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroMandadoActionPerformed

    private void btnSalvarMandadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarMandadoActionPerformed
        // TODO add your handling code here:
        salvarMandado();
    }//GEN-LAST:event_btnSalvarMandadoActionPerformed

    private void cmbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbStatusActionPerformed

    private void btnCancelarMandadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarMandadoActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarMandadoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarMandado;
    private javax.swing.JButton btnSalvarMandado;
    private javax.swing.JComboBox<String> cmbOficial;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel painelCadastroMandados;
    private javax.swing.JTextArea txtAnotacoes;
    private javax.swing.JFormattedTextField txtDataEmissao;
    private javax.swing.JTextField txtNumeroMandado;
    private javax.swing.JTextField txtNumeroProcesso;
    private javax.swing.JFormattedTextField txtPrazoCumprimento;
    // End of variables declaration//GEN-END:variables
}
