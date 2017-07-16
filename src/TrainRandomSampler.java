import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by farima on 7/5/17.
 */
public class TrainRandomSampler {
    private String inputPath= "C:\\clone_data\\train.txt";
    private HashSet<String> readLines=new HashSet<>();

    private final int SAMPLE_COUNT=22151199*2;
    private PrintWriter printWriter;

    public TrainRandomSampler(){
        try{
            BufferedReader bufferedReader=new BufferedReader(new FileReader(inputPath));
            //linesCount=bufferedReader.lines().count();
            printWriter=new PrintWriter(Paths.get("./output/train_integrated/train_sample_large.txt").toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sampler(){
        File f = new File(inputPath);
        RandomAccessFile file;

        try {
            Random random=new Random(12);
            file = new RandomAccessFile(f, "r");
            long file_size = file.length();
            int counter=0;
            while (counter<=SAMPLE_COUNT) {
                // Let's start
                long chosen_byte = (long) (random.nextDouble() * (file_size - 1));
                long cur_byte = chosen_byte;

                // Goto starting position
                file.seek(cur_byte);

                String s_LR = "";
                char a_char;

                // Get left hand chars
                for (; ; ) {
                    a_char = (char) file.readByte();
                    if (cur_byte < 0 || a_char == '\n' || a_char == '\r' || a_char == -1) break;
                    else {
                        s_LR = a_char + s_LR;
                        --cur_byte;
                        if (cur_byte >= 0) file.seek(cur_byte);
                        else break;
                    }
                }

                // Get right hand chars
                cur_byte = chosen_byte + 1;
                file.seek(cur_byte);
                for (; ; ) {
                    a_char = (char) file.readByte();
                    if (cur_byte >= file_size || a_char == '\n' || a_char == '\r' || a_char == -1) break;
                    else {
                        s_LR += a_char;
                        ++cur_byte;
                    }
                }
                //System.out.println(cur_byte);
                // Parse ID
                if (cur_byte < file_size&&!readLines.contains(s_LR)) {
                    readLines.add(s_LR);
                    writeToFile(s_LR);
                    System.out.println(s_LR);
//                    int chosen_id = Integer.parseInt(s_LR);
//                    System.out.println("Chosen id : " + chosen_id);
                    counter++;
                } else {
                    System.out.println("Ran out of bounds");
                }
            }
            printWriter.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void writeToFile(String line){
        try {
            printWriter.append(line+System.lineSeparator());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
