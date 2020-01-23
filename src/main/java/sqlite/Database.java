package sqlite;

import java.sql.*;

public class Database {

    private static Database db = new Database();

    public void run(){
        //Database.getDb().dropTable();
        //Database.getDb().createNewTable();

        //Database.getDb().addColumn("exp", "0");
        //Database.getDb().addColumn("coins", "0");
        //Database.getDb().addColumn("lastDaily", "0");
        //Database.getDb().addColumn("pickaxe", "0");
        //Database.getDb().addColumn("stick", "0");
        //Database.getDb().addColumn("hammer", "0");
        //Database.getDb().addColumn("dagger", "0");
        //Database.getDb().addColumn("gun", "0");
        //Database.getDb().addColumn("attack", "0");
        //Database.getDb().addColumn("defence", "0");
        //Database.getDb().addColumn("swiftness", "0");
        //Database.getDb().addColumn("dTime", "0");
        //Database.getDb().addColumn("dDiff", "0");
        //Database.getDb().addColumn("dNotifier", "0");
        //Database.getDb().addColumn("dCounter", "0");
        //Database.getDb().addColumn("gear", "0");
        //Database.getDb().addColumn("wrench", "0");
        //Database.getDb().addColumn("rustyKey", "0");
        //Database.getDb().addColumn("battery", "0");
        //Database.getDb().addColumn("moneyBag", "0");
        //Database.getDb().addColumn("diamond", "0");
        //Database.getDb().addColumn("mKnife", "0");
        //Database.getDb().addColumn("legAmulet","0");
        //Database.getDb().addColumn("amuletMult", "1");
        //Database.getDb().addColumn("orb", "0");
        //Database.getDb().addColumn("shield", "0");
        //Database.getDb().addColumn("boots", "0");
        //Database.getDb().addColumn("mShield", "0");
        //Database.getDb().addColumn("hourglass", "0");
        //System.out.println(Database.getDb().tableColumns("tempOldTable"));
        //System.out.println(Database.getDb().tableColumns("scores"));

        // REMEMBER TO MODIFY newUser() to set default values
        //Database.getDb().delete("499752045138411531");
        Database.getDb().selectAll();
    }

    public static Database getDb(){
        return db;
    }

    /**
     * Connect to a sample database
     * @return
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:scores.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void newUser(String id, String name){
        // SQLite connection string
        String url = "jdbc:sqlite:scores.db";
        String sql = "INSERT INTO scores ("+ tableColumns("scores") +") VALUES(" + tableQuestions("scores") + ")";

        String[] columns = tableColumns("scores").split(",");



        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, name);

            pstmt.setInt(3, 1);

            // defaults everything past 3rd column to 0

            for(int i = 4; i <= columns.length; i++){
                pstmt.setInt(i, 0);
            }

            // amulet multiplier set to 1
            pstmt.setString(27, "1");

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Create a new table in the test database
     *
     */
    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:scores.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS scores (\n"
                + "    id text PRIMARY KEY,\n"
                + "    name text NOT NULL,\n"
                + "    level integer\n"
                + "); ";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("Created table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addColumn(String column, String value) {
        // SQLite connection string
        String url = "jdbc:sqlite:scores.db";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE scores ADD COLUMN " + column + " text DEFAULT " + value + ";");
            //stmt.execute("ALTER TABLE scores RENAME TO TempOldTable;");
            //stmt.execute("CREATE TABLE scores (" + tableColumns("TempOldTable") + ", " + column + " text DEFAULT " + value + ");");
            //stmt.execute("INSERT INTO scores ("+ tableColumns("TempOldTable") +") SELECT * FROM TempOldTable;");
            //stmt.execute("DROP TABLE TempOldTable;");

            System.out.println("added column");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private String tableColumns(String table){
        String url = "jdbc:sqlite:scores.db";
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
            System.out.println(e.getMessage());
        }

        return columns;
    }

    private String tableQuestions(String table){
        String url = "jdbc:sqlite:scores.db";
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
            System.out.println(e.getMessage());
        }

        return questions;
    }

    public static void dropTable(){
        // SQLite connection string
        String url = "jdbc:sqlite:scores.db";

        // SQL statement for creating a new table
        String sql = "DROP TABLE scores;";
        String sql2 = "DROP TABLE TempOldTable;";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // drop table
            stmt.execute(sql);
            System.out.println("Dropped table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectAll(){
        String sql = "SELECT * FROM scores";

        String s = tableColumns("scores");
        String[] columnArray = s.split(",");

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                for(int i = 0; i < columnArray.length; i++){
                    if(i == columnArray.length - 1)
                        System.out.print(rs.getString(columnArray[i]) + "\n");
                     else
                        System.out.print(rs.getString(columnArray[i]) + "\t");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }

        return value;
    }

    public void setColumn(String id, String column, String value){
        String url = "jdbc:sqlite:scores.db";
        String sql = "UPDATE scores SET " + column + " = '" + value + "' WHERE id = " + id + ";";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }

        if(idCol.equals(id))
            return true;
        else return false;
    }

}