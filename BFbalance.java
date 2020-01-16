//  BFbalance.java

import java.util.*;
import java.io.*;
import java.security.*;

public class BFbalance{
    protected Vector<String> oRawData, oAdjustedData;
    protected int fromLineNo, toLineNo;
    protected double randomDist=0.5f;
    protected SecureRandom r;

    //  constructor
    public BFbalance(){
        try{
            //  initialize variables
            oRawData = new Vector<String>();
            oAdjustedData = new Vector<String>();
            r = new SecureRandom();

            //  load parameters from config file
            Properties prop = new Properties();
            prop.load(new BufferedReader(new FileReader("balanceConfig.txt")));

            String FileName = prop.getProperty("FileName");
            fromLineNo = Integer.parseInt(prop.getProperty("fromLineNo"));
            toLineNo = Integer.parseInt(prop.getProperty("toLineNo"));
            randomDist = StrictMath.abs(Double.parseDouble(prop.getProperty("randomDist")));

            if(StrictMath.abs(randomDist)>0.5f)
                randomDist = 0.5f;

            //  fetch raw data - gsi-8 format of digital level
            BufferedReader in = new BufferedReader(new FileReader(FileName));
            String sLine = null;
            while((sLine=in.readLine())!=null){
                oRawData.add(sLine);
                oAdjustedData.add(sLine);
            }

            //  check parameters
            if(fromLineNo<1)
                throw new RuntimeException("value of 'fromLineNo' in config file is TOO small!");

            if(toLineNo>oRawData.size())
                throw new RuntimeException("value of 'toLineNo' in config file is TOO large!");

            if(fromLineNo >= toLineNo)
                throw new RuntimeException("value of 'fromLineNo' in config file must smaller than that of 'toLineNo'!");

        }catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }
    }

    public void compute(){
        int index = fromLineNo-1;
        while(index != -1){
            double deltaDist = r.nextDouble() * randomDist - randomDist/2.0f;

            index=scan4BS(oAdjustedData, index);
            if(index!=-1){
                int backsightIndex      = index;
                String sBacksightData   = oAdjustedData.get(backsightIndex);
                StringTokenizer st      = new StringTokenizer(sBacksightData);
                String backsightPointid = st.nextToken();
                double backsightDist    = gsi8_util.getHD(st.nextToken());
                String backsightReading = st.nextToken();

                //  System.out.print((index + 1) + ", ");
                index=scan4FS(oAdjustedData, index);
                if(index!=-1){
                    String sForsightData    = oAdjustedData.get(index);
                    st = new StringTokenizer(sForsightData);
                    String forsightPointid  = st.nextToken();
                    double foresightDist    = gsi8_util.getHD(st.nextToken());
                    String foresightReading = st.nextToken();

                    //  adjusting fore / back sight distances
                    double adjustedDist = (backsightDist + foresightDist)/2.0f;
                    backsightDist = adjustedDist + deltaDist;
                    foresightDist = adjustedDist - deltaDist;

                    sBacksightData = backsightPointid + " " + gsi8_util.getHDWord('8', backsightDist) + " " + backsightReading + " ";
                    oAdjustedData.set(backsightIndex, sBacksightData);
                    sForsightData = forsightPointid + " " + gsi8_util.getHDWord('8', foresightDist) + " " + foresightReading + " ";
                    oAdjustedData.set(index, sForsightData);

                    //  adjusting distance balance in foresight result
                    String foresightResult = oAdjustedData.get(index+1);
                    st = new StringTokenizer(foresightResult);
                    String resultBlockID = st.nextToken();
                    st.nextToken();  //  just consume it
                    String totalDist = st.nextToken();
                    String groundHight = st.nextToken();
                    foresightResult = resultBlockID + " " + gsi8_util.getDistBalWord('8', deltaDist*2.0f) + " " + totalDist + " " + groundHight;
                    oAdjustedData.set(index+1, foresightResult);

                    //  testing output
                    //  System.out.println(sBacksightData);
                    //  System.out.println(sForsightData);
                    //  System.out.println(foresightResult);

                    //  System.out.println(index+1);
                }
            }
        }
    }

    //  scan for next BS
    protected int scan4BS(Vector<String> v, int iFrom){
        int result = -1;
        if(iFrom<0)
            return -1;

        int index = iFrom;

        while(result==-1 && index<toLineNo){
            StringTokenizer st = new StringTokenizer(oAdjustedData.get(index));
            if(st.hasMoreTokens()){
                if((st.nextToken()).startsWith("11")){  //  first word
                    if((st.nextToken()).startsWith("32")){  //  second word
                        String thirdWord = st.nextToken();  //  whether it is BS
                        if(thirdWord.startsWith("331")){
                            result = index;
                        }
                    }
                }
            }

            index++;
        }

        return result;
    }

    //  scan for next FS
    protected int scan4FS(Vector<String> v, int iFrom){
        int result = -1;
        if(iFrom<0)
            return -1;

        int index = iFrom;

        while(result==-1 && index<toLineNo){
            StringTokenizer st = new StringTokenizer(oAdjustedData.get(index));
            if(st.hasMoreTokens()){
                if((st.nextToken()).startsWith("11")){  //  first word
                    if((st.nextToken()).startsWith("32")){  //  second word
                        String thirdWord = st.nextToken();  //  whether it is FS
                        if(thirdWord.startsWith("332")){
                            result = index;
                        }
                    }
                }
            }

            index++;
        }

        return result;
    }

    public void printResult(){
        for(int index=0; index<oAdjustedData.size(); index++){
            System.out.println(oAdjustedData.get(index));
        }
    }

    public static void main(String[] args) throws Exception{
        BFbalance b = new BFbalance();
        b.compute();
        b.printResult();
    }
}
