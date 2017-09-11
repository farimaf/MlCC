import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by Farima on 7/12/2017.
 */
public class SampleEqualCloneNonclone {
    private String pathFirstPart= "/scratch/mondego/local/farima/mlcc_related/SourcererCC/clone-detector/NODE_";
    private String pathSecondPart= "/output7.0/queryclones_index_WITH_FILTER_2.txt";
    private int nodesCount=3;
    //private String inputPath= "./output/train_integrated/train_doublefeature.txt";
    private HashSet<String> readLines=new HashSet<>();
    private HashMap<String,HashSet<Integer>> cloneLines=new HashMap<>();
    private HashSet<String> randomNoncloneLines=new HashSet<>();
    //private final int SAMPLE_COUNT=22151199*2;
    //private final int SAMPLE_COUNT=1205138;
    private final int SAMPLE_COUNT=478885*2;
    private PrintWriter printWriter;
    private HashMap<String,Integer> lineNumbers=new HashMap<>();
    SampleEqualCloneNonclone(){
        try{
            //linesCount=bufferedReader.lines().count();
            printWriter=new PrintWriter(Paths.get("./output/train_integrated/train_type2_2TimesNonclone.txt").toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sampler(){
        String[] fileChance={"NODE_1","NODE_1","NODE_1","NODE_1","NODE_1","NODE_1","NODE_2","NODE_2","NODE_2","NODE_3"};
        try {
            for (int i = 1; i <= nodesCount; i++) {
                String path = pathFirstPart + i + pathSecondPart;
                String nodeName = "NODE_" + i;
                BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
                String line = "";
                int lineNum = 0;
                cloneLines.put(nodeName, new HashSet<>());
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.split("~~")[2].equals("1")) {
                        cloneLines.get(nodeName).add(lineNum);
                    }
                    lineNum++;
                }
                System.out.println("1 read complete");
                lineNumbers.put(nodeName, lineNum);
            }

            Random random = new Random(12);
            int randNumNode = 0;
            int randNumLine = 0;

            while (randomNoncloneLines.size() < SAMPLE_COUNT) {
                randNumNode = random.nextInt(fileChance.length);
                String filePicked = fileChance[randNumNode];
                randNumLine = random.nextInt(lineNumbers.get(filePicked));
                if (!cloneLines.get(filePicked).contains(randNumLine))
                    randomNoncloneLines.add(filePicked + ":" + randNumLine);
            }
            System.out.println("random making complete");
            for (int i = 1; i <= nodesCount; i++) {
                String path = pathFirstPart + i + pathSecondPart;
                String nodeName = "NODE_" + i;
                BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
                String line = "";
                int lineNum = 0;
                int filesize = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.split("~~")[2].equals("1") || randomNoncloneLines.contains(nodeName+":"+lineNum)) {
                        filesize++;
                        writeToFile(line);
                        System.out.println(line);
                    }
                    lineNum++;
                }

                System.out.println("one node written. size: "+filesize);

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
