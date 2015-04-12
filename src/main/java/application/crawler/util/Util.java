package application.crawler.util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Util {
    public synchronized static String toSha256(String str){
        str = str.toLowerCase();
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes("ASCII"));
            byte[] hash = md.digest();

            for(Byte b : hash){
                sb.append(b);
            }

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static String compressString(String str) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(outputStream);

        gzip.write(str.getBytes());
        gzip.close();
        String outStr = outputStream.toString("ISO-8859-1");

        return outStr;
    }

    public static String decompressString(String str) throws IOException {
        String outStr = "";
        String line;

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
        BufferedReader reader = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));

        while ((line = reader.readLine()) != null) {
            outStr += line;
        }

        return outStr;
    }
}

