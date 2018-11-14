    import java.io.File;
    import java.io.IOException;
    import java.net.MalformedURLException;
     
    import javax.sound.sampled.AudioFormat;
    import javax.sound.sampled.AudioInputStream;
    import javax.sound.sampled.AudioSystem;
    import javax.sound.sampled.Clip;
    import javax.sound.sampled.DataLine;
    import javax.sound.sampled.LineUnavailableException;
    import javax.sound.sampled.UnsupportedAudioFileException;
      
    public class Nov06Sound1p {
    	public static void main(String[] args) {
    		createClip(new File("hapyou.wav"));
    		//ここで再生メソッドの呼び出し
    	}
     
    	public static Clip createClip(File path) {
    		//指定されたURLのオーディオ入力ストリームを取得
    		try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){
    			
    			//ファイルの形式取得
    			AudioFormat af = ais.getFormat();
    			
    			//単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
    			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
    			
    			//指定された Line.Info オブジェクトの記述に一致するラインを取得
    			Clip c = (Clip)AudioSystem.getLine(dataLine);
    			
    			//再生準備完了
    			c.open(ais);
    			c.start();
    			
    			return c;
    		} catch (MalformedURLException e) {
    			e.printStackTrace();
    		} catch (UnsupportedAudioFileException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (LineUnavailableException e) {
    			e.printStackTrace();
    		}
    		return null;
    	}
    }
