package org.lessons.java.nations;

import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

            String query2 = """
                        select c.name as country_name
                        from countries c
                        where c.country_id = ? ;
                    """;

            String query3 = """
                        select l.`language` as language
                        from countries c
                        join country_languages cl on cl.country_id = c.country_id
                        join languages l on l.language_id = cl.language_id
                        where c.country_id = ?;
                    """;
            System.out.print("Scrivi l'id del paese di cui desideri ricevere info (es. id 104): ");
            int choice = Integer.parseInt(scan.nextLine());

            try (PreparedStatement ps = con.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){

                ps.setInt(1, choice);

                try (ResultSet rs = ps.executeQuery()){

                    if (!rs.next()) {
                        System.out.println("Nessun paese corrispondente.");
                    } else {
                        rs.beforeFirst();
                    }
                    while (rs.next()) {
                        String countrySelected = rs.getString("country_name");
                        System.out.println("Informazioni su: " + countrySelected + ".");
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(query3, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                ps.setInt(1, choice);

                try (ResultSet rs = ps.executeQuery()){
                    if (!rs.next()) {
                        System.out.println("Nessuna lingua presente.");
                    } else {
                        rs.beforeFirst();
                        System.out.print("Lingue presenti: ");
                    }

                    while (rs.next()) {
                        String language = rs.getString("language");
                        if (!rs.next()) {
                            System.out.print(language + ".");
                        } else {
                            System.out.print(language + ", ");
                        }

                    }
                }
            }

            String quary4 = """
                        select cs.country_id, cs.`year` as anno, c.name , cs.population as population, cs.gdp as gross_domestic_product
                        from countries c
                        join country_stats cs on c.country_id = cs.country_id
                        where cs.country_id = ?
                        order by cs.`year` desc
                        limit 1;
                    """;

            try (PreparedStatement ps = con.prepareStatement(quary4, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                ps.setInt(1, choice);
                try (ResultSet rs = ps.executeQuery()){
                    if (!rs.next()) {
                        System.out.println("Nessuna statistica presente.");
                    } else {
                        rs.beforeFirst();
                    }
                    while (rs.next()) {
                        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
                        int year = rs.getInt("anno");
                        long population = rs.getLong("population");
                        long gdp = rs.getLong("gross_domestic_product");
                        System.out.println("\nStatistiche dell'anno: " + year);
                        System.out.println("Popolazione: " + population);
                        System.out.println("Prodotto interno lordo: " + gdp);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        scan.close();
    }
}
