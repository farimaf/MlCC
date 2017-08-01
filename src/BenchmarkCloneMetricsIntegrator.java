import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
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
//            File folder = new File(inputMetricsPath);
//            File[] files = folder.listFiles();
//            for (int i = 0; i < files.length; i++) {
//                if (files[i].isFile()&&(files[i].getName().equals("sample.csv")||files[i].getName().equals("default.csv")||files[i].getName().equals("selected.csv"))) {
//                    BufferedReader bfMetrics = new BufferedReader(new FileReader( files[i]));
//                    String line = bfMetrics.readLine();//to ignore header row
//                    while ((line = bfMetrics.readLine()) != null) {
//                        String[] lineSplitted=line.replaceAll("\"","").split("~~");
//                        metricFilesMap.put(lineSplitted[0]+"~~"+lineSplitted[1]+"~~"+lineSplitted[2]+"~~"+lineSplitted[27],
//                                lineSplitted);
//                    }
//                }
//            }
//            System.out.println("Metrics read complete");
//            System.out.println("size:"+metricFilesMap.size());
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
                    System.out.println(methodAtHand);
                    //if (!((methodMetrics.get(i)[1]).equals("1"))) {
                    //String str="default~~JHawkDefaultPackage~~SoDoKu~~AC3(ConstraintSet,Domain)";
                        for (int j = i + 1; j < ijaMappingList.size(); j++) {
                            String[] methodMatchedSpliited=ijaMappingList.get(j).split(":");
                            String[] methodMatchedNameSplitted=methodMatchedSpliited[0].split("\\.");
                            String methodMatched=methodMatchedSpliited[1].split(",")[0]+"~~"+methodMatchedNameSplitted[0]+"~~"+
                                    methodMatchedNameSplitted[1]+"~~"+methodMatchedNameSplitted[2];
                            String[] matchedLines=methodMatchedNameSplitted[1].split(",");
                            if(metricFilesMap.containsKey(methodAtHand) && metricFilesMap.containsKey(methodMatched)) {
                                if (getPercentageDiff(Double.valueOf(metricFilesMap.get(methodAtHand)[6]), Double.valueOf(metricFilesMap.get(methodMatched)[6])) <= 30.00) {

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
        line=line.substring(0,line.length()-1);
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
        output[0]=firstLines;
        output[1]=secondLines;
        for (int i = 2; i <output.length ; i++) {
            if ((i+2)!=18 && (i+2)!=27)
                output[i]=roundTwoDecimal(getPercentageDiff(Double.valueOf(firstLine[i+2]),Double.valueOf(secondLine[i+2]))).toString();
        }
        //       output[3]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[4]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[5]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[6]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[7]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[8]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[9]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[10]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[11]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[12]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[13]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[14]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[15]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[16]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[17]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[18]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[19]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[20]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[21]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[22]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[23]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[24]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[25]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[26]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[27]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[28]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
//        output[29]=getPercentageDiff(Double.valueOf(firstLine[6]),Double.valueOf(secondLine[6])).toString();
        return output;
    }
    }

