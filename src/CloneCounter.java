import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

/**
 * Created by Farima on 8/1/2017.
 */
public class CloneCounter {
    public static void main(String[] args) {
        String pathFirstPart= "/scratch/mondego/local/farima/mlcc_related/SourcererCC/clone-detector/NODE_";
        String pathSecondPart= "/output7.0/queryclones_index_WITH_FILTER_2.txt";
        int numclone=0;
        int numnonclone=0;
        for (int i = 1; i <=7 ; i++) {
            String path=pathFirstPart+i+pathSecondPart;

            try {
                BufferedReader bf = new BufferedReader(new FileReader(Paths.get(path).toString()));
                String line="";
                while ((line=bf.readLine())!=null){
                    if (line.split("~~")[2].equals("1")) numclone++;
                    else numnonclone++;
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        System.out.println("clone "+numclone);
        System.out.println("non clone "+numnonclone);
    }
}
