import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Farima on 7/31/2017.
 */
public class BenchmarkCloneMetricsIntegrator {


        private HashMap<String,String[]> metricFilesMap=new HashMap<>();
        private ArrayList<String> ijaMappingList=new ArrayList<>();
        private PrintWriter printWriter;
        private String inputIjaMappingPath=  "./output/IjaMapping.txt";
        private String inputMetricsPath=  "./input/benchmark_jhawk_features/";
        private String inputClonePath= "./input/clone_pairs/";
        private String outputPath= "./output/benchmark_integrated.txt";

    public BenchmarkCloneMetricsIntegrator(){
        try {
            File folder = new File(inputMetricsPath);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()&&(files[i].getName().equals("sample.csv")||files[i].getName().equals("default.csv")||files[i].getName().equals("selected.csv"))) {
                    BufferedReader bfMetrics = new BufferedReader(new FileReader( files[i]));
                    String line = bfMetrics.readLine();//to ignore header row
                    while ((line = bfMetrics.readLine()) != null) {
                        String[] lineSplitted=line.replaceAll("\"","").split("~~");
                        metricFilesMap.put(lineSplitted[0]+"~~"+lineSplitted[1]+"~~"+lineSplitted[2]+"~~"+lineSplitted[27],
                                lineSplitted);
                        //break;
                    }
                }
            }
            System.out.println("Metrics read complete");
            System.out.println("size:"+metricFilesMap.size());
            BufferedReader bfIjaMapping=new BufferedReader(new FileReader(Paths.get(inputIjaMappingPath).toString()));
            String line = "";
            while ((line = bfIjaMapping.readLine()) != null ) {//insert methods having more than 25 tokens
               if(Integer.parseInt(line.split(":")[1].split(",")[4])>25)
                   ijaMappingList.add(line);
            }

            System.out.println("ija mapping read complete");
            System.out.println("size:"+ijaMappingList.size());
            printWriter = new PrintWriter(Paths.get(outputPath).toString());
        }
            catch (IOException e){
                e.printStackTrace();
            }
        }

    public void intergrate(){

//        BufferedReader bfMetrics;
//        try {
//            for (String fileName : metricFilesList) {
//                // read a project file for metrics
//                bfMetrics = new BufferedReader(new FileReader(inputMetricsPath + fileName));
//
//                ArrayList<String[]> methodMetrics=new ArrayList<>();
//                String line="";
//                while ((line=bfMetrics.readLine())!=null){
//                    // read all the rows in the metric file
//                    methodMetrics.add(line.replace("\"","").split(","));
//                }
//                // read all clone pairs for this project
//                HashSet<String> clonesSet = new HashSet<>();
//                try {
//                    BufferedReader bfClones = new BufferedReader(new FileReader(inputClonePath + fileName));
//                    String lineClone="";
//                    while ((lineClone=bfClones.readLine())!=null){
//                        clonesSet.add(lineClone);
//                    }
//
//                }
//                catch (FileNotFoundException e){
//                    System.out.println(fileName+ " not found");
//                    continue;
//                }


                for (int i = 0; i <ijaMappingList.size() ; i++){
                    String[] methodAtHandSpliited=ijaMappingList.get(i).split(":");
                    String[] methodAtHandNameSplitted=methodAtHandSpliited[0].split("\\.");
                    String methodAtHand=methodAtHandSpliited[1].split(",")[0]+"~~"+methodAtHandNameSplitted[0]+"~~"+
                            methodAtHandNameSplitted[1]+"~~"+methodAtHandNameSplitted[2];
                    String[] atHandLines=methodAtHandSpliited[1].split(",");
                    //if (!((methodMetrics.get(i)[1]).equals("1"))) {
                    //String str="default~~JHawkDefaultPackage~~SoDoKu~~AC3(ConstraintSet,Domain)";
                    //methodAtHand=str;
                        for (int j = i + 1; j < ijaMappingList.size(); j++) {
                            String[] methodMatchedSpliited=ijaMappingList.get(j).split(":");
                            String[] methodMatchedNameSplitted=methodMatchedSpliited[0].split("\\.");
                            String methodMatched=methodMatchedSpliited[1].split(",")[0]+"~~"+methodMatchedNameSplitted[0]+"~~"+
                                    methodMatchedNameSplitted[1]+"~~"+methodMatchedNameSplitted[2];
                            String[] matchedLines=methodMatchedSpliited[1].split(",");
                            //methodMatched=str;
                            if(metricFilesMap.containsKey(methodAtHand) && metricFilesMap.containsKey(methodMatched)) {
                                if (getPercentageDiff(Double.valueOf(metricFilesMap.get(methodAtHand)[6]), Double.valueOf(metricFilesMap.get(methodMatched)[6])) <= 30.00) {
                                    System.out.println(methodAtHand);
                                    writeOnFile(getLineToWrite(atHandLines[0]+","+atHandLines[1]+","+atHandLines[2]+","+atHandLines[3]
                                            ,matchedLines[0]+","+matchedLines[1]+","+matchedLines[2]+","+matchedLines[3]
                                            ,metricFilesMap.get(methodAtHand), metricFilesMap.get(methodMatched)));
                                }
                            }
                       // }
//                    }
//                    else {
//                        for (int j = i + 1; j < methodMetrics.size(); j++) {
//                            boolean isClone=clonesSet.contains(methodMetrics.get(i)[0]+","+methodMetrics.get(j)[0])||
//                                    clonesSet.contains(methodMetrics.get(j)[0]+","+methodMetrics.get(i)[0]);
//                            if (getPercentageDiff(Double.valueOf(methodMetrics.get(i)[8]),Double.valueOf(methodMetrics.get(j)[8]))<=30.00)
//                                writeOnFile(getLineToWrite(methodMetrics.get(i), methodMetrics.get(j), isClone));
//                        }
//
//                    }
                }
                //clonesSet.clear();
            }
            printWriter.close();
        //}
//        catch (IOException e){
//            e.printStackTrace();
//        }
    }

    private void writeOnFile(String[] lineParams){
        String line="";
        for (int i = 0; i <lineParams.length ; i++) {
            line+=lineParams[i]+"~~";
        }
        line=line.substring(0,line.length()-2);//changed to 2 becasue ~~ has to chars
        try{
            printWriter.append(line+System.lineSeparator());
            System.out.println(line);
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
            output[i]=roundTwoDecimal(getPercentageDiff(Double.valueOf(line1[i+2]),Double.valueOf(line2[i+2]))).toString();
        }
        return output;
    }

    public static String[] removeNames(String[] input) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i <input.length ; i++) {
            if (i!=18 && i!=27) result.add(input[i]);
        }
        String[] temp=new String[input.length-2];
        return result.toArray(temp);
    }
    }

