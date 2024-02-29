package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class MusicalMedium {
    private String authorGroup;
    private String genre;
    private int yearOfManufacture;
    private List<MusicWork> musicalWorks = new ArrayList<>();
    private Collection collection;

    public MusicalMedium(String authorGroup, String genre, int yearOfManufacture, Collection collection) {
        this.authorGroup = authorGroup;
        this.genre = genre;
        this.yearOfManufacture = yearOfManufacture;
        this.collection = collection;
    }

    public void addMusicalWork(MusicWork work) {
        musicalWorks.add(work);
        work.setMusicalMedia(this);
    }

    public String getAuthorGroup() {
        return authorGroup;
    }

    public String getGenre() {
        return genre;
    }

    public int getYearOfManufacture() {
        return yearOfManufacture;
    }

    public List<MusicWork> getMusicalWorks() {
        return musicalWorks;
    }

    public int getTotalDurationOfSound() {
        int total = 0;
        for (MusicWork work : musicalWorks) {
            total += work.getDuration();
        }
        return total;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Collection getCollection() {
        return collection;
    }

    public void add() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234")) {
            String sql = "INSERT INTO MusicalMedium (collection_id, author_group, genre, year_of_manufacture, total_duration) " +
                    "SELECT collection_id, ?, ?, ?, ? " +
                    "FROM Collection WHERE name=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, this.authorGroup);
                preparedStatement.setString(2, this.genre);
                preparedStatement.setInt(3, this.yearOfManufacture);
                preparedStatement.setInt(4, getTotalDurationOfSound());
                preparedStatement.setString(5, collection.getName());

                preparedStatement.executeUpdate();
                System.out.println("Musical Medium added successfully.");
            } catch (SQLException e) {
                System.err.println("Error executing SQL statement: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }
}