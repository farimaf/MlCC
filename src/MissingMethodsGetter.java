import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Farima on 8/17/2017.
 */
public class MissingMethodsGetter {
    public static void main(String[] args) {
        String inputIjaMappingPath=  "/scratch/mondego/local/farima/prep_code/output/IjaMapping_benchmark.txt";
        String inputBlocksPath=  "./blocks.file";
        //private String inputMetricsPath=  "./input/benchmark_jhawk_features/";
        String inputMetricsPath=  "../mlcc_files/consolidatedMetrics.csv";

        //private String inputClonePath= "./input/clone_pairs/";
        String outputMissingBlocksPath= "./output/missed_fqmns_blocks.txt";
        String outputMissingMetricsPath= "./output/missed_fqmns_metrics.txt";
        String outputNewBlocksPath= "./output/Blocks_new.file";
        String outputNewMetricsPath= "./output/consolidatedMetrics_new.csv";
        HashMap<String,String> ijaMapping =new HashMap<>();
        HashSet<String> intersectFqmn=new HashSet<>();
        HashSet<String> intersectIds=new HashSet<>();
        HashSet<String> writtenMetricsFqmns=new HashSet<>();
        try{
            BufferedReader bfIjaMapping=new BufferedReader(new FileReader(Paths.get(inputIjaMappingPath).toString()));
            String line = "";
            while ((line = bfIjaMapping.readLine()) != null ) {//insert methods having more than 50 tokens
               // if (Integer.parseInt(line.split(":")[1].split(",")[4]) > 50) {
                    String[] lineSplitted = line.split(":");
                    ijaMapping.put(lineSplitted[0],lineSplitted[1]);
               // }
            }
            PrintWriter printWriterMissingBlocks=new PrintWriter(Paths.get(outputMissingBlocksPath).toString());
            PrintWriter printWriterMissingMetrics=new PrintWriter(Paths.get(outputMissingMetricsPath).toString());
            PrintWriter printWriterNewBlocks=new PrintWriter(Paths.get(outputNewBlocksPath).toString());
            PrintWriter printWriterNewMetrics=new PrintWriter(Paths.get(outputNewMetricsPath).toString());
            BufferedReader bfMetrics = new BufferedReader(new FileReader(Paths.get(inputMetricsPath ).toString()));
            line = bfMetrics.readLine();//to ignore header row
            while ((line = bfMetrics.readLine()) != null) {
                line=line.replaceAll("\"","");
                String[] lineSplitted=line.split("~~");
                String fqmn=
                        lineSplitted[1] + "." +
                                lineSplitted[2] + "." + lineSplitted[3] + "." + lineSplitted[28];
                System.out.println(fqmn);
                if (ijaMapping.containsKey(fqmn)) {
                    intersectFqmn.add(fqmn);
                    String[] fqmnValues=ijaMapping.get(fqmn).split(",");
                    intersectIds.add(fqmnValues[6]+","+fqmnValues[7]);
                    //printWriterNewMetrics.append(line+System.lineSeparator());
                }
                //else printWriterMissingMetrics.append(fqmn+System.lineSeparator());
            }
            System.out.println("fqmn set size:"+intersectFqmn.size());
            System.out.println("id set size:"+intersectIds.size());

            bfMetrics.close();

            BufferedReader bfBlocks = new BufferedReader(new FileReader(inputBlocksPath ));
            while ((line = bfBlocks.readLine()) != null) {
                String[] metaSeparated=line.split("@#@");
                String[] metainfo=metaSeparated[0].split(",");
                String fileid=metainfo[0];
                String methodid=metainfo[1];
                if (intersectIds.contains(fileid+","+methodid)){
                    printWriterNewBlocks.append(line+System.lineSeparator());

                }
                else
                    printWriterMissingBlocks.append(line+System.lineSeparator());
            }

            bfMetrics = new BufferedReader(new FileReader(Paths.get(inputMetricsPath ).toString()));
            line = bfMetrics.readLine();//to ignore header row
            while ((line = bfMetrics.readLine()) != null) {
                line=line.replaceAll("\"","");
                String[] lineSplitted=line.split("~~");
                String fqmn=
                        lineSplitted[1] + "." +
                                lineSplitted[2] + "." + lineSplitted[3] + "." + lineSplitted[28];
                if (intersectFqmn.contains(fqmn)&& !writtenMetricsFqmns.contains(fqmn)) {
                    printWriterNewMetrics.append(line+System.lineSeparator());
                    writtenMetricsFqmns.add(fqmn);
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
