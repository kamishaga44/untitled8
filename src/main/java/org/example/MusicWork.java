package org.example;
import java.sql.*;

class MusicWork {
    private String name;
    private int duration;
    private MusicalMedium musicalMedia;

    public MusicWork(String name, int duration, MusicalMedium musicalMedia) {
        this.name = name;
        this.duration = duration;
        this.musicalMedia = musicalMedia;
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234");
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO MusicalComposition (medium_id, name, duration) " +
                             "SELECT m.medium_id, ?, ? " +
                             "FROM MusicalMedium m INNER JOIN Collection c ON m.collection_id = c.collection_id " +
                             "WHERE m.author_group = ?")) {
            preparedStatement.setString(1, this.name);
            preparedStatement.setInt(2, this.duration);
            preparedStatement.setString(3, musicalMedia.getAuthorGroup());

            preparedStatement.executeUpdate();
            System.out.println("Music Work added successfully.");

        } catch (SQLException e) {
            System.err.println("Error executing SQL statement: " + e.getMessage());
        }

    }
    public void update(int newDuration) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234")) {
            String sql = "UPDATE MusicalComposition SET duration = ? WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, newDuration);
                preparedStatement.setString(2, getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error updating music work duration: " + ex.getMessage());
        }
    }


    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void setMusicalMedia(MusicalMedium musicalMedia) {
        this.musicalMedia = musicalMedia;
    }

    public MusicalMedium getMusicalMedia() {
        return musicalMedia;
    }

    public void delete() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234")) {
            String sql = "DELETE FROM MusicalComposition WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Error deleting music work: " + ex.getMessage());
        }
    }
}