import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by farima on 7/3/17.
 */
public class CloneMetricsIntegrator {
    
    private ArrayList<String> metricFilesList=new ArrayList<>();
    private PrintWriter printWriter;
    private String inputMetricsPath=  "./output/train/";
    private String inputClonePath= "./input/clone_pairs/";
    private String outputPath= "./output/train_integrated/train.txt";

    public CloneMetricsIntegrator(){
        File folder = new File(inputMetricsPath);
        File[] files=folder.listFiles();
        for (int i = 0; i <files.length ; i++) {
            if (files[i].isFile())
                metricFilesList.add(files[i].getName());
        }

        try{
            printWriter=new PrintWriter(outputPath);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public void intergrate(){

        BufferedReader bfMetrics;
        try {
            for (String fileName : metricFilesList) {
                bfMetrics = new BufferedReader(new FileReader(inputMetricsPath + fileName));

                ArrayList<String[]> methodMetrics=new ArrayList<>();
                String line="";
                while ((line=bfMetrics.readLine())!=null){
                    methodMetrics.add(line.replace("\"","").split(","));
                }

                for (int i = 1; i <methodMetrics.size() ; i++){//start from 1 because of headers
                if (!((methodMetrics.get(i)[1]).equals("1"))) {
                    for (int j = i + 1; j < methodMetrics.size(); j++) {
                        writeOnFile(getLineToWrite(methodMetrics.get(i), methodMetrics.get(j), false));
                    }
                }
                else {
                    BufferedReader bfClones = new BufferedReader(new FileReader(inputClonePath + fileName));
                    HashSet<String> clonesList=new HashSet<>();
                    String lineClone="";
                    while ((lineClone=bfClones.readLine())!=null){
                        clonesList.add(lineClone);
                    }
                    for (int j = i + 1; j < methodMetrics.size(); j++) {
                        boolean isClone=clonesList.contains(methodMetrics.get(i)[0]+","+methodMetrics.get(j)[0])||
                                clonesList.contains(methodMetrics.get(j)[0]+","+methodMetrics.get(i)[0]);
                        writeOnFile(getLineToWrite(methodMetrics.get(i), methodMetrics.get(j), isClone));
                    }
                    clonesList.clear();
                    }
                }
                System.out.println(fileName);
            }
            printWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void writeOnFile(String[] lineParams){
        String line="";
        for (int i = 0; i <lineParams.length ; i++) {
            line+=lineParams[i]+",";
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

    private String[] getLineToWrite(String[] firstLine,String[] secondLine, boolean isClone){
        String output[]=new String[30];
        output[0]=firstLine[0];
        output[1]=secondLine[0];
        output[2]=isClone?"1":"0";
        for (int i = 3; i <output.length ; i++) {
            output[i]=roundTwoDecimal(getPercentageDiff(Double.valueOf(firstLine[i+3]),Double.valueOf(secondLine[i+3]))).toString();
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
