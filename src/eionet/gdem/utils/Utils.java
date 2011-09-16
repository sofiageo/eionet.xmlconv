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
 * Original Code: Kaido Laine (TietoEnator)
 */
package eionet.gdem.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;

/**
 * Several common methods for file handling etc
 */
public class Utils {

    private static Map<Character, String> xmlEscapes = null;

    private static final Logger LOGGER = Logger.getLogger(Utils.class);

    /**
     * saving an URL stream to the specified text file
     */
    public static String saveSrcFile(String srcUrl) throws IOException {

        String fileName = null;
        String tmpFileName = Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";
        InputStream is = null;
        FileOutputStream fos = null;

        try {

            URL url = new URL(srcUrl);
            is = url.openStream();
            File file = new File(tmpFileName);
            fos = new FileOutputStream(file);

            int bufLen = 0;
            byte[] buf = new byte[1024];

            while ((bufLen = is.read(buf)) != -1) {
                fos.write(buf, 0, bufLen);
            }

            fileName = tmpFileName;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                }
            }
        }

        return fileName;

    }

    public static String saveStrToFile(String str, String extension) throws IOException {
        return saveStrToFile(null, str, extension);
    }

    /**
     * Stores a String in a text file
     *
     * @param String
     *            fileName:
     * @param String
     *            str: text to be stored
     * @param String
     *            ext: file extension
     */
    public static String saveStrToFile(String fileName, String str, String extension) throws IOException {

        if (fileName == null) {
            fileName = Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + "." + extension;
        } else {
            if (extension != null) {
                fileName = fileName + "." + extension;
            }
        }

        FileWriter fos = new FileWriter(new File(fileName));
        fos.write(str);
        fos.flush();
        fos.close();
        return fileName;
    }

    public static String readStrFromFile(String fileName) throws java.io.IOException {

        BufferedReader fis = new BufferedReader(new FileReader(fileName));
        StringBuffer s = new StringBuffer();
        String line = null;
        while ((line = fis.readLine()) != null) {
            s.append(line + "\n");
        }

        fis.close();
        return s.toString();
    }

    public static void deleteFile(String fileName) {
        deleteFile(new File(fileName));
    }
    public static void deleteFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            try {
                file.delete();
            } catch (SecurityException e) {
                LOGGER.error("Security exception when trying to delete " + file, e);
            } catch (RuntimeException e) {
                LOGGER.error("Unexpected RuntimeException when trying to delete " + file, e);
            }
        }
    }
    static void log(Object msg) {
        Properties.logger.info(msg);
    }

    public static boolean isNullStr(Object o) {
        if (o == null || !(o instanceof String)) {
            return true;
        }

        return isNullStr((String) o);
    }

    public static boolean isNullStr(String s) {
        if (s == null || s.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNullVector(Vector v) {
        if (v == null) {
            return true;
        } else if (v.size() == 0) {
            return true;
        }

        return false;
    }

    public static boolean isNullList(List l) {
        if (l == null) {
            return true;
        } else if (l.size() == 0) {
            return true;
        }

        return false;
    }

    public static boolean isNullHashtable(Hashtable h) {
        if (h == null) {
            return true;
        } else if (h.isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean isNullHashMap(Map h) {
        if (h == null) {
            return true;
        } else if (h.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the given string is a well-formed URL
     */
    public static boolean isURL(String s) {
        try {
            new URL(s);
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the given string is number
     */
    public static boolean isNum(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * A method for replacing substrings in string
     */
    public static String Replace(String str, String oldStr, String replace) {
        str = (str != null ? str : "");

        StringBuffer buf = new StringBuffer();
        int found = 0;
        int last = 0;

        while ((found = str.indexOf(oldStr, last)) >= 0) {
            buf.append(str.substring(last, found));
            buf.append(replace);
            last = found + oldStr.length();
        }
        buf.append(str.substring(last));
        return buf.toString();
    }

    /**
     * A method for decoding the BASIC auth from request header
     */
    public static String getEncodedUsername(String str) throws java.io.IOException {

        byte[] b_decoded = Base64.decodeBase64(str.getBytes());
        String str_decoded = new String(b_decoded);
        int sep = str_decoded.indexOf(":");
        if (sep > 0) {
            return str_decoded.substring(0, sep);
        } else {
            return null;
        }
    }

    /**
     * A method for decoding the BASIC auth from request header
     */
    public static String getEncodedPwd(String str) throws java.io.IOException {
        byte[] b_decoded = Base64.decodeBase64(str.getBytes());
        String str_decoded = new String(b_decoded);
        int sep = str_decoded.indexOf(":");
        if (sep > 0) {
            return str_decoded.substring(sep + 1);
        } else {
            return null;
        }
    }

    /**
     * A method for encoding the BASIC auth for request header
     */
    public static String getEncodedAuthentication(String user, String pwd) throws java.io.IOException {
        String auth = user + ":" + pwd;
        String ret = new String(Base64.encodeBase64(auth.getBytes()));
        return ret;
    }

    /**
     * A method for escaping apostrophes
     */
    public static String strLiteral(String in) {
        in = (in != null ? in : "");
        StringBuffer ret = new StringBuffer("'");

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '\'') {
                ret.append("''");
            } else {
                ret.append(c);
            }
        }
        ret.append('\'');

        return ret.toString();
    }

    public static String escapeXML(String text) {

        if (text == null) {
            return "";
        }
        if (text.length() == 0) {
            return text;
        }

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            buf.append(escapeXML(i, text));
        }

        return buf.toString();
    }

    public static String escapeXML(int pos, String text) {

        if (xmlEscapes == null) {
            setXmlEscapes();
        }
        Character c = new Character(text.charAt(pos));

        for (String esc : xmlEscapes.values()) {
            if (pos + esc.length() < text.length()) {
                String sub = text.substring(pos, pos + esc.length());
                if (sub.equals(esc)) {
                    return c.toString();
                }
            }
        }

        if (pos + 1 < text.length() && text.charAt(pos + 1) == '#') {
            int semicolonPos = text.indexOf(';', pos + 1);
            if (semicolonPos != -1) {
                String sub = text.substring(pos + 2, semicolonPos);
                if (sub != null) {
                    try {
                        // if the string between # and ; is a number then return true,
                        // because it is most probably an escape sequence
                        if (Integer.parseInt(sub) >= 0) {
                            return c.toString();
                        }
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
        }

        String esc = xmlEscapes.get(c);
        if (esc != null) {
            return esc;
        } else {
            return c.toString();
        }
    }

    private static void setXmlEscapes() {
        xmlEscapes = new HashMap<Character, String>();
        xmlEscapes.put(new Character('&'), "&amp;");
        xmlEscapes.put(new Character('<'), "&lt;");
        xmlEscapes.put(new Character('>'), "&gt;");
        xmlEscapes.put(new Character('"'), "&quot;");
        xmlEscapes.put(new Character('\''), "&apos;");
    }

    /**
     * reads temporary file from dis and returs as a bytearray
     */
    public static byte[] fileToBytes(String fileName) throws GDEMException {

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        try {

            // log("========= open fis " + fileName);
            fis = new FileInputStream(fileName);
            // log("========= fis opened");

            baos = new ByteArrayOutputStream();

            int bufLen = 0;
            byte[] buf = new byte[1024];

            while ((bufLen = fis.read(buf)) != -1) {
                baos.write(buf, 0, bufLen);
            }

        } catch (FileNotFoundException fne) {
            LOGGER.error("File not found " + fileName, fne);
            throw new GDEMException("File not found " + fileName, fne);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new GDEMException("Exception " + e.toString(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {

                }
            }
        }
        return baos.toByteArray();
    }

    public static boolean containsKeyIgnoreCase(Map<String, String> hash, String val) {

        Iterator<String> keysIterator = hash.keySet().iterator();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            if (key.equalsIgnoreCase(val)) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks if list contains any String values or not
     *
     * @param list
     *            The list that will be investigated
     *
     * @return value true, if the list does not contain any String values, otherwise true
     */
    public static boolean isEmptyList(List<String> list) {
        boolean ret = true;
        if (list == null) {
            return ret;
        }
        if (list.size() == 0) {
            return ret;
        }

        for (int i = 0; i < list.size(); i++) {
            String str_value = list.get(i);
            if (!Utils.isNullStr(str_value)) {
                return false;
            }
        }

        return ret;
    }

    /*
     * Creates random name using timestamp
     */
    public static String getRandomName() {

        StringBuffer bufRandName = new StringBuffer(32);
        bufRandName.append(System.currentTimeMillis());
        bufRandName.append(Math.random() * 10000);
        return bufRandName.toString();
    }

    /**
     *
     * @param in
     * @param out
     * @throws Exception
     */
    public static void copyFile(File in, File out) throws Exception {

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(in);
            fos = new FileOutputStream(out);
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /*
     *
     */
    public static void deleteFolder(String folderPath) {

        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            if (files[i].isDirectory()) {
                deleteFolder(files[i].getAbsolutePath());
                continue;
            }
            files[i].delete();
        }

        folder.delete();
    }

    // Returns the contents of the file in a byte array.
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = null;
        byte[] bytes = null;

        try {
            is = new FileInputStream(file);
            // Get the size of the file
            long length = file.length();

            // You cannot create an array using a long type.
            // It needs to be an int type.
            // Before converting to an int type, check
            // to ensure that file is not larger than Integer.MAX_VALUE.
            if (length > Integer.MAX_VALUE) {
                // File is too large
            }

            // Create the byte array to hold the data
            bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }

        } finally {
            // Close the input stream and return bytes
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return bytes;
    }

    /**
     *
     * @param date
     * @return
     */
    public static String getDate(Date date) {
        return getFormat(date, Properties.dateFormatPattern);
    }

    /**
     *
     * @param date
     * @return
     */
    public static String getDateTime(Date date) {
        return getFormat(date, Properties.timeFormatPattern);
    }

    /**
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String getFormat(Date date, String pattern) {

        if (date == null) {
            return null;
        }

        SimpleDateFormat formatter = null;
        if (pattern == null) {
            formatter = new SimpleDateFormat();
        } else {
            formatter = new SimpleDateFormat(pattern);
        }

        return formatter.format(date);
    }

    /**
     * parses String to Date
     *
     * @param String
     *            date
     * @param pattern
     * @return Date object
     * @throws ParseException
     */
    public static Date parseDate(String srtDate, String pattern) throws ParseException {

        if (isNullStr(srtDate)) {
            return null;
        }

        SimpleDateFormat formatter = null;
        if (pattern == null) {
            formatter = new SimpleDateFormat();
        } else {
            formatter = new SimpleDateFormat(pattern);
        }

        return formatter.parse(srtDate);
    }

    /**
     * formats timestamp (millis from 1 Jan 1970) into string using pattern
     *
     * @param String
     *            timestamp
     * @param pattern
     * @return Date object
     * @throws ParseException
     */
    public static String formatTimestampDate(String timestamp) {

        if (timestamp == null) {
            return null;
        }

        long l = 0;
        try {
            l = Long.parseLong(timestamp);
        } catch (Exception e) {
            return null;
        }

        Date d = new Date(l);
        return Utils.getDate(d);
    }

    /**
     * Generates checksum (MD5) value from filepath
     *
     * @param filename
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getChecksumFromFile(String filename) throws NoSuchAlgorithmException, IOException {

        FileInputStream in = new FileInputStream(filename);
        try {
            return getChecksumValue(in);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
            ;
        }

    }

    /**
     * Generates checksum (MD5) value from string value
     *
     * @param src
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getChecksumFromString(String src) throws NoSuchAlgorithmException, IOException {

        ByteArrayInputStream in = new ByteArrayInputStream(src.getBytes());
        try {
            return getChecksumValue(in);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
            ;
        }
    }

    /**
     * Greneretes checksum value from given inputsource
     *
     * @param is
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getChecksumValue(InputStream is) throws IOException, NoSuchAlgorithmException {

        StringBuffer out = new StringBuffer();

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = is.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        byte[] chk1 = complete.digest();
        for (int i = 0; i < chk1.length; i++) {
            out.append(Integer.toString((chk1[i] & 0xff) + 0x100, 16).substring(1));
        }
        return out.toString();
    }

    public static String stringArray2String(String[] arr, String sep) {

        if (arr == null || arr.length == 0) {
            return null;
        }
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            buf.append(arr[i]);
            if (i < arr.length - 1) {
                buf.append(sep);
            }
        }

        return buf.toString();
    }

    /**
     * Method constructs a URI from specified file and folder path. If the file or folder does not exists, then it return null
     * value.
     *
     * @param strPath
     *            Folder path. eg: /usr/prj/xmlconv/xmlfiles
     * @param isDirectory
     *            return URI only, if the path is directory
     * @return URI: file:///usr/prj/xmlconv/xmlfiles
     */
    public static String getURIfromPath(String strPath, boolean isDirectory) {

        if (strPath != null) {
            File f = new File(strPath);
            if (f.exists() && ((isDirectory && f.isDirectory()) || !isDirectory)) {
                return f.toURI().toString();
            }
        }
        return null;
    }

    /**
     * generates unique temporary file name with full path
     *
     * @param fileName
     * @return
     */
    public static String getUniqueTmpFileName(String fileName) {
        StringBuffer buf = new StringBuffer();
        buf.append(Properties.tmpFolder);
        buf.append("xmlconv_");
        buf.append(System.currentTimeMillis());
        if (fileName != null) {
            buf.append(fileName);
        } else {
            buf.append(".tmp");
        }
        return buf.toString();
    }

    /**
     * generates unique temporary folder name and creates the directory
     *
     * @return fill path
     */
    public static String createUniqueTmpFolder() {

        StringBuffer buf = new StringBuffer("tmp_");
        buf.append(System.currentTimeMillis());

        String folderName = buf.toString();
        String parent_folder = Properties.tmpFolder;

        int n = 0;
        File folder = new File(parent_folder, folderName);
        while (folder.exists()) {
            n++;
            folderName = getGeneratedFolderName(folderName, n);
            folder = new File(parent_folder, folderName);
        }
        folder.mkdir();

        return folder.getPath();
    }

    /**
     * Generates foldername
     *
     * @param folderName
     *            , n >0, if folder with the same name already exists in the tmp folder ex: getGeneratedFolderName( test, 1 )=
     *            test_1 getGeneratedFolderName( test_1, 2 )= test_2
     */
    public static String getGeneratedFolderName(String folderName, int n) {
        String ret;

        int dashPos = folderName.lastIndexOf("_");
        if (dashPos > 1) {
            String snum = folderName.substring(dashPos + 1);
            try {
                int inum = Integer.parseInt(snum);
                ret = folderName.substring(0, dashPos) + "_" + (inum + 1);
            } catch (Exception e) {
                ret = folderName + "_" + n;
            }
        } else {
            ret = folderName + "_" + n;
        }

        return ret;
    }

    /**
     * deletes the folder, where specified file locates
     *
     * @param filePath
     */
    public static void deleteParentFolder(String filePath) {

        File file = new File(filePath);
        String folder = file.getParent();

        // check if the folder is not Properties.tmpFolder
        File oFolder = new File(folder);
        File oTmpFolder = new File(Properties.tmpFolder);

        // if parent folder is system tmp folder, then delete only the specifieds file
        if (oFolder.equals(oTmpFolder)) {
            deleteFile(filePath);
        } else {
            deleteFolder(folder);
        }
    }

    /**
     * Find the first XML file stored in specified folder
     *
     * @param folder
     *            folder path
     * @return
     */
    public static String findXMLFromFolder(String folder) {
        return findXMLFromFolder(new File(folder));
    }

    /**
     * Find the first XML file stored in specified folder
     *
     * @param folder
     *            File object
     * @return
     */
    public static String findXMLFromFolder(File folder) {

        File[] files = folder.listFiles();
        // go through all the files and check well formedness
        for (int i = 0; files != null && i < files.length; i++) {
            if (!files[i].isDirectory()) {
                try {
                    IXmlCtx x = new XmlContext();
                    x.setWellFormednessChecking();
                    x.checkFromFile(files[i].getAbsolutePath());
                    // XML file found
                    return files[i].getAbsolutePath();
                } catch (Exception e) {
                    // it is not an XML
                }
            }
        }
        // go through all subfolders
        for (int i = 0; files != null && i < files.length; i++) {
            if (files[i].isDirectory()) {
                String xmlFile = findXMLFromFolder(files[i].getAbsolutePath());
                if (xmlFile != null) {
                    return xmlFile;
                }
            }
        }

        return null;

    }

    /**
     *
     * @param s
     * @return
     */
    public static String md5digest(String s) throws Exception {
        return Utils.digest(s, "md5");
    }

    /**
     *
     * @param src
     * @param algorithm
     * @return
     * @throws Exception
     */
    public static String digest(String src, String algorithm) throws Exception {

        byte[] srcBytes = src.getBytes();
        return Utils.digest(srcBytes, algorithm);
    }

    /**
     *
     * @param srcBytes
     * @param algorithm
     * @return
     * @throws Exception
     */
    public static String digest(byte[] srcBytes, String algorithm) throws Exception {

        byte[] dstBytes = new byte[16];

        MessageDigest md;

        md = MessageDigest.getInstance(algorithm);

        md.update(srcBytes);
        dstBytes = md.digest();
        md.reset();

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < dstBytes.length; i++) {
            Byte byteWrapper = new Byte(dstBytes[i]);
            int k = byteWrapper.intValue();
            String s = Integer.toHexString(k);
            if (s.length() == 1) {
                s = "0" + s;
            }
            buf.append(s.substring(s.length() - 2));
        }

        return buf.toString();
    }

    /**
     *
     * @param srcBytes
     * @param algorithm
     * @return
     * @throws Exception
     */
    public static String digest(File f, String algorithm) throws Exception {

        byte[] dstBytes = new byte[16];

        MessageDigest md;

        md = MessageDigest.getInstance(algorithm);

        BufferedInputStream in = null;

        int theByte = 0;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            while ((theByte = in.read()) != -1) {
                md.update((byte) theByte);
            }
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
        dstBytes = md.digest();
        md.reset();

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < dstBytes.length; i++) {
            Byte byteWrapper = new Byte(dstBytes[i]);
            int k = byteWrapper.intValue();
            String s = Integer.toHexString(k);
            if (s.length() == 1) {
                s = "0" + s;
            }
            buf.append(s.substring(s.length() - 2));
        }

        return buf.toString();
    }

    /**
     * Extracts file extension from filename
     */
    public static String extractExtension(String strFilename) {
        return extractExtension(strFilename, "xml");
    }

    public static String extractExtension(String strFilename, String defaultExt) {
        String strExtension = "";
        int index = strFilename.lastIndexOf('.');
        // if the "." is before the 5 chars at the end of file name, then it's not probably a file name
        if (index > strFilename.length() - 5) {
            strExtension = strFilename.substring(index + 1, strFilename.length());
            strExtension = strExtension.toLowerCase();
            return strExtension;
        }
        return defaultExt;
    }

    /**
     * Utility method for checking whether the resource exists. The resource can be web or file system resource that matches the URI
     * with "http", "https" or "file" schemes Returns false, if the resource does not exist
     *
     * @param strUri
     * @return
     */
    public static boolean resourceExists(String strUri) {
        try {
            URI uri = new URI(strUri);
            String scheme = uri.getScheme();
            if (scheme.startsWith("http")) {
                return HttpUtils.urlExists(strUri);
            } else if (scheme.equals("file")) {
                File f = new File(uri.getPath());
                return f.exists();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    /**
     * Escape HTML characters and replace new lines with HTML brake tag.
     *
     * @param s
     * @return escaped string
     */
    public static String escapeHtml(String s) {

        if (!StringUtils.isBlank(s)) {
            s = StringEscapeUtils.escapeHtml4(s);
            s = s.replaceAll("\n", "<br/>");
        }
        return s;
    }
}
