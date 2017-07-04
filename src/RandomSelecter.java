import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by farima on 6/27/17.
 */
public class RandomSelecter {
    private ArrayList<String> filesList=new ArrayList<>();
    private ArrayList<String> trainList=new ArrayList<>();
    private ArrayList<String> testList=new ArrayList<>();
    private String inputPath=  "./input/project_metrics/";
    private String trainPath= "./output/train";
    private String testPath= "./output/test";

    RandomSelecter(){
        File folder = new File(inputPath);
        File[] files=folder.listFiles();
        for (int i = 0; i <files.length ; i++) {
          if (files[i].isFile())
              filesList.add(files[i].getName());
        }
    }

    public void makeRandomTestTrain()
    {
        int filesCount=filesList.size();
        Random random=new Random(0);
        while (trainList.size()<=(int)filesList.size()*0.6) {
            int nextFile = random.nextInt(filesList.size());
            if (!trainList.contains(filesList.get(nextFile)))
                trainList.add(filesList.get(nextFile));

        }
        for (String s:filesList){
            if (!trainList.contains(s)) testList.add(s);
        }

        try {
            for (String s:trainList){
                Files.copy(new File(inputPath+s).toPath(),new File(trainPath+"/"+s).toPath());
            }
            for (String s:testList){
                Files.copy(new File(inputPath+"/"+s).toPath(),new File(testPath+"/"+s).toPath());
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }

}
