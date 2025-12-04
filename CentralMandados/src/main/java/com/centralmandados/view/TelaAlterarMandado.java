/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.centralmandados.view;

import com.centralmandados.connection.ConnectionFactory;
import com.centralmandados.dao.MandadoDAO;
import com.centralmandados.model.Mandado;
import com.centralmandados.model.Oficial;
import com.centralmandados.model.Processo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Laris
 */
public class TelaAlterarMandado extends javax.swing.JInternalFrame {
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private JPopupMenu popupMandados;
    private List<String> registrosMandados;
    
    
    public TelaAlterarMandado() {
        initComponents();
        carregarCombos();
        carregarMandados();
        configurarAutocompleteMandados();
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
    
    private void carregarMandados() {
        registrosMandados = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, numero_mandado FROM mandado");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                registrosMandados.add(rs.getInt("id") + " - " + rs.getString("numero_mandado"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void configurarAutocompleteMandados() {
        popupMandados = new JPopupMenu();
        txtNumeroMandadoBuscado.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { showSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { showSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) {}
            
            private void showSuggestions() {
                String text = txtNumeroMandadoBuscado.getText().toLowerCase();
                popupMandados.removeAll();
                if (text.isEmpty()) {
                    popupMandados.setVisible(false);
                    return;
                }
                for (String item : registrosMandados) {
                    if (item.toLowerCase().contains(text)) {
                        JMenuItem menuItem = new JMenuItem(item);
                        menuItem.addActionListener(ae -> {
                            txtNumeroMandadoBuscado.setText(item);
                            popupMandados.setVisible(false);
                            buscarMandado();//aqui preenche os dados
                        });
                        popupMandados.add(menuItem);
                    }
                }
                if (popupMandados.getComponentCount() > 0) {
                    popupMandados.show(txtNumeroMandadoBuscado, 0, txtNumeroMandadoBuscado.getHeight());
                } else {
                    popupMandados.setVisible(false);
                }
            }
        });
    }
    
    private void buscarMandado() {
        String sel = txtNumeroMandadoBuscado.getText();
        if (!sel.contains(" - ")) {
            JOptionPane.showMessageDialog(this, "Digite e selecione um mandado válido.");
            return;
        }
        int id = Integer.parseInt(sel.split(" - ")[0]);
        String sql = "SELECT m.*, p.numero_processo FROM mandado m JOIN processo p ON m.id_processo=p.id WHERE m.id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    txtDataEmissao.setText(sdf.format(rs.getDate("data_emissao")));
                    txtPrazoCumprimento.setText(sdf.format(rs.getDate("prazo_cumprimento")));
                    cmbStatus.setSelectedItem(rs.getString("status"));
                    txtAnotacoes.setText(rs.getString("anotacoes"));
                    txtNumeroProcesso.setText(rs.getString("numero_processo"));

                    int oficialId = rs.getInt("id_oficial");
                    if (rs.wasNull()) cmbOficial.setSelectedIndex(0);
                    else selecionarComboPorId(cmbOficial, oficialId);
                } else {
                    JOptionPane.showMessageDialog(this, "Mandado não encontrado.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar: " + e.getMessage());
        }
    }
    
    private void salvarAlteracoes() throws ParseException {
        String sel = txtNumeroMandado.getText();
        if (!sel.contains(" - ")) {
            JOptionPane.showMessageDialog(this, "Selecione um mandado válido antes de salvar.");
            return;
        }
        int idMandado = Integer.parseInt(sel.split(" - ")[0]);
        Mandado mandado = new Mandado();
        mandado.setId(idMandado);
        try {
            mandado.setNumeroMandado(sel.split(" - ",2)[1]);
            mandado.setDataEmissao(new java.sql.Date(sdf.parse(txtDataEmissao.getText()).getTime()));
            mandado.setPrazoCumprimento(new java.sql.Date(sdf.parse(txtPrazoCumprimento.getText()).getTime()));
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido.");
            return;
        }

        mandado.setStatus((String) cmbStatus.getSelectedItem());
        mandado.setAnotacoes(txtAnotacoes.getText());
        
        
        String numProc = txtNumeroProcesso.getText().trim();
        if (numProc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o número do processo.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection conn = ConnectionFactory.getConnection()) {
            String sql = "SELECT id FROM processo WHERE numero_processo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, numProc);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        mandado.setProcesso(new Processo(rs.getInt("id")));
                    } else {
                        JOptionPane.showMessageDialog(this, "Processo não encontrado: " + numProc, "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao validar processo:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
       
        mandado.setOficial(new Oficial(getIdSelecionado(cmbOficial)));
        
        int idOficial = getIdSelecionado(cmbOficial);
        mandado.setOficial(idOficial == -1 ? null : new Oficial(idOficial));
        
        MandadoDAO dao = new MandadoDAO();
        if (dao.atualizar(mandado)) {
            JOptionPane.showMessageDialog(this, "Mandado alterado com sucesso!");
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao alterar mandado.");
        }
    }
    
     private int getIdSelecionado(JComboBox<String> combo) {
        String selecionado = (String) combo.getSelectedItem();
        if (selecionado != null && selecionado.contains(" - ")) {
            try {
                return Integer.parseInt(selecionado.split(" - ")[0]);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }
     
    private void selecionarComboPorId(JComboBox<String> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if (item.startsWith(id + " -")) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.setSelectedIndex(0);
    }

    private String formatarData(Date date) {
        return (date != null) ? sdf.format(date) : "";
    }

    private Date parseData(String text) throws ParseException {
        java.util.Date utilDate = sdf.parse(text);
        return new Date(utilDate.getTime());
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
        btnSalvarAlteracoes = new javax.swing.JButton();
        btnCancelarMandado = new javax.swing.JButton();
        txtNumeroProcesso = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();
        txtNumeroMandadoBuscado = new javax.swing.JTextField();
        btnLimpar = new javax.swing.JButton();

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

        btnSalvarAlteracoes.setBackground(new java.awt.Color(0, 102, 51));
        btnSalvarAlteracoes.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSalvarAlteracoes.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvarAlteracoes.setText("Salvar");
        btnSalvarAlteracoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarAlteracoesActionPerformed(evt);
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(txtNumeroProcesso))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelCadastroMandadosLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(btnCancelarMandado, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(btnSalvarAlteracoes, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtNumeroProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(painelCadastroMandadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSalvarAlteracoes, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(btnCancelarMandado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Digite o mandado que deseja alterar:");

        btnBuscar.setBackground(new java.awt.Color(0, 102, 51));
        btnBuscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        txtNumeroMandadoBuscado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 51)));

        btnLimpar.setBackground(new java.awt.Color(0, 102, 51));
        btnLimpar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(painelCadastroMandados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(jLabel8)
                        .addGap(0, 105, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(txtNumeroMandadoBuscado, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNumeroMandadoBuscado, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(painelCadastroMandados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNumeroMandadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroMandadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroMandadoActionPerformed

    private void cmbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbStatusActionPerformed

    private void btnSalvarAlteracoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarAlteracoesActionPerformed
        try {
            // TODO add your handling code here:
            salvarAlteracoes();
        } catch (ParseException ex) {
            Logger.getLogger(TelaAlterarMandado.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSalvarAlteracoesActionPerformed

    private void btnCancelarMandadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarMandadoActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarMandadoActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        buscarMandado();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        txtNumeroMandadoBuscado.setText("");
        txtNumeroMandado.setText("");
        txtDataEmissao.setText("");
        txtPrazoCumprimento.setText("");
        txtNumeroProcesso.setText("");
        txtAnotacoes.setText("");
        cmbStatus.setSelectedIndex(0);
        cmbOficial.setSelectedIndex(0);
        popupMandados.setVisible(false);
    }//GEN-LAST:event_btnLimparActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelarMandado;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnSalvarAlteracoes;
    private javax.swing.JComboBox<String> cmbOficial;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel painelCadastroMandados;
    private javax.swing.JTextArea txtAnotacoes;
    private javax.swing.JFormattedTextField txtDataEmissao;
    private javax.swing.JTextField txtNumeroMandado;
    private javax.swing.JTextField txtNumeroMandadoBuscado;
    private javax.swing.JTextField txtNumeroProcesso;
    private javax.swing.JFormattedTextField txtPrazoCumprimento;
    // End of variables declaration//GEN-END:variables

}
