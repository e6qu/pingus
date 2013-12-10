package pingus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ping {

    private static int extractNumber(String input) {
        Pattern p = Pattern.compile("time.(.*?)ms");
        Matcher m = p.matcher(input);
        if (m.find()) {
            int number = -1;

            try {
                number = Integer.parseInt(m.group(1));
            } catch (NumberFormatException nfe) {
                return -1;
            }

            return number;
        }
        return -1;
    }

    public static int pingOnce(String address) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ping", address, "-n", "1");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String out;
            int lines = 0;
            while ((out = inStreamReader.readLine()) != null) {
                ++lines;
                if (lines >= 10) {
                    return -1;
                }

                if (out.contains("time")) {
                    return extractNumber(out);
                }
            }
        } catch (IOException ex) {
            //Logger.getLogger(Ping.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return -1;
    }

    public static List<Pair> getPairs(String serversfile) {
        List<Pair> pairs = new LinkedList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(serversfile), Charset.defaultCharset());

            for (String line : lines) {
                String[] parts = line.trim().split("\\s+");
                //only consider the lines with at least two parts
                if (parts.length >= 2) {
                    //lines starting with # are comments so we should ignore them
                    if (parts[0].trim().charAt(0) == '#') {
                        continue;
                    }
                    Pair p = new Pair(parts[0], parts[1]);
                    pairs.add(p);
                }
            }
        } catch (IOException ex) {
            System.out.println("There is a problem with the serversfile. Does it exist at the location: " + serversfile + " ?");
        }
        return pairs;
    }
}
