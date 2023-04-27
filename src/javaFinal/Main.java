package javaFinal;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame { 
	
	JLabel lblCode, lblName, lblPrice, lblStock;
	JTextField txtCode, txtName, txtPrice, txtStock;
    JButton btnInsert, btnView, btnUpdate, btnDelete;
    DefaultTableModel model;

    Connection conn;
    PreparedStatement ps;
    ResultSet rs;

    public Main() {
        setTitle("PT. Pudding Menu Management");
        setSize(350, 150);
        setLocationRelativeTo(null);

        JMenuItem insertItem = new JMenuItem("Insert Menu");
        insertItem.addActionListener(new InsertListener());

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        lblCode = new JLabel(" Kode Menu (PD-XXX):");
        txtCode = new JTextField();
        lblName = new JLabel(" Nama Menu:");
        txtName = new JTextField();
        lblPrice = new JLabel(" Harga Menu:");
        txtPrice = new JTextField();
        lblStock = new JLabel(" Stok Menu:");
        txtStock = new JTextField();
        panel.add(lblCode);
        panel.add(txtCode);
        panel.add(lblName);
        panel.add(txtName);
        panel.add(lblPrice);
        panel.add(txtPrice);
        panel.add(lblStock);
        panel.add(txtStock);
        add(panel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(1, 3));
        btnInsert = new JButton("Insert");
        btnInsert.addActionListener(new InsertListener());
        btnView = new JButton("View");
        btnView.addActionListener(new ViewListener());
        btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new UpdateListener());
        btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new DeleteListener());
        btnPanel.add(btnInsert);
        btnPanel.add(btnView);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.CENTER);

        setVisible(true);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pt_pudding", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Main();
    }
    
    private class InsertListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String code = txtCode.getText();
            String name = txtName.getText();
            String priceStr = txtPrice.getText();
            String stockStr = txtStock.getText();
            
            if (!code.matches("PD-\\d{3}")) {
                JOptionPane.showMessageDialog(null, "Error: Ikuti format PD-XXX | XXX adalah integer bebas dan PD harus kapital.");
                return;
            }
            
            if(name == null) {
            	JOptionPane.showMessageDialog(null, "Error: Nama menu tidak boleh kosong.");
            	return;
            }
            
            int price, stock;
            try {
                price = Integer.parseInt(priceStr);
                stock = Integer.parseInt(stockStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error: Harga ataupun stok harus integer.");
                return;
            }
            
            try {
                ps = conn.prepareStatement("INSERT INTO menu (kode_menu, nama_menu, harga_menu, stok_menu) VALUES (?, ?, ?, ?)");
                ps.setString(1, code);
                ps.setString(2, name);
                ps.setInt(3, price);
                ps.setInt(4, stock);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Menu baru ditambahkan!");
                
                txtCode.setText("");
                txtName.setText("");
                txtPrice.setText("");
                txtStock.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Gagal menambah menu.");
            }
        }
    }

    private class ViewListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                ps = conn.prepareStatement("SELECT * FROM menu");
                rs = ps.executeQuery();
                
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "Error: Menu kosong.");
                    return;
                }
                
                model = new DefaultTableModel();
                model.addColumn("Kode Menu");
                model.addColumn("Nama Menu");
                model.addColumn("Harga Menu");
                model.addColumn("Stok Menu");
                
                do {
                    String code = rs.getString("kode_menu");
                    String name = rs.getString("nama_menu");
                    int price = rs.getInt("harga_menu");
                    int stock = rs.getInt("stok_menu");
                    model.addRow(new Object[] { code, name, price, stock });
                } while (rs.next());
                
                JTable table = new JTable(model);
                
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(400, 200));
                
                JOptionPane.showMessageDialog(null, scrollPane, "Daftar Menu", JOptionPane.PLAIN_MESSAGE);
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Gagal memuat daftar menu.");
            }
        }
    }

    private class UpdateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	try {
        		ps = conn.prepareStatement("SELECT COUNT(*) FROM menu");
                rs = ps.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                if (count == 0) {
                    JOptionPane.showMessageDialog(null, "Error: Menu kosong.");
                    return;
                }
        		
	            String code = JOptionPane.showInputDialog(null, "Masukkan kode menu yang ingin di update (PD-XXX):");
	            
	            if (code == null) {
	                return;
	            }
	            
	            if (!code.matches("PD-\\d{3}")) {
	                JOptionPane.showMessageDialog(null, "Error: Ikuti format PD-XXX | XXX adalah integer bebas dan PD harus kapital.");
	                return;
	            }
	            
	            ps = conn.prepareStatement("SELECT * FROM menu WHERE kode_menu=?");
	            ps.setString(1, code);
	            rs = ps.executeQuery();
	            
	            if (!rs.next()) {
	                JOptionPane.showMessageDialog(null, "Error: Menu tidak ditemukan.");
	            } else {
	                int price = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the new price of the menu:"));
	                int stock = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the new stock of the menu:"));
	                ps = conn.prepareStatement("UPDATE menu SET harga_menu=?, stok_menu=? WHERE kode_menu=?");
	                ps.setInt(1, price);
	                ps.setInt(2, stock);
	                ps.setString(3, code);
	                ps.executeUpdate();
	                JOptionPane.showMessageDialog(null, "Menu berhasil di update!");
	            }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Gagal update menu.");
            } catch (NumberFormatException ex) {
            	JOptionPane.showMessageDialog(null, "Error: Harga ataupun stok harus integer.");
            }
        }
    }
    
    private class DeleteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                ps = conn.prepareStatement("SELECT COUNT(*) FROM menu");
                rs = ps.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                if (count == 0) {
                    JOptionPane.showMessageDialog(null, "Error: Menu kosong.");
                    return;
                }
                
                String code = JOptionPane.showInputDialog(null, "Masukkan kode menu yang ingin di delete (PD-XXX:");
                if (code == null) {
                    return;
                }
                if (!code.matches("PD-\\d{3}")) {
                    JOptionPane.showMessageDialog(null, "Error: Ikuti format PD-XXX | XXX adalah integer bebas dan PD harus kapital.");
                    return;
                }
                
                ps = conn.prepareStatement("SELECT * FROM menu WHERE kode_menu=?");
                ps.setString(1, code);
                rs = ps.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "Error: Menu tidak ditemukan.");
                } else {
                    ps = conn.prepareStatement("DELETE FROM menu WHERE kode_menu=?");
                    ps.setString(1, code);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Menu berhasil di delete!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Gagal delete menu.");
            }
        }
    }


}
