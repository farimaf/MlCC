import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Farima on 8/11/2017.
 */
public class SccBenchmarkMapper {
    private String inputIjaMappingPath=  "./output/IjaMapping_new_uniquetokens.txt";
    private String inputSCCPath=  "./input/consolidated_clone_pairs.txt";
    private String outputPath="./output/SCC_BenchmarkFormat.txt";
    public static void main(String[] args) {
        HashMap<String,String> ijaMapping=new HashMap<>();
        try{
            SccBenchmarkMapper sccBenchmarkMapper=new SccBenchmarkMapper();
            BufferedReader bfIjaMapping=new BufferedReader(new FileReader(Paths.get(sccBenchmarkMapper.inputIjaMappingPath).toString()));
            String line="";
            while ((line=bfIjaMapping.readLine())!=null){
                String[] lineSplitted=line.split(":");
                String[] mappings=lineSplitted[1].split(",");
                ijaMapping.put(mappings[6]+","+mappings[7],mappings[0]+","+mappings[1]+","+mappings[2]+","+mappings[3]);
            }
            bfIjaMapping.close();

            PrintWriter printWriter=new PrintWriter(Paths.get(sccBenchmarkMapper.outputPath).toString());
            line="";
            BufferedReader bfSCC=new BufferedReader(new FileReader(Paths.get(sccBenchmarkMapper.inputSCCPath).toString()));
            while ((line=bfSCC.readLine())!=null){
                String[] lineSplitted=line.split(",");
                printWriter.append(ijaMapping.get(lineSplitted[0]+","+lineSplitted[1])+","+ijaMapping.get(lineSplitted[2]+","+lineSplitted[3]));
            }
            printWriter.close();
            bfSCC.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
