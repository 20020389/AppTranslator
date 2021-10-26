package sample.App;

import sample.App.console.ConsoleApp;

import java.sql.*;
import java.util.LinkedList;

public class Database {
    public static Connection connection;
    private Statement state = null;
    private String url;
    public static String user;
    public static String pass;
    private String command;             // chọn bảng trên csdl
    private ResultSet database;              // lấy data của mảng
    private LinkedList<String[]> data;
    private int number_oj;                   //số cột trên data base
    private LinkedList<Integer> localChar;
    public static boolean Logined = false;
    public static LinkedList<String[]> dataLog = null;
    private static String dataName;
    public static int WORD = 0;
    public static int TYPE = 1;
    public static int SPEAK = 2;
    public static int TRANS = 3;
    public static int SAME = 4;

    private static Connection getConnection(String dbUrl, String user, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(dbUrl, user, password);
        connection.setAutoCommit(true);
        return connection;
    }


    public Database() {
        if (dataLog == null) {
            dataLog = new LinkedList<>();
        }
        try {
            url = "jdbc:mariadb://localhost:3306";
            user = "snow";
            pass = "dai";
            number_oj = 4;
            command = "select * from language order by word";           //lệnh
            if (!Logined) {
                connection = getConnection(url, user, pass);        // kết nối
            }
            state = connection.createStatement();             // biến thực thi lệnh trên csdl
            database = state.executeQuery(command);          //lệnh lấy bảng trên database
            data = new LinkedList<>();
            String num[];
            while (database != null && database.next()) {
                num = new String[number_oj];
                num[0] = database.getString("word").toLowerCase();
                num[1] = database.getString("type");
                num[2] = database.getString("speak");
                num[3] = database.getString("trans");
                data.addLast(num);
            }
            localChar = setLocaChar(data, 0);
            System.out.println();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public static void login(String user, String pass, String yourdataname, String yoururl) {
        if (dataLog == null) {
            dataLog = new LinkedList<>();
        }
        try {
            String url = "jdbc:mariadb://localhost:3306";
            String dataname = "src";
            if (yourdataname != null) {
                dataname = yourdataname;
            }
            if (dataname.isEmpty()) {
                dataname = "done";
            }
            if (yoururl != null)
                url = yoururl;
            connection = getConnection(url, user, pass);        // kết nối
            Statement trystate = connection.createStatement();
            String cmd = "use " + dataname;
            trystate.executeQuery(cmd);
            trystate.close();
            Logined = true;
            dataName = dataname;
        } catch (SQLException throwables) {
            System.out.println("login failed");
            Logined = false;
            dataName = "";
        }

    }

    public String getData(int row, int conlum) {
        return data.get(row)[conlum];
    }

    public String[] getArrData(int index) {
        String num[] = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            num[i] = data.get(i)[index];
        }
        return num;
    }

    public LinkedList<Integer> getLocalChar() {
        return localChar;
    }

    public int size() {
        return data.size();
    }


    public void add(String[] data) {
        if (data.length != 4 && data.length != 5) {
            System.out.println("wrong database obj!");
            return;
        }
        command = "use " + dataName;
        try {
            state.executeQuery(command);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        for (int i = 0; i < data.length; i++) {
            if (data[i].equals("")) {
                data[i] = null;
            }
        }

        if (data.length == 4) {
            command = "INSERT INTO language (word, type, speak, trans) VALUES ('" +
                    data[0] + "', '" +
                    data[1] + "', '" +
                    data[2] + "', '" +
                    data[3] +
                    "');";
        } else if (data.length == 5) {
            command = "INSERT INTO language (word, type, speak, trans, same) VALUES ('" +
                    data[0] + "', '" +
                    data[1] + "', '" +
                    data[2] + "', '" +
                    data[3] + "', '" +
                    data[4] +
                    "');";
        } else {
            command = "";
        }
        System.out.println(command);
        dataLog.add(new String[]{ConsoleApp.WORK, command});
        try {
            state.executeQuery(command);
            String log = "successfully to add word!";
            System.out.println(log);
            dataLog.add(new String[]{ConsoleApp.WORK, log});
            /*update();*/
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataLog.add(new String[]{ConsoleApp.ERROR, throwables.getMessage()});
        }

    }

    public void changeData(String[] newWord, String[] oldWord) {
        String[] newValue = new String[newWord.length];
        for (int i = 0; i < newValue.length; i++) {
            newValue[i] = newWord[i];
            if (newValue[i] == null && i == 2) {
                newValue[i].replace("'", "''");
            }
            if (newValue[i] == null || newValue[i].isEmpty()) {
                newValue[i] = "= NULL";
            } else {
                newValue[i] = "= '" + newWord[i] + "'";
            }
        }
        String[] oldValue = new String[oldWord.length];
        for (int i = 0; i < oldValue.length; i++) {
            oldValue[i] = oldWord[i];
            if (oldValue[i] != null && !oldValue[i].isEmpty() && i == 2) {
                oldValue[i].replace("'", "''");
            }
            if (oldValue[i] == null || oldValue[i].isEmpty()) {
                oldValue[i] = "IS NULL";
            } else {
                oldValue[i] = "LIKE '" + oldValue[i] + "'";
            }
        }
        try {
            if (newValue.length != 4 && newValue.length != 5) {
                String log = "wrong database obj!";
                System.out.println(log);
                dataLog.add(new String[]{ConsoleApp.ERROR, log});
                return;
            }
            state.executeQuery("use " + dataName + ";");
            command = "select * from language " +
                    "WHERE word " + oldValue[0] + " " +
                    "AND type " + oldValue[1] + " " +
                    "AND speak " + oldValue[2] + " " +
                    "AND trans " + oldValue[3] + "";
            System.out.println(command);
            ResultSet sql = state.executeQuery(command);
            System.out.println(command);
            if (!sql.next()) {
                String log = "opps! wrong data word";
                System.out.println(log);
                dataLog.add(new String[]{ConsoleApp.ERROR, log});
                return;
            }
            command = "UPDATE language SET word " + newValue[0] + "" +
                    ",type " + newValue[1] + " " +
                    ",speak " + newValue[2] + " " +
                    ",trans " + newValue[3] + " " +
                    "WHERE word " + oldValue[0] + " " +
                    "AND type " + oldValue[1] + " " +
                    "AND speak " + oldValue[2] + " " +
                    "AND trans " + oldValue[3] + "";
            System.out.println(command);
            dataLog.add(new String[]{ConsoleApp.WORK, command});
            state.executeQuery(command);
            for (int i = 0; i < newValue.length; i++) {
                if (newValue[i].equals("= NULL") || newWord[i].isEmpty()) {
                    newValue[i] = "IS NULL";
                } else {
                    newValue[i] = "LIKE '" + newWord[i] + "'";
                }
            }
            command = "select * from language " +
                    "WHERE word " + newValue[0] + " " +
                    "AND type " + newValue[1] + " " +
                    "AND speak " + newValue[2] + " " +
                    "AND trans " + newValue[3] + "";
            System.out.println(command);
            sql = state.executeQuery(command);
            if (sql.next()) {
                String log = "successfully to update word!";
                System.out.println(log);
                dataLog.add(new String[]{ConsoleApp.WORK, log});
            }
            /*update();*/
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update() {
        command = "select * from language order by word";
        try {
            database = state.executeQuery(command);          //lệnh lấy bảng trên database
            data = new LinkedList<>();
            String num[];
            data.clear();
            while (database != null && database.next()) {
                num = new String[number_oj];
                num[0] = database.getString("word").toLowerCase();
                num[1] = database.getString("type");
                num[2] = database.getString("speak");
                num[3] = database.getString("trans");
                data.addLast(num);
                if (num.equals("xaomalin")) {
                    System.out.println("co tu nay");
                }
            }
            localChar = setLocaChar(data, 0);
            System.out.println("updated database");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void remove(String word) {
        if (word.isEmpty()) {
            String log = "wrong word!";
            System.out.println(log);
            dataLog.add(new String[]{ConsoleApp.ERROR, log});
            return;
        }
        try {
            state.executeQuery("use " + dataName + ";");
            command = "delete from language " +
                    "WHERE word like '" + word + "' ";
            System.out.println(command);
            state.executeQuery(command);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LinkedList<String[]> getDataLog() {
        return dataLog;
    }


    public void close() {
        try {
            if (connection != null) {
                state.close();
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private LinkedList<Integer> setLocaChar(LinkedList<String[]> arr, int index) {
        LinkedList<Integer> save = new LinkedList<>();
        for (int i = 0; i < 26; i++) {
            save.add(-1);
        }
        save.set(arr.get(0)[index].charAt(0) - 97, 0);
        for (int i = 1; i < arr.size(); i++) {
            if (arr.get(i)[index].charAt(0) != arr.get(i - 1)[index].charAt(0)) {
                save.set(arr.get(i)[index].charAt(0) - 97, i);
            }
        }
        save.add(arr.size());
        return save;
    }
}
