package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Collection Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(3, 2));

        JLabel collectionNameLabel = new JLabel("Collection Name:");
        JTextField collectionNameField = new JTextField();
        JLabel ownerNameLabel = new JLabel("Owner's Name:");
        JTextField ownerNameField = new JTextField();
        JButton submitButton = new JButton("Submit");

        frame.add(collectionNameLabel);
        frame.add(collectionNameField);
        frame.add(ownerNameLabel);
        frame.add(ownerNameField);
        frame.add(new JLabel());
        frame.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().removeAll();
                String collectionName = collectionNameField.getText();
                String ownerName = ownerNameField.getText();
                Collection collection = new Collection(collectionName, ownerName);
                JLabel label = new JLabel("Welcome to Collection Management System");
                JButton addButton = new JButton("Add Album");
                JButton showButton = new JButton("Show Collection");
                JButton updateButton = new JButton("Update Music Work");
                JButton deleteButton = new JButton("Delete Music Work");
                JButton exitButton = new JButton("Exit");
                JPanel panel = new JPanel(new GridLayout(7, 1));
                panel.add(label);
                panel.add(addButton);
                panel.add(showButton);
                panel.add(updateButton);
                panel.add(deleteButton);
                panel.add(exitButton);
                frame.add(panel);
                addButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JPanel addPanel = new JPanel(new GridLayout(4, 2));
                        addPanel.add(new JLabel("Author:"));
                        JTextField authorField = new JTextField();
                        addPanel.add(authorField);
                        addPanel.add(new JLabel("Genre:"));
                        JTextField genreField = new JTextField();
                        addPanel.add(genreField);
                        addPanel.add(new JLabel("Year of Manufacture:"));
                        JTextField yearField = new JTextField();
                        addPanel.add(yearField);
                        JButton addButton = new JButton("Add Album");
                        addButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                MusicalMedium newAlbum = new MusicalMedium(authorField.getText(), genreField.getText(), Integer.parseInt(yearField.getText()), collection);
                                collection.addMedia(newAlbum);
                                JOptionPane.showMessageDialog(frame, "Album added successfully");
                                newAlbum.add();
                                frame.getContentPane().remove(addPanel);
                                frame.repaint();
                                JPanel addPanel = new JPanel(new GridLayout(3, 2));
                                addPanel.add(new JLabel("Name:"));
                                JTextField NameField = new JTextField();
                                addPanel.add(NameField);
                                addPanel.add(new JLabel("Duration:"));
                                JTextField durationField = new JTextField();
                                addPanel.add(durationField);
                                JButton addButton = new JButton("Add Music");
                                addButton.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        newAlbum.addMusicalWork(new MusicWork(NameField.getText(), Integer.parseInt(durationField.getText()), newAlbum));
                                        JOptionPane.showMessageDialog(frame, "Work added successfully");

                                        NameField.setText("");
                                        durationField.setText("");
                                    }
                                });
                                addPanel.add(addButton);
                                frame.add(addPanel);
                                frame.revalidate();
                            }
                        });
                        addPanel.add(addButton);
                        frame.add(addPanel);
                        frame.revalidate();
                    }
                });
                showButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String info = "Коллекция: " + collection.getName() + "\n";
                        info += "Владелец: " + collection.getOwnersName() + "\n\n";

                        for (MusicalMedium medium : collection.getMedia()) {
                            info += "Альбом: " + medium.getAuthorGroup() + "\n";
                            info += "Жанр: " + medium.getGenre() + "\n";
                            info += "Год выпуска: " + medium.getYearOfManufacture() + "\n";
                            info += "Песни в альбоме:\n";

                            for (MusicWork work : medium.getMusicalWorks()) {
                                info += " - " + work.getName() + " (продолжительность: " + work.getDuration() + " секунд)\n";
                            }
                            info += "Общая продолжительность альбома: " + medium.getTotalDurationOfSound() + " секунд\n\n";
                        }

                        JTextArea textArea = new JTextArea(info);
                        textArea.setEditable(false);

                        JScrollPane scrollPane = new JScrollPane(textArea);

                        JOptionPane.showMessageDialog(frame, scrollPane, "Collection Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                updateButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String musicWorkName = JOptionPane.showInputDialog(frame, "Enter the name of the music work to update:");
                        MusicWork updatedWork = collection.findWorkByTitle(musicWorkName);
                        if (updatedWork != null) {
                            String updatedDurationString = JOptionPane.showInputDialog(frame, "Enter the updated duration for the music work '" + musicWorkName + "':");
                            try {
                                int updatedDuration = Integer.parseInt(updatedDurationString);
                                updatedWork.update(updatedDuration);
                                JOptionPane.showMessageDialog(frame, "Music work '" + musicWorkName + "' updated successfully.");
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid integer for the duration.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Music work '" + musicWorkName + "' not found.");
                        }
                    }
                });
                deleteButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String musicWorkName = JOptionPane.showInputDialog(frame, "Enter the name of the music work to delete:");
                        MusicWork deletedWork = collection.findWorkByTitle(musicWorkName);
                        if (deletedWork != null) {
                            int confirmDialogResult = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this music work?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                            if (confirmDialogResult == JOptionPane.YES_OPTION) {
                                try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password")) {
                                    String sql = "DELETE FROM MusicalComposition WHERE name = ?";
                                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                                        preparedStatement.setString(1, musicWorkName);
                                        preparedStatement.executeUpdate();
                                        JOptionPane.showMessageDialog(frame, "Music work deleted successfully.");
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(frame, "Error deleting music work: " + ex.getMessage());
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Music work '" + musicWorkName + "' not found.");
                        }


                    }
                });
                deleteButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String musicWorkName = JOptionPane.showInputDialog(frame, "Enter the name of the music work to delete:");
                        MusicWork deletedWork = collection.findWorkByTitle(musicWorkName);
                        if (deletedWork != null) {
                            int confirmDialogResult = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this music work?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                            if (confirmDialogResult == JOptionPane.YES_OPTION) {
                                deletedWork.delete();
                                JOptionPane.showMessageDialog(frame, "Music work deleted successfully.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Music work '" + musicWorkName + "' not found.");
                        }
                    }
                });
                exitButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int option = JOptionPane.showConfirmDialog(frame, "Are you sure?", "Exit", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    }
                });
                frame.revalidate();
                frame.repaint();
            }
        });
        frame.setVisible(true);
    }
}