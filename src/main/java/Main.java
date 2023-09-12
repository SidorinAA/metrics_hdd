import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {

        if(args.length > 0){
            int port = Integer.parseInt(args[0]);
            System.out.format("Server: get our metrics in port %d\n", port);

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            PushService pushService = new PushService();
            server.createContext("/", new MyHandler(pushService));
            server.start();
        } else {
            System.out.println("Agent, collect metrics");
            Metric diskUsage = new DiskUsageMetric("http://localhost:9001", 10000);
            while(true){
                Thread.sleep(diskUsage.getInterval());
                diskUsage.post();
            }
        }
    }


    static class MyHandler implements HttpHandler {
        PushService pushService;

        public MyHandler(PushService pushService){
            this.pushService = pushService;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String metricsName = exchange.getRequestURI().getPath().replace("/", "");

            if(exchange.getRequestMethod().equalsIgnoreCase("POST")){
                String[] lines = new BufferedReader(
                        new InputStreamReader(exchange.getRequestBody()))
                        .lines().toArray(String[]::new);

                System.out.println("\n\nrequest:");
                for(String line : lines){
                    System.out.println(line);
                    String[] parts = line.split(" ");
                    Metric metric = null;
                    switch(parts[0]){
                        case "DiskUsageMetric":
                            metric = new DiskUsageMetric(parts[1]);
                            break;
                    }

                    if(metric != null && metric.alert()){
                        pushService.notify(metric);
                    }
                }
            }

            String response = "ok";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));

        }
    }
}
