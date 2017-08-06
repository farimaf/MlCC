import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Farima on 7/31/2017.
 */
public class BenchmarkCloneMetricsIntegrator {
        private static double MIN_NUM_STATEMENT_DIFF=0.3;

        private ArrayList<String[]> metricFilesList=new ArrayList<>();
        private HashMap<String,String> ijaMapping =new HashMap<>();
        private PrintWriter printWriter;
        private String inputIjaMappingPath=  "./output/IjaMapping_new.txt";
        //private String inputMetricsPath=  "./input/benchmark_jhawk_features/";
        private String inputMetricsPath=  "./new_benchmark/consolidatedMetrics.csv";
        //private String inputClonePath= "./input/clone_pairs/";
        private String outputPath= "./output/benchmark_integrated_new.txt";
        private String sourceFile="";

    public BenchmarkCloneMetricsIntegrator(){
        try {
            System.out.println("enter source file name:");
            Scanner sc=new Scanner(System.in);
            sourceFile=sc.next();
            outputPath="./output/benchmark_integrated_"+sourceFile+".txt";
            sourceFile="./new_benchmark/"+sourceFile;

 //           File folder = new File(inputMetricsPath);
   //         File[] files = folder.listFiles();
//            for (int i = 0; i < files.length; i++) {
//                if (files[i].isFile()&&(files[i].getName().equals("sample.csv"))){//||files[i].getName().equals("default.csv")||files[i].getName().equals("selected.csv"))) {
                    BufferedReader bfMetrics = new BufferedReader(new FileReader(inputMetricsPath ));
                    String line = bfMetrics.readLine();//to ignore header row
                    while ((line = bfMetrics.readLine()) != null) {
                        line=line.replaceAll("\"","");
                        metricFilesList.add(line.split("~~"));
                        //break;
                    }
//                }
 //           }
            metricFilesList.sort(new Comparator<String[]>() {
                @Override
                public int compare(String[] o1, String[] o2) {
                    return Integer.valueOf(o1[7])-Integer.valueOf(o2[7]);
                }
            });
            System.out.println("Metrics read complete");
            System.out.println("size:"+metricFilesList.size());
            BufferedReader bfIjaMapping=new BufferedReader(new FileReader(Paths.get(inputIjaMappingPath).toString()));
            line = "";
            while ((line = bfIjaMapping.readLine()) != null ) {//insert methods having more than 25 tokens
                if (Integer.parseInt(line.split(":")[1].split(",")[4]) > 25) {
                    String[] lineSplitted = line.split(":");
                    ijaMapping.put(lineSplitted[0],lineSplitted[1]);
                }
            }
            System.out.println("ija mapping read complete");
            System.out.println("size:"+ ijaMapping.size());
            printWriter = new PrintWriter(Paths.get(outputPath).toString());
        }
            catch (IOException e){
                e.printStackTrace();
            }
        }

    public static String[] removeNames(String[] input) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i <input.length ; i++) {
            if (i!=19 && i!=28) result.add(input[i]);
        }
        String[] temp=new String[input.length-2];
        return result.toArray(temp);
    }

    public void intergrate(){
        try {
            BufferedReader bfMetrics =new BufferedReader(new FileReader(Paths.get(sourceFile).toString()));
            String line=bfMetrics.readLine();//to ignore header line
            while ((line=bfMetrics.readLine())!=null) {
                String[] lineSplitted = line.replaceAll("\"", "").split("~~");
                int lowerIndex = getLowerIndex(Integer.valueOf(lineSplitted[7]));
                int higherIndex = getHigherIndex(Integer.valueOf(lineSplitted[7]));
                String fqmnAtHand = lineSplitted[1] + "." + lineSplitted[2] + "." + lineSplitted[3] + "." + lineSplitted[28];
                int idAtHand=Integer.valueOf(lineSplitted[0]);
                System.out.println(fqmnAtHand);
                if (ijaMapping.containsKey(fqmnAtHand)) {
                    String[] linesAtHand = ijaMapping.get(fqmnAtHand).split(",");
                    for (int i = lowerIndex; i <= higherIndex; i++) {
                        int idMatched = Integer.valueOf(metricFilesList.get(i)[0]);
                        if (idAtHand < idMatched) {
                            String fqmnMatched = metricFilesList.get(i)[1] + "." + metricFilesList.get(i)[2] + "." + metricFilesList.get(i)[3] + "." + metricFilesList.get(i)[28];
                            if (ijaMapping.containsKey(fqmnMatched)) {
                                String[] linesMatched = ijaMapping.get(fqmnMatched).split(",");
                                writeOnFile(getLineToWrite(linesAtHand[0] + "," + linesAtHand[1] + "," + linesAtHand[2] + "," + linesAtHand[3]
                                        , linesMatched[0] + "," + linesMatched[1] + "," +linesMatched[2]+","+ linesMatched[3]
                                        , lineSplitted, metricFilesList.get(i)));
                            } else continue;
                        }
                    }
                }
                else continue;
            }
            printWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void writeOnFile(String[] lineParams){
        String line="";
        for (int i = 0; i <lineParams.length ; i++) {
            line+=lineParams[i]+"~~";
        }
        line=line.substring(0,line.length()-2);//changed to 2 becasue ~~ has to chars
        try{
            printWriter.append(line+System.lineSeparator());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Double getPercentageDiff(double firstValue,double secondValue){
        return (Math.abs(firstValue-secondValue)/Math.max(firstValue,secondValue))*100;
    }

    private Double roundTwoDecimal(double param){
        return Double.valueOf(Math.round(param*100.0)/100.0);
    }

    private String[] getLineToWrite(String firstLines,String secondLines,String[] firstLine,String[] secondLine){
        String output[]=new String[29];
//        output[0]=firstLine[0]+"."+firstLine[1]+"."+firstLine[2]+"."+firstLine[27];
//        output[1]=secondLine[0]+secondLine[1]+secondLine[2]+secondLine[27];
       // output[2]=isClone?"1":"0";
        String[] line1= removeNames(firstLine);
        String[] line2= removeNames(secondLine);
        output[0]=firstLines;
        output[1]=secondLines;
        for (int i = 2; i <output.length ; i++) {
            output[i]=roundTwoDecimal(getPercentageDiff(Double.valueOf(line1[i+3]),Double.valueOf(line2[i+3]))).toString();
        }
        return output;
    }

    private int getLowerIndex(int numStatements){
        double lowerRange=Math.ceil(numStatements*(1-MIN_NUM_STATEMENT_DIFF));
        if (Double.valueOf(metricFilesList.get(0)[7])<lowerRange) {
            int low = 0;
            int high = metricFilesList.size() - 1;
            int mid;
            int bestLow = -1;
            while (low <= high) {
                mid = (low + high) / 2;
                if (Double.parseDouble(metricFilesList.get(mid)[7]) > lowerRange) {
                    high = mid - 1;
                } else if (Double.parseDouble(metricFilesList.get(mid)[7]) <= lowerRange) {
                    bestLow = mid;
                    low = mid + 1;
                } else {
                    // medians are equal
                    bestLow = mid;
                    break;
                }
            }
            double temp = Double.parseDouble(metricFilesList.get(bestLow)[7]);
            int index = --bestLow;
            while (index > -1 && Double.parseDouble(metricFilesList.get(index)[7]) == temp) {
                index--;
            }
            return ++index;
        }
        return 0;
    }

    private int getHigherIndex(int numStatements){
        double higherRange=Math.floor(numStatements/(1-MIN_NUM_STATEMENT_DIFF));
        if (Double.valueOf(metricFilesList.get(metricFilesList.size()-1)[7])>higherRange) {
            int low = 0;
            int high = metricFilesList.size() - 1;
            int mid = 0;
            int bestHigh = -1;
            while (low <= high && mid <= metricFilesList.size() - 1 && mid >= 0) {
                mid = (low + high) / 2;
                if (Double.parseDouble(metricFilesList.get(mid)[7]) >= higherRange) {
                    bestHigh = mid;
                    high = mid - 1;
                } else if (Double.parseDouble(metricFilesList.get(mid)[7]) < higherRange) {
                    low = mid + 1;
                } else {
                    // medians are equal
                    bestHigh = mid;
                    break;
                }
            }
            double temp = Double.parseDouble(metricFilesList.get(bestHigh)[7]);
            int index = ++bestHigh;
            while (index < metricFilesList.size() && Double.parseDouble(metricFilesList.get(index)[7]) == temp) {
                index++;
            }
            return --index;
        }
        else
            return metricFilesList.size()-1;
    }
    }

