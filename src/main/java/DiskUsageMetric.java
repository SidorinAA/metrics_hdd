import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiskUsageMetric extends Metric {

    int value;

    public DiskUsageMetric(String serverUrl, int interval) {
        super(serverUrl, interval);
    }

    public DiskUsageMetric(String value){
        this.value = Integer.parseInt(value);
    }

    @Override
    public String getNotificationText() {
        return String.format("disk usage is %d%%", value);
    }

    @Override
    public String getMetric() {
        String result = executeCommand("fsutil volume diskfree C:"); //df
        Pattern pattern = Pattern.compile("[\\d+]%");
        Matcher m = pattern.matcher(result);
        while (m.find()){
            return m.group(1);
        }
        return "___";
    }

    @Override
    public boolean alert() {
        return value > 77;
    }
}
