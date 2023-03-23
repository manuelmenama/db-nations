package org.lessons.java.nations;

import java.sql.*;

public class Main {
    private final static String URL = System.getenv("DB_URL");
    private final static String USER = System.getenv("DB_USER");
    private final static String PASSWORD = System.getenv("DB_PASSWORD");
    public static void main(String[] args) {

        try (Connection con = DriverManager.getConnection(URL,USER,PASSWORD)){

            String query = """
                        select c.name , c.country_id , r.name as region_name , co.name as continent_name
                        from countries c
                        join regions r on r.region_id = c.region_id
                        join continents co on co.continent_id = r.continent_id
                        order by c.name;
                    """;
            try (PreparedStatement ps = con.prepareStatement(query)){

                try (ResultSet rs = ps.executeQuery()){

                    while (rs.next()) {
                        String nationName = rs.getString("name");
                        int nationId = rs.getInt("country_id");
                        String regionName = rs.getString("region_name");
                        String continentName = rs.getString("continent_name");
                        System.out.println(nationName + " " + nationId + " " + regionName + " " + continentName);

                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
