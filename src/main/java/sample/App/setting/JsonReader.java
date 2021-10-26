package sample.App.setting;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class JsonReader {
    private JSONObject data;
    private JSONObject settingData;
    private JSONObject opacity;
    private String[] listBg;
    public static int n;

    private String PATH = "src/main/Resource/sample/App/setting/setting.json";
    public JsonReader() {
        FileReader reader = null;
        try {
            JSONParser jsonParser = new JSONParser();
            reader = new FileReader("src/main/Resource/sample/App/setting/setting.json");
            data = (JSONObject) jsonParser.parse(reader);
            settingData = (JSONObject) data.get("setting");
            listBg = get_NameBG("src/main/Resource/assets/bg");
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Đường dẫn file json sai rồi :v");
        } catch (Exception e) {
            System.out.println("Lỗi ở class JsonReader nhá vào mà fix!");
        }
    }

    public JsonReader(String path) {
        FileReader reader;
        try {
            JSONParser jsonParser = new JSONParser();
            reader = new FileReader(path);
            data = (JSONObject) jsonParser.parse(reader);
            settingData = (JSONObject) data.get("setting");
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Đường dẫn file json sai rồi :v");
        } catch (Exception e) {
            System.out.println("Lỗi ở class JsonReader nhá vào mà fix!");
        }
    }

//    public String getListBG() {
//
//    }

    public String get_Link(){
        return (String) settingData.get("link_bg");
    }
    public double get_Opacity() {
        return (double) settingData.get("opacity");
    }
    public String[] get_Listbg() {
        return listBg;
    }

    public void set_Link(String link,double opa) {
        if (opa > 1) {
            opa = 1;
        }
        settingData.put("link_bg",link);
        settingData.put("opacity",opa);
        data.put("setting", settingData);
        try {
            FileWriter file = new FileWriter(PATH);
            file.write(data.toJSONString());
            file.close();
            System.out.println("saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] get_NameBG(String folder) {
        File directory;        // File object referring to the directory.
        String[] files = null;        // Array of file names in the directory.
        directory = new File(folder);
        if (directory.isDirectory() == false) {
            if (directory.exists() == false)
                System.out.println("There is no such directory!");
            else
                System.out.println("That file is not a directory.");
        }
        else {
            files = directory.list();
        }
        return files;
    }

}

