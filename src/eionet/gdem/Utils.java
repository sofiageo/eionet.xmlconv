package eionet.gdem;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import java.net.URL;
import java.io.*;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class Utils {

  //constants:
  //XQuery job statuses:
  public static final int XQ_RECEIVED=0;
  public static final int XQ_DONWLOADING_SRC=1;
  public static final int XQ_PROCESSING=2;
  public static final int XQ_READY=3;
  public static final int XQ_PULLED=4;    

  public static final int XQ_ERROR=9;  
  
  public static String tmpFolder="/tmp";

  public static String urlPrefix="http://conversions.eionet.eu.int/";
  
  public static String xslFolder="/xsl/";

  //Database settings from the properties file
  public static String dbUrl=null;
  public static String dbDriver=null;
  public static String dbUser=null;
  public static String dbPwd=null;

  //period for checking new jobs in the workqueue in milliseconds, default 20sec
  public static long wqCheckInterval=20000L;
  
  public static final String XQ_SOURCE_PARAM_NAME="source_url";
  private static ResourceBundle props;
  private static Category logger;

  static {
    if(logger == null)
      logger = Category.getInstance("gdem");
      
    if (props==null) {
      props=ResourceBundle.getBundle("gdem");
      try {
        tmpFolder=props.getString("tmp.folder");
        xslFolder=props.getString("xsl.folder");

        //DB connection settings
        dbDriver=props.getString("db.driver");
        dbUrl=props.getString("db.url");
        dbUser=props.getString("db.user");
        dbPwd=props.getString("db.pwd");

        wqCheckInterval= Long.valueOf(props.getString("wq.check.interval")).longValue();
        urlPrefix=props.getString("url.prefix"); //URL where the files can be downloaded
      } catch (MissingResourceException mse) {
        //no error handling?
      }
    }
  }
  /**
  * saving an URL stream to the specified text file
  */
  public static String saveSrcFile(String srcUrl)throws IOException {

     URL url = new URL(srcUrl);      
     InputStream is = url.openStream();

     String fileName=null;
     String tmpFileName=tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";

     File file =new File(tmpFileName);
     FileOutputStream fos=new FileOutputStream(file);
      
      int bufLen = 0;
      byte[] buf = new byte[1024];
      
      while ( (bufLen=is.read( buf ))!= -1 )
        fos.write(buf, 0, bufLen );
     
      fileName=tmpFileName;
      is.close();
      fos.flush(); fos.close();

      return fileName;

  }

  static String saveStrToFile(String str, String extension) throws IOException {
    return saveStrToFile(null, str, extension);
  }
   /**
  * Stores a String in a text file 
  * @param String fileName: 
  * @param String str: text to be stored
  * @param String ext: file extension
  */
  public static String saveStrToFile(String fileName, String str, String extension) throws IOException {

    if (fileName==null)
      fileName=tmpFolder + "gdem_" + System.currentTimeMillis() + "." + extension;
    else
      fileName=fileName+"."+extension;
      
    FileWriter fos = new FileWriter(new File(fileName));
    fos.write(str);
    fos.flush(); fos.close();
    return fileName;
  }

  static String readStrFromFile(String fileName) throws java.io.IOException {

    BufferedReader fis = new BufferedReader(new FileReader(fileName)); 
    StringBuffer s = new StringBuffer();
    String line=null;
    while ((line = fis.readLine()) != null) 
      s.append(line + "\n");
     
      fis.close();
      return s.toString();    
  }


  static void deleteFile(String fName) {
    File f = new File(fName);
    f.delete();

  }

  static void log(Object msg) {
    logger.info(msg);
  }

  public static boolean isNullStr(String s ) {
    if (s==null || s.trim().equals(""))
      return true;
    else
      return false;
  } 
}