package org.example;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

class Collection {
    private String name;
    private String ownersName;
    private List<MusicalMedium> media = new ArrayList<>();


    public Collection(String name, String ownersName) {
        this.name = name;
        this.ownersName = ownersName;
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Collection (name, owner_name) VALUES (?, ?)")) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, ownersName);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMedia(MusicalMedium medium) {
        media.add(medium);
        medium.setCollection(this);
    }

    public String getName() {
        return name;
    }

    public String getOwnersName() {
        return ownersName;
    }

    public List<MusicalMedium> getMedia() {
        return media;
    }

    public MusicWork findWorkByTitle(String title) {
        for (MusicalMedium medium : media) {
            for (MusicWork work : medium.getMusicalWorks()) {
                if (work.getName().equals(title)) {
                    return work;
                }
            }
        }
        return null;
    }
}
