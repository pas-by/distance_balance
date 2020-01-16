// BFDataSet.java

import java.io.*;
import java.util.*;

public class BFDataSet implements Serializable{
  protected DNA03_backsight backsight=null;
  protected DNA03_foresight foresight=null;
  protected Vector<DNA03_InterSight> intersight = new Vector<DNA03_InterSight>();

  public BFDataSet(String measure_block){
    this.backsight = new DNA03_backsight(measure_block);
  }

  public void setForeSight(String measure_block){
    foresight = new DNA03_foresight(measure_block);
  }

  public void setInterSight(String measure_block){
    intersight.add(new DNA03_InterSight(measure_block));
  }
  public void state(){
    if((backsight!=null)&&(foresight!=null))
      System.out.println("OK\t" + intersight.size());
    else
      System.out.println("BAD, at this moment!!!");
  }
}
