package sample.App.home;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class VoiceSave {
    private JSONObject data;

    private String PATH = "src/main/Resource/sample/App/home/voice.json";

    public VoiceSave() {
        FileReader reader = null;
        try {
            JSONParser jsonParser = new JSONParser();
            reader = new FileReader("src/main/Resource/sample/App/home/voice.json");
            data = (JSONObject) jsonParser.parse(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Đường dẫn file json sai rồi");
        } catch (Exception e) {
            System.out.println("Lỗi ở class JsonReader nhá vào mà fix!");
        }
    }

    public VoiceSave(String path) {
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

    public String getVoice() {
        return (String) data.get("voice");
    }

    public boolean isUsevoice() {
        return (boolean) data.get("usevoice");
    }

    public boolean isAutoCorrect() {
        return (boolean) data.get("autocorrect");
    }

    public void set_Voice(String voice, boolean usevoice, boolean useauto) {
        data.put("voice", voice);
        data.put("usevoice", usevoice);
        data.put("autocorrect", useauto);
        /*"useautosave":true*/
        try {
            FileWriter file = new FileWriter(PATH);
            file.write(data.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        VoiceSave voiceSave = new VoiceSave();
    }
}

