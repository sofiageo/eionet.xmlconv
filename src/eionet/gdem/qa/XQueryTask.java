package eionet.gdem.qa;
import eionet.gdem.db.DbModuleIF;
import eionet.gdem.db.DbUtils;
import eionet.gdem.Utils;
import java.sql.SQLException;
import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.List;
import java.util.Iterator;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.QueryProcessor;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.query.QueryResult;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.value.Type;
import net.sf.saxon.xpath.XPathException;

import org.xml.sax.InputSource;
import javax.xml.transform.Source;
import net.sf.saxon.instruct.TerminationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

/**
* A task executing the XQuery task and storing the result of processing
*/
public class XQueryTask extends Thread {

  private static DbModuleIF _db;

  private String _scriptFile;
  private String _resultFile;
  private String _jobId;

  private String _url; //source url for XML

  //int status;

  public XQueryTask(String jobId)  {
    _jobId=jobId;

    try {
      if(_db==null)
        _db = DbUtils.getDbModule();

    } catch (Exception e ) {
      _db=null; //fix me!!!!
    }
    //inits variables from DB where the waiting task is stored
    //URL, XQ_FILE, RESULT_FILE
    initVariables();

    //set MIN priority for this thread->
    setPriority(MIN_PRIORITY);
    
  }

  /**
  * run XQuery: consists of steps:
  * - download the source from URL
  * - run XQuery
  * - store the result in a text file
  */
  public void run() {
    try {
_l("job " + _jobId + " started getting source file.");      

    //Status to DOWNLOADING source:
    _db.changeJobStatus(_jobId, Utils.XQ_DONWLOADING_SRC);
    //read soruce from the URL and store it:
    String srcFile=null;
    try {
      srcFile=Utils.saveSrcFile(_url);
    } catch (Exception e ) {
      handleError(e.toString());
      return;
    }
    //saved ok:
    
    //status to -processing
    _db.changeJobStatus(_jobId, Utils.XQ_PROCESSING);
_l("** job " + _jobId + " processing started");     

    //CHANGE ME TO USE QUERYPROCESSOR
    String xqParam=Utils.XQ_SOURCE_PARAM_NAME + "=" + srcFile;
    //String[] args = {"-o", _resultFile, _scriptFile, xqParam};
 
      try {
_l("** query starts: " + _jobId + " params: " + _resultFile + " " + xqParam);

//FIX ME using main() is not correct and does not handle errors!!
       //net.sf.saxon.Query.main(args);

        runQuery(srcFile, _scriptFile, _resultFile);
        
      } catch (Exception e ) {
        handleError("Error processing XQ:" + e.toString());
        return;
      }

     _db.changeJobStatus(_jobId, Utils.XQ_READY);
_l("** job " + _jobId + " done");     
      //all done, thread stops here, job is waiting for pulling from the client side      
  
      //Thread.sleep(_sleepTime);
_l("End of = " + _jobId);      
    } catch (Exception ee) {
      handleError("Error in thread run():" + ee.toString());
      
    }
  }
  
private static void _l(String s ){
  System.out.println ("=========================================");
  System.out.println (s);
  System.out.println ("=========================================");  

}
  //read data from the DB where it is stored for further processing
  private void initVariables()  {
    try {

      //URL, XQ, RESULT
      String[] jobData = _db.getXQJobData(_jobId);

      if (jobData==null)
          handleError("No such job: " + _jobId);

      _url=jobData[0];          
      _scriptFile=jobData[1];
      _resultFile=jobData[2]; //just a file name file is not created yet

      
      //_resultFile=Utils.tmpFolder + "gdem_" + System.currentTimeMillis() + ".txt";
    } catch (SQLException sqe ){
      handleError("Error getting WQ data from the DB: " + sqe.toString());
    }
  }


  /**
  * Changes the status to ERROR and finishes the thread normally 
  * saves the error message as the result of the job?
  */
  private void handleError(String error) {

    try {
      _db.changeJobStatus(_jobId, Utils.XQ_ERROR);

      //if result file already ok, store the error message in the file:
      if (_resultFile==null) {
        _resultFile= Utils.tmpFolder + "gdem_error" + _jobId;
        //to DB as well?!?
        
      }
      System.out.println(_resultFile);
        
      Utils.saveStrToFile(_resultFile, "<error>" + error + "</error>", "xml");

      //change the name in the DB?
            
    } catch (Exception e) {
      //what to do if exception occurs here...
      System.out.println("=======================================");
      System.out.println(e.toString());
      System.out.println("=======================================");      
    }
  }

  //possible clear temporary files
  private void cleanup() {
  }

  /**
  * excetues
  */

  private void runQuery(String in, String script, String out) {

    boolean wrap=false;
    Source sourceInput = null;
    StringBuffer err_buf = new StringBuffer();
    try{

      Configuration config = new Configuration();
      config.setHostLanguage(config.XQUERY);
      StaticQueryContext staticEnv = new StaticQueryContext();
      staticEnv.setConfiguration(config);
      DynamicQueryContext dynamicEnv = new DynamicQueryContext();
      Properties outputProps = new Properties();
      outputProps.setProperty(OutputKeys.INDENT, "yes");

    //query script
        Reader queryReader = new FileReader(script);
        //FileInputStream fin = new FileInputStream(script);
        //BufferedReader queryReader = new BufferedReader(new InputStreamReader(fin));
        staticEnv.setBaseURI(new File(script).toURI().toString());
    //source file
        File sourceFile = new File(in);
        InputSource eis = new InputSource(sourceFile.toURI().toString());
        sourceInput = new SAXSource(eis);

    //result file
        OutputStream destination = new FileOutputStream(out);

        QueryProcessor xquery = new QueryProcessor(config, staticEnv);
    //compile Query
        XQueryExpression exp;
        try {
          exp = xquery.compileQuery(queryReader);
        } 
        catch (XPathException err) {
          int line = -1;
          if (err.getLocator() != null) {
            line = err.getLocator().getLineNumber();
          }
          if (line == -1) {
            err_buf.append("Failed to compile query: ");
          } else {
            err_buf.append("Syntax error at line " + line + ":");
          }
          throw new TransformerException(err);
        }

        if (sourceInput != null) {
          DocumentInfo doc = xquery.buildDocument(sourceInput);
          dynamicEnv.setContextNode(doc);
        }
        try {
          // The next line actually executes the query
          //List querylist = exp.evaluate(dynamicEnv);
          // now get a java.util iterator
          //System.out.println("Number Nodes =  " +  querylist.size());        
          SequenceIterator results = exp.iterator(dynamicEnv);

          if (wrap) {
            DocumentInfo resultDoc = QueryResult.wrap(results, NamePool.getDefaultNamePool());
            QueryResult.serialize(resultDoc,
                 new StreamResult(destination),
                      outputProps);
            destination.close();
          } else {
            PrintWriter writer = new PrintWriter(destination);
            while (results.hasNext()) {
              Item item = results.next();
              switch (item.getItemType()) {
                case Type.DOCUMENT:
                case Type.ELEMENT:
                  QueryResult.serialize((NodeInfo)item,
                    new StreamResult(writer), outputProps);
                  writer.println("");
                  break;
                default:
                  writer.println(item.getStringValue());
              }
            }
            writer.close();
          }
        }
        catch (TerminationException err) {
          throw err;
        } catch (TransformerException err) {
         // The message will already have been displayed; don't do it twice
          throw new TransformerException("Run-time errors were reported");
        }
        catch (Exception err) {
          err.printStackTrace();
          throw err;
        }
                  
    } catch (Exception e ) {
       err_buf.append("Query processing failed: " + e.toString());
       handleError(err_buf.toString());
   }
  }
}