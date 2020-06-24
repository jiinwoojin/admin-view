import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String... args) {
        Process process;
        Runtime runtime = Runtime.getRuntime();
        StringBuilder successOutput = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();
        BufferedReader successBufferReader;
        BufferedReader errorBufferReader;

        String msg;
        List<String> cmdList = new ArrayList<>();

        cmdList.add("/bin/sh");
        cmdList.add("-c");
        cmdList.add("gdalbuildvrt /data/jiapp/data_dir/tmp/navy/test.vrt /data/jiapp/data_dir/tmp/navy/*");

        String[] cmd = cmdList.toArray(new String[cmdList.size()]);

        try {
            System.out.println(Arrays.toString(cmd));
            process = runtime.exec(cmd);

            successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

            while ((msg = successBufferReader.readLine()) != null) {
                System.out.println(msg);
            }

            errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

            while ((msg = errorBufferReader.readLine()) != null) {
                System.out.println(msg);
            }

            process.waitFor();
            System.out.println(process.exitValue());

            if (process.exitValue() == 0) {
                System.out.println("성공");
            } else {
                System.out.println("실패");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
