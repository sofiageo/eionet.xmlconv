/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.conversion.excel;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eionet.gdem.conversion.SourceReaderIF;
import eionet.gdem.conversion.excel.reader.ExcelReader;
import eionet.gdem.conversion.excel.writer.ExcelConversionHandler;
import eionet.gdem.conversion.excel.writer.ExcelConversionHandlerIF;

public class ExcelUtils {

  /**
  * returns a valid ExcelConversionHandlerIF
  */
  public static ExcelConversionHandlerIF getExcelConversionHandler(){
    return new ExcelConversionHandler();
  }
  /**
  * returns a valid ExcelStyleIF
  */
  public static ExcelStyleIF getExcelStyle(){
    return new ExcelStyle();
  }
  /**
  * returns a valid ExcelReaderIF
  */
  public static SourceReaderIF getExcelReader(){
    return new ExcelReader(false);
  }
  /**
   * returns a valid ExcelReaderIF
   */
   public static SourceReaderIF getExcel2007Reader(){
     return new ExcelReader(true);
   }


  /*
   * returns true, if inputstream can be opened with MS Excel
   */
  public static boolean isExcelFile(InputStream input){
      try{
          POIFSFileSystem fs = new POIFSFileSystem(input);
          return true;
      }
      catch(Exception e){
          return false;
      }
     finally {
        try {
            if (input != null) input.close();
        } catch (Exception e) {}
     }
  }

  /**
   * determines if stream is Excel 2007 file
   */
  public static boolean isExcel2007File(InputStream input) {
      try{
          OPCPackage p = OPCPackage.open(input);
          Workbook wb = WorkbookFactory.create(p);
          return true;
      }
      catch(Exception e){
          return false;
      }
     finally {
        try {
            if (input != null) input.close();
        } catch (Exception e) {}
     }
  }
}

