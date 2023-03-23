package org.lessons.java.nations;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private final static String URL = System.getenv("DB_URL");
    private final static String USER = System.getenv("DB_USER");
    private final static String PASSWORD = System.getenv("DB_PASSWORD");
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(URL,USER,PASSWORD)){

            String query = """
                        select c.name , c.country_id , r.name as region_name , co.name as continent_name
                        from countries c
                        join regions r on r.region_id = c.region_id
                        join continents co on co.continent_id = r.continent_id
                        where c.name like ?
                        order by c.name;
                    """;
            System.out.print("Search nation name (or part of its): ");
            String searchedWord = scan.nextLine();

            try (PreparedStatement ps = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){

                ps.setString(1, "%" + searchedWord + "%");

                try (ResultSet rs = ps.executeQuery()){

                    if (!rs.next()) {
                        System.out.println("No results with this search.");
                    } else {
                        rs.beforeFirst();
                    }

                    while (rs.next()) {
                        String nationName = rs.getString("name");
                        int nationId = rs.getInt("country_id");
                        String regionName = rs.getString("region_name");
                        String continentName = rs.getString("continent_name");
                        // System.out.println(nationName + " " + nationId + " " + regionName + " " + continentName);
                        System.out.printf("|%50s|", nationName);
                        System.out.printf("%6s|", nationId);
                        System.out.printf("%30s|", regionName);
                        System.out.printf("%15s|\n", continentName);
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
