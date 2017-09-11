import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Farima on 8/18/2017.
 */
public class DeleteMethodsNoTokens {
    static String inputMethodTokensPath=  "/scratch/mondego/local/farima/prep_code/output/Method_Token_Map.txt";//To remove methods not having any action token
    static String inputBlocksPath=  "/scratch/mondego/local/farima/prep_code/output/Blocks_new.file";
    //private String inputMetricsPath=  "./input/benchmark_jhawk_features/";
    static String inputMetricsPath=  "/scratch/mondego/local/farima/prep_code/output/consolidatedMetrics_new.csv";
    static String inputIjaMappingPath=  "/scratch/mondego/local/farima/prep_code/output/IjaMapping_benchmark.txt";
    static String outputMissingBlocksPath= "./output/missed_tokens_blocks.txt";
    static String outputMissingMetricsPath= "./output/missed_tokens_metrics.txt";
    static String outputNewBlocksPath= "./output/Blocks_new_with_action_token.file";
    static String outputNewMetricsPath= "./output/consolidatedMetrics_new_with_action_token.csv";
    static HashMap<String,String> ijaMappingBasedOnFqmns =new HashMap<>();
    static HashMap<String,String > ijaMappingBasedOnIds=new HashMap<>();
    static HashSet<String> methodsWithTokensSet=new HashSet<>();
    public static void main(String[] args) {
        try{
            BufferedReader bfIjaMapping=new BufferedReader(new FileReader(Paths.get(inputIjaMappingPath).toString()));
            String line = "";
            while ((line = bfIjaMapping.readLine()) != null ) {//insert methods having more than 50 tokens
                // if (Integer.parseInt(line.split(":")[1].split(",")[4]) > 50) {
                String[] lineSplitted = line.split(":");
                ijaMappingBasedOnFqmns.put(lineSplitted[0],lineSplitted[1]);
                String[] lineProps=lineSplitted[1].split(",");
                ijaMappingBasedOnIds.put(lineProps[6]+","+lineProps[7],lineSplitted[0]);
                // }
            }
            System.out.println("ijaMappingBasedOnFqmns.size "+ijaMappingBasedOnFqmns.size());
            System.out.println("ijaMappingBasedOnIds.size "+ijaMappingBasedOnIds.size());
            BufferedReader bfMethodToken=new BufferedReader(new FileReader(Paths.get(inputMethodTokensPath).toString()));
            line = "";
            while ((line = bfMethodToken.readLine()) != null ) {
                String[] lineSplitted = line.split("@#@");
                methodsWithTokensSet.add(lineSplitted[0]);
                // }
            }
            System.out.println("methodsWithTokensSet.size "+methodsWithTokensSet.size());
            PrintWriter printWriterMissingBlocks=new PrintWriter(Paths.get(outputMissingBlocksPath).toString());
            PrintWriter printWriterMissingMetrics=new PrintWriter(Paths.get(outputMissingMetricsPath).toString());
            PrintWriter printWriterNewBlocks=new PrintWriter(Paths.get(outputNewBlocksPath).toString());
            PrintWriter printWriterNewMetrics=new PrintWriter(Paths.get(outputNewMetricsPath).toString());

            BufferedReader bfBlocks = new BufferedReader(new FileReader(inputBlocksPath ));
            while ((line = bfBlocks.readLine()) != null) {
                String[] metaSeparated=line.split("@#@");
                String[] metainfo=metaSeparated[0].split(",");
                String fileid=metainfo[0];
                String methodid=metainfo[1];
                //System.out.println(fileid+" "+methodid);
                String fqmnFromIja=ijaMappingBasedOnIds.get(fileid+","+methodid);
                System.out.println(fqmnFromIja);
                if (methodsWithTokensSet.contains(fqmnFromIja)){
                    printWriterNewBlocks.append(line+System.lineSeparator());
                }
                else
                    printWriterMissingBlocks.append(line+System.lineSeparator());
            }

            BufferedReader bfMetrics = new BufferedReader(new FileReader(Paths.get(inputMetricsPath ).toString()));
            while ((line = bfMetrics.readLine()) != null) {
                line=line.replaceAll("\"","");
                String[] lineSplitted=line.split("~~");
                String fqmn=
                        lineSplitted[1] + "." +
                                lineSplitted[2] + "." + lineSplitted[3] + "." + lineSplitted[28];
                if (methodsWithTokensSet.contains(fqmn)) {
                    printWriterNewMetrics.append(line+System.lineSeparator());
                }
                else printWriterMissingMetrics.append(fqmn+System.lineSeparator());
            }


            bfMetrics.close();
            printWriterNewMetrics.close();
            printWriterMissingMetrics.close();
            printWriterMissingBlocks.close();
            printWriterNewBlocks.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
