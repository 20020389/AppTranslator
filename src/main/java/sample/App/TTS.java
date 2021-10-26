package sample.App;

import com.voicerss.tts.*;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class TTS {
    private VoiceProvider tts;
    private VoiceParameters params;
    String PATH;
    Media m;
    MediaPlayer media;
    boolean inited;
    boolean havevoice;
    String Text;
    public static Thread thr = null;
    public static boolean Running = false;
    Button Cpy;

    public TTS(String path) {
        tts = new VoiceProvider("d42aae924a3b49b087dc21537688f086");
        params = new VoiceParameters("adam", Languages.English_UnitedStates);
        inited = false;
        params = new VoiceParameters("", Languages.English_UnitedStates);
        params.setCodec(AudioCodec.MP3);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setVoice(Voices.English_UnitedStates.John);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);
        this.PATH = path + "/voice.mp3";
        Path trypath = Paths.get(PATH);
        if (Files.exists(trypath)) {
            havevoice = true;
            System.out.println("yes");
        } else {
            havevoice = false;
            System.out.println("no");
        }
        Text = " ";
        Cpy = null;
    }

    public void setVoice(String voice) {
        params.setVoice(voice);
    }

    private void speech(){
        params.setText(Text);
        save();
        System.out.println("Done!");
    }

    public void setCpy(Button cpy) {
        this.Cpy = cpy;
    }

    private boolean checkSpace(String text) {
        if (text.length() == 0) {
            return true;
        }
        for (int i = 0; i < text.length(); i ++) {
            if (text.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    public void play() {
        if (havevoice) {
            if (!inited) {
                m = new Media(Paths.get(PATH).toUri().toString());
                inited = true;
            }
            media = new MediaPlayer(m);
            media.setVolume(100);
            media.play();
        }
    }

    public void start() {
        if (!checkSpace(Text)) {
            stop();
            thr = new Thread(()->{
                Running = true;
                if (Cpy != null)
                    Cpy.setDisable(true);
                speech();
                if (Cpy != null)
                    Cpy.setDisable(false);
                Running = false;
            });
            thr.start();
        }
    }

    public void start(String text) {
        if (!checkSpace(text)) {
            stop();
            this.setText(text);
            thr = new Thread(()->{
                Running = true;
                if (Cpy != null)
                    Cpy.setDisable(true);
                speech();
                if (Cpy != null)
                    Cpy.setDisable(false);
                Running = false;
            });
            thr.start();
        }
    }

    public void stop() {
        if (thr != null) {
            if (thr.isAlive())
                thr.stop();
        }
    }

    public void setText(String text) {
        Text = text;
    }

    public void setLang(String lang) {
        params.setVoice(lang);
    }

    public boolean isRunning() {
        return Running;
    }

    private void save(){
        try {
            FileOutputStream fos = new FileOutputStream(PATH);
            System.out.println(params.getText());
            byte[] voice = tts.speech(params);
            fos.write(voice, 0, voice.length);
            fos.flush();
            fos.close();
            havevoice = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
