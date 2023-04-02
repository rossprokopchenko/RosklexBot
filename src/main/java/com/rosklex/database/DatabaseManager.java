package com.rosklex.database;

import com.rosklex.boot.Rosklex;
import lombok.extern.slf4j.Slf4j;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DatabaseManager {
    private static Map<String, String> urlMap;

    public DatabaseManager(String resourcePath, ArrayList<String> filenames) {
        urlMap = new HashMap<>();

        for (String filename : filenames) {
            if (!fileExists(resourcePath, filename)) {
                log.error("Could not find database file :(");
            }
            String url = "jdbc:sqlite:" + resourcePath + "/" + filename + ".db";

            log.info("Loaded {} file for url {}.", filename, url);
            urlMap.put(filename, url);

        }

        //dropTable(DatabaseTypes.SERVERS_DB_NAME);
        //createNewServersDB();

        selectAll(DatabaseTypes.SCORES_DB_NAME, 5);
    }

    private boolean fileExists(String resourcePath, String table) {
        File f = new File(resourcePath + "/" + table + ".db");

        log.info("Attempting to read file: {}", f.toPath());

        if (f.isFile() && !f.isDirectory()) return true;

        return false;
    }

    public void recoverScores(){
        /*
        dropTable("scores");
        createNewTable("scores");

        addColumn("exp", "0", "scores");
        addColumn("coins", "0", "scores");
        addColumn("lastDaily", "0", "scores");
        addColumn("pickaxe", "0", "scores");
        addColumn("stick", "0", "scores");
        addColumn("hammer", "0", "scores");
        addColumn("dagger", "0", "scores");
        addColumn("gun", "0", "scores");
        addColumn("attack", "0", "scores");
        addColumn("defence", "0", "scores");
        addColumn("swiftness", "0", "scores");
        addColumn("dTime", "0", "scores");
        addColumn("dDiff", "0", "scores");
        addColumn("dNotifier", "0", "scores");
        addColumn("dCounter", "0", "scores");
        addColumn("gear", "0", "scores");
        addColumn("wrench", "0", "scores");
        addColumn("rustyKey", "0", "scores");
        addColumn("battery", "0", "scores");
        addColumn("moneyBag", "0", "scores");
        addColumn("diamond", "0", "scores");
        addColumn("mKnife", "0", "scores");
        addColumn("legAmulet","0", "scores");
        addColumn("amuletMult", "1", "scores");
        addColumn("orb", "0", "scores");
        addColumn("shield", "0", "scores");
        addColumn("boots", "0", "scores");
        addColumn("mShield", "0", "scores");
        addColumn("hourglass", "0", "scores");
        addColumn("rank", "0", "scores");
        */
        //System.out.println(getTop("level").toString());
    }

    private void createNewServersDB() {
        if (tableExists(DatabaseTypes.SERVERS_DB_NAME)) {
            log.info("Tried to create new servers database table, one already exists.");
            return;
        }

        createNewTable(DatabaseTypes.SERVERS_DB_NAME);

        addColumn("requests", "0", DatabaseTypes.SERVERS_DB_NAME);
        addColumn("users", "0", DatabaseTypes.SERVERS_DB_NAME);
        addColumn("prefix", "" + Rosklex.getPrefix(), DatabaseTypes.SERVERS_DB_NAME);
    }

    /**
     * Connect to a sample database
     * @return
     */
    private Connection connect(String databaseURL) {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException e) {
            log.error("Error while connecting to url \"{}\"", databaseURL, e);
        }
        return conn;
    }

    public boolean tableExists(String tableName){
        try {
            String tableExists = String.format("SELECT name FROM sqlite_master WHERE type='table' AND name='%s';", tableName);
            Statement statement = connect(urlMap.get(tableName)).createStatement();
            ResultSet ex = statement.executeQuery(tableExists);
            String exS = ex.getString(1);
            log.info("Table " + exS + " exists.");

            statement.close();
            ex.close();
            return true;
        }catch (SQLException e){
            log.info("Table {} does not exist.", tableName);
            return false;
        }
    }

    public void newUser(String id, String name) {
        // SQLite connection string
        String sql = "INSERT INTO " + DatabaseTypes.SCORES_DB_NAME + " (" + tableColumns(DatabaseTypes.SCORES_DB_NAME) + ") VALUES(" + tableQuestions(DatabaseTypes.SCORES_DB_NAME) + ")";

        String[] columns = tableColumns(DatabaseTypes.SCORES_DB_NAME).split(",");


        try (Connection conn = DriverManager.getConnection(urlMap.get(DatabaseTypes.SCORES_DB_NAME));
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, name);

            pstmt.setInt(3, 1);

            // defaults everything past 3rd column to 0

            for (int i = 4; i <= columns.length; i++) {
                pstmt.setInt(i, 0);
            }

            // amulet multiplier set to 1
            pstmt.setString(27, "1");
            pstmt.executeUpdate();

            log.info("Registered new user entry in database! ID: {} name: {}", id, name);
        } catch (SQLException e) {
            log.error("Error while registering new user \"{}\" with id \"{}\"", name, id, e);
        }
    }

    public void newServer(String id, String name) {
        // SQLite connection string

        String columns = "id, name";
        String values = String.format("\'%s\', \'%s\'", id, name);

        String sql = String.format("INSERT INTO %s(%s) VALUES(%s)", DatabaseTypes.SERVERS_DB_NAME, columns, values);

        try (Connection conn = DriverManager.getConnection(urlMap.get(DatabaseTypes.SERVERS_DB_NAME));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.executeUpdate();
            log.info("Registered new server entry in database! ID: {} name: {}", id, name);
        } catch (SQLException e) {
            log.error("Error while registering new server entry \"{}\" with id \"{}\"", name, id, e);
        }
    }

    /**
     * Create a new table in the test database
     *
     */
    public static void createNewTable(String table) {
        // SQL statement for creating a new table
        String sql = String.format("CREATE TABLE IF NOT EXISTS %s (\n"
                + "    id text PRIMARY KEY,\n"
                + "    name text NOT NULL\n"
                + "); ", table);

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            log.info("Successfully created table {}.", table);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addColumn(String column, String value, String table) {
        // SQLite connection string
        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
            Statement stmt = conn.createStatement()) {

            String command = String.format("ALTER TABLE %s ADD COLUMN %s text DEFAULT \"%s\";", table, column, value);

            stmt.execute(command);

            log.info("Successfully added column \"{}\" with value \"{}\" to table {}", column, value, table);

        } catch (SQLException e) {
            log.error("Error while adding column \"{}\" with value \"{}\" to table {}", column, value, e, table);
        }
    }

    public String[] getTop(String column, String order, String table){
        String sql = String.format("SELECT id, %s FROM %s ORDER BY %s %s LIMIT 25", column, table, column, order);
        Statement stmt = null;

        Map<String, String> top = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(urlMap.get(table))){

            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                top.put(rs.getString("id"), rs.getString(column));
            }

            stmt.close();
        } catch (SQLException e) {
            log.error("Error while getting top 25.", e);
        }

        return top.keySet().toArray(new String[top.size()]);
    }

    private String tableColumns(String table){
        String sql = "SELECT * FROM " + table + ";";
        String columns = "";

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
             Statement stmt = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // The column count starts from 1
            for (int i = 1; i <= columnCount; i++) {

                if(i == columnCount){
                    columns += rsmd.getColumnName(i);
                } else {
                    columns += rsmd.getColumnName(i) + ",";
                }
            }
        } catch (SQLException e){
            log.error("Error while getting table columns.", e);
        }

        return columns;
    }

    private String tableQuestions(String table){
        String sql = "SELECT * FROM " + table + ";";
        String questions = "";

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
             Statement stmt = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // The column count starts from 1
            for (int i = 1; i <= columnCount; i++) {

                if(i == columnCount){
                    questions += "?";
                } else {
                    questions += "?,";
                }
            }
        } catch (SQLException e){
            log.error("", e);
        }

        return questions;
    }

    public static void dropTable(String table){

        // SQL statement for creating a new table
        String sql = String.format("DROP TABLE %s;", table);
        String sql2 = "DROP TABLE TempOldTable;";

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
             Statement stmt = conn.createStatement()) {
            // drop table
            stmt.execute(sql);
            System.out.println("Dropped table");
        } catch (SQLException e) {
            log.error("Error while dropping table", e);
        }
    }

    public void selectAll(String table, int cols){
        String sql = String.format("SELECT * FROM %s", table);

        String s = tableColumns(table);
        String[] columnArray = s.split(",");
        //int numColumns = columnArray.length;
        int numColumns = cols;

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            log.info("Printing all values found in database:");
            String columns = "";

            for(int i = 0; i < numColumns; i++) {

                if (i == numColumns - 1)
                    columns += columnArray[i];
                else
                    columns += columnArray[i] + "\t";
            }

            log.info("{}", columns);

            // loop through the result set
            while (rs.next()) {
                String row = "";

                for(int i = 0; i < numColumns; i++){
                    row += rs.getString(columnArray[i]) + "\t";
                }

                log.info("{}", row);
            }
        } catch (SQLException e) {
            log.error("Error while selecting all.", e);
        }
    }

    public String getColumn(String id, String column, String table){

        String sql = String.format("SELECT %s FROM %s WHERE id = ?", column, table);

        String value = "";

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            pstmt.setString(1,id);

            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                value += rs.getString(column);
            }
        } catch (SQLException e) {
            log.error("Error while getting column \"{}\" where id \"{}\"", column, id, e);
        }

        return value;
    }

    public int getRows(String table) throws SQLiteException {
        String sql = String.format("SELECT id FROM %s", table);

        int counter = 0;

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                counter++;
            }
        } catch (SQLException e) {
            log.error("Error while getting all rows.", e);

            throw new SQLiteException("", SQLiteErrorCode.SQLITE_ERROR);
        }

        return counter;
    }

    public void setColumn(String id, String column, String value, String table){
        String sql = String.format("UPDATE %s SET %s = '%s' WHERE id = %s;", table, column, value, id);

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);

        } catch (SQLException e) {
            log.error("Error while setting id \"{}\" column \"{}\" value \"{}\".", id, column, value, e);
        }
    }

    public void delete(String id, String table) {
        String sql = String.format("DELETE FROM %s WHERE id = ?", table);

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, id);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("Error while deleting id \"{}\".", id, e);
        }
    }

    public boolean exists(String id, String table){
        String sql = String.format("SELECT id FROM %s WHERE id = ?", table);

        String idCol = "";

        try (Connection conn = DriverManager.getConnection(urlMap.get(table));
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            pstmt.setString(1,id);

            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                idCol = rs.getString("id");
            }
        } catch (SQLException e) {
            log.error("Error while checking for id \"{}\" existence.", id, e);
        }

        if(idCol.equals(id))
            return true;
        else return false;
    }

}