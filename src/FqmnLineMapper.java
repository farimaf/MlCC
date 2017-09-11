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
    private String blockstatsInputPath = "./file-stats.file";
    //private String blockstatsInputPath= "C:\\clone_data\\new benchmark\\file_block_stats\\files-stats-";
    private String blocktokensInputPath = "./blocks_with_method_names.file";
    //private String blocktokensInputPath= "C:\\clone_data\\new benchmark\\blocks_tokens\\files-tokens-";
    PrintWriter printWriter = null;
    private static int dollarMethods=0;
    private static int DEFAULT_dir = 11;
    private static int SAMPLE_dir = 12;
    private static int SELECTED_dir = 13;

    public static void main(String[] args) {

        FqmnLineMapper fqmnLineMapper = new FqmnLineMapper();
        HashMap<String, String> fileIdFileNameMap = new HashMap<>();
        HashMap<String, String> methodIdMethodStatMap = new HashMap<>();
//        HashMap<String,String> subdirectoryMap=new HashMap<>();
//        subdirectoryMap.put("11","default");
//        subdirectoryMap.put("12","sample");
//        subdirectoryMap.put("13","selected");

        try {
            fqmnLineMapper.printWriter = new PrintWriter(Paths.get("./output/IjaMapping_new_18aug.txt").toString());
            //for (int i = 0; i < 3; i++) {
            String blockstatsInputPath = fqmnLineMapper.blockstatsInputPath;
            //blockstatsInputPath = blockstatsInputPath + i + ".stats";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(Paths.get(blockstatsInputPath).toString()));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                String[] lineSplitted = line.replaceAll("\"", "").split(",");
                if (line.startsWith("f")) {
                    String[] fileName = lineSplitted[2].split("/");
                    //directoryid->directoryName,fileName
                    //fileIdFileNameMap.put(lineSplitted[1], fileName[7] + "_" + fileName[8].split("\\.")[0]);
                    fileIdFileNameMap.put(lineSplitted[1], fileName[7] + "," + fileName[8]);
                } else if (line.startsWith("b")) {
                    //fileid->startline,endline
                    methodIdMethodStatMap.put(lineSplitted[1], lineSplitted[6] + "," + lineSplitted[7]);
                }
            }

            String blocktokensInputPath = fqmnLineMapper.blocktokensInputPath;
            //blocktokensInputPath=blocktokensInputPath+i+".tokens";
            bufferedReader = new BufferedReader(new FileReader(Paths.get(blocktokensInputPath).toString()));
            line = "";
            while ((line = bufferedReader.readLine()) != null) {
                String[] lineSplitted = line.split("~~");
                //output line format: FQMN:directory,filename,start_line,end_line,#tokens,#unique_tokens,directoryid,fileid,token_hash
                if (Integer.valueOf(lineSplitted[2]) > 50) {
                    if(!lineSplitted[4].contains("$")) {
                        String lineToWrite =
                                //commented for train dataset
                                //fileIdFileNameMap.get(lineSplitted[1].substring(5)) + "." +
                                lineSplitted[4] + ":"
//                            + fileIdFileNameMap.get(lineSplitted[1].substring(5)).split("_")[0] + "," +
//                            fileIdFileNameMap.get(lineSplitted[1].substring(5)).split("_")[1] + ".java" + "," +
                                        + fileIdFileNameMap.get(lineSplitted[1].substring(5)) +
                                        methodIdMethodStatMap.get(lineSplitted[1]) + "," + lineSplitted[2] + "," + lineSplitted[3] + "," + lineSplitted[0] + "," + lineSplitted[1] +
                                        "," + lineSplitted[5].substring(0, lineSplitted[5].indexOf("@#@"));
                        System.out.println(lineToWrite);
                        fqmnLineMapper.writeToFile(lineToWrite);
                    }
                    else dollarMethods++;
                }
            }

            fqmnLineMapper.printWriter.close();
            System.out.println("number of methods with $ sign that were removed: "+dollarMethods);
        }
        catch(Exception e){
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
