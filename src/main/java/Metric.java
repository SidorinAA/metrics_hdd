import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class Metric {

    String serverUrl;
    String value;
    int interval;

    public Metric(String serverUrl, int interval){
        this.serverUrl = serverUrl;
        this.interval = interval;
    }

    public Metric(){
    }

    public Metric(String value){
        this.value = value;
    }

    public int getInterval(){
        return interval;
    }

    public String getNotificationText(){
        return "none";
    }

    public String getMetric(){
        return "nothing happens";
    }

    public boolean alert(){
        return false;
    }

    public void post() throws IOException {
        URL obj = new URL(serverUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoInput(true);

        byte[] out = (this.getClass().getSimpleName() + " " + getMetric()).getBytes(StandardCharsets.UTF_8);

        con.setFixedLengthStreamingMode(out.length);
        con.connect();;
        try(OutputStream os = con.getOutputStream()){
            os.write(out);
        }
        int responseCode = con.getResponseCode();
        System.out.println("Response code="+responseCode+".");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }

        in.close();
        System.out.println(response.toString());

    }

    protected String executeCommand(String command){
        StringBuffer output = new StringBuffer();
        Process p;
        try{
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return output.toString();
    }





}
