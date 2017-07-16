import java.io.*;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by Farima on 7/12/2017.
 */
public class SampleEqualCloneNonclone {
    private String inputPath= "C:\\clone_data\\train.txt";
    private HashSet<String> readLines=new HashSet<>();
    private HashSet<Integer> cloneLines=new HashSet<>();
    private HashSet<Integer> randomNoncloneLines=new HashSet<>();
    private final int SAMPLE_COUNT=22151199*2;
    private PrintWriter printWriter;
    SampleEqualCloneNonclone(){
        try{
            //linesCount=bufferedReader.lines().count();
            printWriter=new PrintWriter(Paths.get("./output/train_integrated/train_twotimes_NonClone.txt").toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    public void sampler(){

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputPath));
            String line="";
            int lineNum=0;
            while ((line=bufferedReader.readLine())!=null){
                if (line.split(",")[2].equals("1")) {
                    cloneLines.add(lineNum);
                }
                lineNum++;
            }
            System.out.println("read complete");
            int fileLinesCount=lineNum;
            Random random=new Random(12);
            int randNum=0;

            while (randomNoncloneLines.size()<SAMPLE_COUNT){
                randNum=random.nextInt(fileLinesCount);
                if (!cloneLines.contains(randNum))
                    randomNoncloneLines.add(randNum);
            }
            System.out.println("random making complete");
            bufferedReader = new BufferedReader(new FileReader(inputPath));
            line="";
            lineNum=0;
            int filesize=0;
            while ((line=bufferedReader.readLine())!=null){
                if (line.split(",")[2].equals("1")||randomNoncloneLines.contains(lineNum)) {
                    filesize++;
                    writeToFile(line);
                    System.out.println(line);
                }
                lineNum++;
            }
            System.out.println(filesize);
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
