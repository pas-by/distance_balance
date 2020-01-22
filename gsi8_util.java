// gsi8_util.java


public class gsi8_util {

  public static int getBlockNum(String dataWord){
    if(!dataWord.startsWith("41") && !(dataWord.startsWith("11") && dataWord.length()==15))
      throw new NumberFormatException("Not a PtID!!!\t" + dataWord);

    StringBuffer sb = new StringBuffer(dataWord);
    
    return Integer.parseInt(sb.substring(2,6));
  }

  public static String getHDWord(char unit, double value){
    StringBuffer sb = new StringBuffer(gsi8_util.getDataWord(unit, value));
    sb.replace(0,2,"32");
    return sb.toString();
  }

  public static String getB1Word(char unit, double value){
    StringBuffer sb = new StringBuffer(gsi8_util.getDataWord(unit, value));
    sb.replace(0,3,"331");
    sb.setCharAt(4,'0');  // 0 - measured; without earth-curvature correction
    return sb.toString();
  }

  public static String getF1Word(char unit, double value){
    StringBuffer sb = new StringBuffer(gsi8_util.getDataWord(unit, value));
    sb.replace(0,3,"332");
    sb.setCharAt(4,'0');  // 0 - measured; without earth-curvature correction
    return sb.toString();
  }

  public static String getISWord(char unit, double value){
    StringBuffer sb = new StringBuffer(gsi8_util.getDataWord(unit, value));
    sb.replace(0,3,"333");
    sb.setCharAt(4,'0');  // 0 - measured; without earth-curvature correction
    return sb.toString();
  }

  //  distance balance '573'
  public static String getDistBalWord(char unit, double value){
    StringBuffer sb = new StringBuffer(gsi8_util.getDataWord(unit, value));
    sb.replace(0,3,"573");
    sb.setCharAt(4,'0');  // 0 - measured; without earth-curvature correction
    return sb.toString();
  }

  public static String getPtIDWord(int blockNum, String ptID){
    if(ptID.length()>8)
      throw new NumberFormatException("station name too long!!!\t" + ptID);
    if(blockNum>9999)
      throw new NumberFormatException("Block number too large!!!\t" + blockNum);

    StringBuffer sb = new StringBuffer("110000+00000000");
    sb.replace(15-ptID.length(),15,ptID);

    String blockNo = String.valueOf(blockNum);
    sb.replace(6-blockNo.length(),6,blockNo);

    return sb.toString();
  }

  public static String getDataWord(char unit, double value){
    if(!(unit=='0' || unit=='6' || unit =='8' || unit =='4'))
      throw new NumberFormatException("Wrong unit!!!\t" + unit);

    StringBuffer sb = new StringBuffer("......+00000000");
    if(value<0){
      sb.setCharAt(6,'-');
      value = -value;
    }

    if(unit=='0'){
      value = value * 1000f;
      sb.setCharAt(5,'0');
      }
    else {
           if(unit=='6'){
             value = value * 10000f;
             sb.setCharAt(5,'6');
           }
           else {
             value = value * 100000f;
             sb.setCharAt(5, unit);
           }
         }

    String measureData = String.valueOf((int)value);
    if(measureData.length()>8)
      throw new NumberFormatException("value too large!!!\t" + measureData);

    sb.replace(15-measureData.length(),15,measureData);
    return sb.toString();
  }

  public static String getID(String dataWord){
    if(dataWord.length()!=15)
      throw new NumberFormatException("Wrong length!!!\t" + dataWord);

    if(!(dataWord.startsWith("11")))
      throw new NumberFormatException("Not a point ID!!!\t" + dataWord);

    StringBuffer sb = new StringBuffer(dataWord.substring(7,15));
    while(sb.charAt(0)=='0')
      sb.deleteCharAt(0);
    return sb.toString();
  }

  public static double getHD(String dataWord){
    if(!(dataWord.startsWith("32")))
      throw new NumberFormatException("Not a Horizontal distance!!!\t" + dataWord);

    return gsi8_util.getValue(dataWord);
  }

  public static double getB1(String dataWord){
    if(!(dataWord.startsWith("331")))
      throw new NumberFormatException("Not a staff reading(B1)!!!\t" + dataWord);

    return gsi8_util.getValue(dataWord);
  }

  public static double getF1(String dataWord){
    if(!(dataWord.startsWith("332")))
      throw new NumberFormatException("Not a staff reading(F1)!!!\t" + dataWord);

    return gsi8_util.getValue(dataWord);
  }

  public static double getIS(String dataWord){
    if(!(dataWord.startsWith("333")))
      throw new NumberFormatException("Not a staff reading(intermediate sight)!!!\t" + dataWord);

    return gsi8_util.getValue(dataWord);
  }

  public static double getValue(String dataWord){
    if(dataWord.length()!=15)
      throw new NumberFormatException("Wrong length!!!\t" + dataWord);

    String sign = dataWord.substring(6,7);
    if(!(sign.equals("+") || sign.equals("-")))
      throw new NumberFormatException("Wrong format -- +/- sign at 7th digit!!!\t" + dataWord);

    String units = dataWord.substring(5,6);
    if(!(units.equals("0") || units.equals("6") || units.equals("8")))
      throw new NumberFormatException("Wrong unit!!!\t" + dataWord);

    double value = Double.valueOf(dataWord.substring(7,15));
    if(units.equals("0"))
      value = value / 1000f;
    else if(units.equals("6"))
           value = value / 10000f;
         else
           value = value / 100000f;
    if(sign.equals("-"))
      value = -value;
    return value;
  }

  //  convert decimal degree to DDD.MMSSssss
  public static double toHMS(double d){
    //  make it a positive value
    d = normalBearing(d);

    int degree = (int)d;
    double minute = (d - degree) * 60.0f;
    double second = (minute - (int)minute) * 60.0f;

    //  initialize return value
    double returnValue = degree + ((int)minute + second/100.0f)/100.0f;

    return returnValue;
  }

  //  convert a minus bearing to that of positive
  public static double normalBearing(double d){
    if(d<0){
        return normalBearing(d + 360.0f);
    }
    return d;
  }
}  //  end of class
