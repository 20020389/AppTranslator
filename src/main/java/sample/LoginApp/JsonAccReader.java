package sample.LoginApp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class JsonAccReader {
    private JSONObject data;
    private String user;
    private String password;
    private String csdl;
    private boolean isremember;

    private String PATH = "src/main/Resource/sample/LoginApp/account.json";
    public JsonAccReader() {
        FileReader reader = null;
        try {
            JSONParser jsonParser = new JSONParser();
            reader = new FileReader(PATH);
            data = (JSONObject) jsonParser.parse(reader);
            user = (String) data.get("user");
            password = (String) data.get("password");
            csdl = (String) data.get("csdl");
            isremember = (boolean) data.get("remember");
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Đường dẫn file json sai rồi :v");
        } catch (Exception e) {
            System.out.println("Lỗi ở class JsonReader nhá vào mà fix!");
        }

    }

    public JsonAccReader(String path) {
        FileReader reader;
        try {
            JSONParser jsonParser = new JSONParser();
            reader = new FileReader(path);
            data = (JSONObject) jsonParser.parse(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Đường dẫn file json sai rồi :v");
        } catch (Exception e) {
            System.out.println("Lỗi ở class JsonReader nhá vào mà fix!");
        }
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsremember() {
        return isremember;
    }

    public String getCsdl() {
        return csdl;
    }


    public void saveData(String user, String password,String csdl, boolean isremember) {
        this.user = user;
        this.password = password;
        this.isremember = isremember;
        this.csdl = csdl;
        data.replace("remember", this.isremember);
        data.replace("password",this.password);
        data.replace("user", this.user);
        data.replace("csdl", this.csdl);
        try {
            FileWriter file = new FileWriter(PATH);
            file.write(data.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JsonAccReader a = new JsonAccReader();
        System.out.println();
    }
}

