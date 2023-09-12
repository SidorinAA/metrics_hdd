import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PushService {

    static final String SERVER_KEY = "";
    static final String ID_TOKEN = "";
    static final String SERVER_URL = "";
    static final String FCM_URL = "";

    public PushService() {
    }

    public boolean notify(Metric metric) throws IOException {
        sendNotification(metric.getClass().getSimpleName(), metric.getNotificationText());
        return true;
    }

    void sendNotification(String title, String body) throws IOException {
        System.out.println("notification");
        URL obj = new URL(FCM_URL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "key="+ SERVER_KEY);
        con.setRequestProperty("Content-Type", "application/json");


        con.setDoOutput(true);

        byte[] out = ("{\n" +
                "\"notification\": {\n" +
                " \"title\": \"" + title + "\", \n" +
                " \"body\": \"" + body + "\", \n" +
                " \"icon\": ").getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        con.setFixedLengthStreamingMode(length);
        con.connect();
        try (OutputStream os = con.getOutputStream()){
            os.write(out);
        }

        int responseCode = con.getResponseCode();
        System.out.println("Response code="+ responseCode + ".");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }

        in.close();
        System.out.println(response.toString());

    }

}
