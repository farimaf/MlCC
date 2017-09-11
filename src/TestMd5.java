import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * Created by Farima on 8/20/2017.
 */
public class TestMd5 {
    public static void main(String[] args) {
        try {
            System.out.println(replaceExpression("farima125farahani", "125", "uci"));
            System.out.println(substitute("farima125farahani", "125", "uci"));


            File in=new File("farima.txt");
            File out=new File("farahani.txt");
            //copyFile(in,out,true);
            copyFile(in,out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String replaceExpression(String input, String replace, String replacement) {
        int idx;
        if ((idx = input.indexOf(replace)) == -1) {
            return input;
        }
        boolean finished = false;
        while (!finished) {
            StringBuffer returning = new StringBuffer();
            while (idx != -1) {
                returning.append(input.substring(0, idx));
                returning.append(replacement);
                input = input.substring(idx + replace.length());
                idx = input.indexOf(replace);
            }
            returning.append(input);
            input = returning.toString();
            if ((idx = returning.indexOf(replace)) == -1) {
                finished = true;
            }
        }
        return input;
    }

    static String substitute(String input, String var, String value) throws IOException {
        StringBuffer out = new StringBuffer();
        int varlen = var.length();
        int oidx = 0;
        for (; ; ) {
            int idx = input.indexOf(var, oidx);
            if (idx == -1) break;
            out.append(input.substring(oidx, idx));
            idx += varlen;
            out.append(value);
            oidx = idx;
        }
        out.append(input.substring(oidx));
        return out.toString();
    }


    public static void copyFile(File in, File out, boolean copyModified) throws IOException {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            long size = inChannel.size();
            long position = 0;
            while (position < size) {
                position += inChannel.transferTo(position, maxCount, outChannel);
            }
            if (copyModified) out.setLastModified(in.lastModified());
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }

    public static void copyFile(File source, File target) throws Exception {
        if (source == null || target == null) {
            throw new IllegalArgumentException("The arguments may not be null.");
        }
        try {
            FileChannel srcChannel = new FileInputStream(source).getChannel();
            FileChannel dtnChannel = new FileOutputStream(target).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dtnChannel);
            srcChannel.close();
            dtnChannel.close();
        } catch (Exception e) {
            String message = "Unable to copy file '" + source.getName() + "' to '" + target.getName() + "'.";
            //logger.error(message, e);
            throw new Exception(message, e);
        }
    }
}
