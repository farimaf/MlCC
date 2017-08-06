import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Farima on 7/31/2017.
 */
public class FqmnLineMapper {
    private String blockstatsInputPath= "./new_benchmark/file_block_stats/files-stats-";
   // private String blockstatsInputPath= "C:\\clone_data\\new benchmark\\file_block_stats\\files-stats-";
    private String blocktokensInputPath= "./new_benchmark/blocks_tokens/files-tokens-";
   // private String blocktokensInputPath= "C:\\clone_data\\new benchmark\\blocks_tokens\\files-tokens-";
    PrintWriter printWriter=null;

    private static int DEFAULT_dir=11;
    private static int SAMPLE_dir=12;
    private static int SELECTED_dir=13;
    public static void main(String[] args) {

        FqmnLineMapper fqmnLineMapper=new FqmnLineMapper();
        HashMap<String,String> fileIdFileNameMap=new HashMap<>();
        HashMap<String,String> methodIdMethodStatMap=new HashMap<>();
//        HashMap<String,String> subdirectoryMap=new HashMap<>();
//        subdirectoryMap.put("11","default");
//        subdirectoryMap.put("12","sample");
//        subdirectoryMap.put("13","selected");

        try {
            fqmnLineMapper.printWriter = new PrintWriter(Paths.get("./output/IjaMapping_new.txt").toString());
            for (int i = 0; i < 3; i++) {
                fqmnLineMapper.blockstatsInputPath="./new_benchmark/file_block_stats/files-stats-";
                fqmnLineMapper.blockstatsInputPath = fqmnLineMapper.blockstatsInputPath + i + ".stats";
                BufferedReader bufferedReader = new BufferedReader(new FileReader(Paths.get(fqmnLineMapper.blockstatsInputPath).toString()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    String[] lineSplitted = line.replaceAll("\"","").split(",");
                    if (line.startsWith("f")) {
                        String[] fileName = lineSplitted[2].split("/");
                        //directoryid->directoryName,fileName
                        fileIdFileNameMap.put(lineSplitted[1], fileName[7]+"_"+fileName[8].split("\\.")[0] );
                    } else if (line.startsWith("b")) {
                        //fileid->startline,endline
                        methodIdMethodStatMap.put(lineSplitted[1], lineSplitted[6] + "," + lineSplitted[7]);
                    }
                }
                fqmnLineMapper.blocktokensInputPath="./new_benchmark/blocks_tokens/files-tokens-";
                fqmnLineMapper.blocktokensInputPath=fqmnLineMapper.blocktokensInputPath+i+".tokens";
                bufferedReader = new BufferedReader(new FileReader(Paths.get(fqmnLineMapper.blocktokensInputPath).toString()));
                line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    String[] lineSplitted = line.split("~~");
                    //output line format: FQMN:directory,filename,start_line,end_line,#tokens,directoryid,fileid
                    String lineToWrite = fileIdFileNameMap.get(lineSplitted[1].substring(5))+"." +lineSplitted[4] + ":"
                            +fileIdFileNameMap.get(lineSplitted[1].substring(5)).split("_")[0]+","+
                            fileIdFileNameMap.get(lineSplitted[1].substring(5)).split("_")[1]+".java"+","+
                            methodIdMethodStatMap.get(lineSplitted[1]) + "," + lineSplitted[2] + "," + lineSplitted[0] + "," + lineSplitted[1];
                    System.out.println(lineToWrite);
                    fqmnLineMapper.writeToFile(lineToWrite);
                }
            }
            fqmnLineMapper.printWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void writeToFile(String inputString){
        try{
            printWriter.append(inputString+System.lineSeparator());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
