package application.crawler.util;


import java.util.Base64;
import javax.xml.bind.DatatypeConverter;
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);

        gzip.write(str.getBytes("ISO-8859-1"));
        gzip.close();

        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    public static String decompressString(String str) throws IOException {
        String outStr = "";
        String line;
        byte[] decodedBytes = Base64.getDecoder().decode(str);

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(decodedBytes));
        BufferedReader reader = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));

        while ((line = reader.readLine()) != null) {
            outStr += line;
        }

        return outStr;
    }

}

