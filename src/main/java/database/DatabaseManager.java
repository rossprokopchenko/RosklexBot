package database;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DatabaseManager {
    private static String url;

    public DatabaseManager(String resourcePath, String table) {
        if (fileExists(resourcePath, table)) {
            log.info("Successfully loaded database file!");

            this.url = "jdbc:sqlite:" + resourcePath + "/" + table + ".db";
            selectAll(table, 5);
        } else {
            log.error("Could not find database file :(");
        }
    }

    private boolean fileExists(String resourcePath, String table) {
        File f = new File(resourcePath + "/" + table + ".db");

        log.info("Attempting to read file: {}", f.toPath());

        if (f.isFile() && !f.isDirectory()) return true;

        return false;
    }

    public void recover(){

        dropTable("scores");
        createNewTable("scores");

        addColumn("exp", "0");
        addColumn("coins", "0");
        addColumn("lastDaily", "0");
        addColumn("pickaxe", "0");
        addColumn("stick", "0");
        addColumn("hammer", "0");
        addColumn("dagger", "0");
        addColumn("gun", "0");
        addColumn("attack", "0");
        addColumn("defence", "0");
        addColumn("swiftness", "0");
        addColumn("dTime", "0");
        addColumn("dDiff", "0");
        addColumn("dNotifier", "0");
        addColumn("dCounter", "0");
        addColumn("gear", "0");
        addColumn("wrench", "0");
        addColumn("rustyKey", "0");
        addColumn("battery", "0");
        addColumn("moneyBag", "0");
        addColumn("diamond", "0");
        addColumn("mKnife", "0");
        addColumn("legAmulet","0");
        addColumn("amuletMult", "1");
        addColumn("orb", "0");
        addColumn("shield", "0");
        addColumn("boots", "0");
        addColumn("mShield", "0");
        addColumn("hourglass", "0");
        addColumn("rank", "0");

        //System.out.println(getTop("level").toString());

    }

    /**
     * Connect to a sample database
     * @return
     */
    private Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            log.error("Error while connecting to url \"{}\"", url, e);
        }
        return conn;
    }

    public void newUser(String id, String name) {
        // SQLite connection string
        String sql = "INSERT INTO scores (" + tableColumns("scores") + ") VALUES(" + tableQuestions("scores") + ")";

        String[] columns = tableColumns("scores").split(",");


        try (Connection conn = DriverManager.getConnection(url);
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
        } catch (SQLException e) {
            log.error("Error while registering new user \"{}\" with id \"{}\"", name, id, e);
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
                + "    name text NOT NULL,\n"
                + "    level integer\n"
                + "); ", table);

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            log.info("Successfully created table.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addColumn(String column, String value) {
        // SQLite connection string
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE scores ADD COLUMN " + column + " text DEFAULT " + value + ";");
            //stmt.execute("ALTER TABLE scores RENAME TO TempOldTable;");
            //stmt.execute("CREATE TABLE scores (" + tableColumns("TempOldTable") + ", " + column + " text DEFAULT " + value + ");");
            //stmt.execute("INSERT INTO scores ("+ tableColumns("TempOldTable") +") SELECT * FROM TempOldTable;");
            //stmt.execute("DROP TABLE TempOldTable;");

            log.info("Successfully added column \"{}\" with value \"{}\"", column, value);
        } catch (SQLException e) {
            log.error("Error while adding column \"{}\" with value \"{}\"", column, value, e);
        }
    }

    public String[] getTop(String column, String order){
        String sql = "SELECT id, " + column + " FROM scores ORDER BY " + column + " " + order + " LIMIT 25";

        Map<String, String> top = new HashMap<>();

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                top.put(rs.getString("id"), rs.getString(column));
            }
        } catch (SQLException e) {
            log.error("Error while getting top 25.", e);
        }

        return top.keySet().toArray(new String[top.size()]);
    }

    private String tableColumns(String table){
        String sql = "SELECT * FROM " + table + ";";
        String columns = "";

        try (Connection conn = DriverManager.getConnection(url);
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

        try (Connection conn = DriverManager.getConnection(url);
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

        try (Connection conn = DriverManager.getConnection(url);
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

        try (Connection conn = this.connect();
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

    public String getColumn(String id, String column){

        String sql = "SELECT " + column + " FROM scores WHERE id = ?";

        String value = "";

        try (Connection conn = this.connect();
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

    public int getRows(){
        String sql = "SELECT id FROM scores";

        int counter = 0;

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                counter++;
            }
        } catch (SQLException e) {
            log.error("Error while getting all rows.", e);
        }

        return counter;
    }

    public void setColumn(String id, String column, String value){
        String sql = "UPDATE scores SET " + column + " = '" + value + "' WHERE id = " + id + ";";

        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            log.error("Error while setting id \"{}\" column \"{}\" value \"{}\".", id, column, value, e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM scores WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, id);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("Error while deleting id \"{}\".", id, e);
        }
    }

    public boolean exists(String id){
        String sql = "SELECT id "
                + "FROM scores WHERE id = ?";

        String idCol = "";

        try (Connection conn = this.connect();
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