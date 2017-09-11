import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Farima on 8/11/2017.
 */
public class SccBenchmarkMapper {
    private String inputIjaMappingPath=  "/scratch/mondego/local/farima/farima-socket-code/recall_exp_benchmark/scc_files/output/IjaMapping_new_18aug.txt";
    private String inputSCCPath=  "/scratch/mondego/local/farima/farima-socket-code/recall_exp_benchmark/scc_files/clone-pairs-by-scc-on-pruned-blocks.file";
    private String outputPath="./output/SCC_pruned_BenchmarkFormat.txt";
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
                printWriter.append(ijaMapping.get(lineSplitted[0]+","+lineSplitted[1])+","+ijaMapping.get(lineSplitted[2]+","+lineSplitted[3])+System.lineSeparator());
            }
            printWriter.close();
            bfSCC.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
