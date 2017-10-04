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
 * Contributors:   Nedeljko Pavlovic (ED)
 */

package eionet.gdem.deprecated;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.exceptions.XMLConvException;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Conversion Service Facade. The service is able to execute different conversions that are called through XML/RPC and HTTP POST and
 * GET.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */
@Service
public class ConversionService {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionService.class);
    private final ListConversionsMethod listConversionsMethod;
    private final ConvertXMLMethod convertXMLMethod;

    @Autowired
    public ConversionService(ListConversionsMethod listConversionsMethod, ConvertXMLMethod convertXMLMethod) {
        this.listConversionsMethod = listConversionsMethod;
        this.convertXMLMethod = convertXMLMethod;
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.gdem.conversion.ConversionServiceIF#listConversions()
     */

    public Vector listConversions() throws XMLConvException {
        return listConversions(null);
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.gdem.conversion.ConversionServiceIF#listConversions(java.lang.String)
     */
    public Vector listConversions(String schema) throws XMLConvException {

        Vector v = listConversionsMethod.listConversions(schema);

        return v;
    }

    /**
     * Creates ticket hash table
     * @param sourceURL Source URL
     * @param convertId Convert ID
     * @param username Username
     * @param password Password
     * @return Ticket Hash table
     * @throws XMLConvException If an error occurs.
     */
    public Hashtable convert(String sourceURL, String convertId, String username, String password) throws XMLConvException {

        try {
            //TODO check
            /*setTicket(Utils.getEncodedAuthentication(username, password));
            setTrustedMode(false);*/

            /*setGlobalParameters(convertMethod);*/
            return convertXMLMethod.convert(sourceURL, convertId);

        } catch (Exception ex) {
            LOGGER.error("Error creating ticket ", ex);
            throw new XMLConvException("Error creating ticket", ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.gdem.conversion.ConversionServiceIF#convert(java.lang.String, java.lang.String)
     */
    public Hashtable convert(String sourceURL, String convertId) throws XMLConvException {
        /*if (!isHTTPRequest() && LOGGER.isDebugEnabled()) {*/
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConversionService.convert method called through XML-rpc.");
        }
        /*setGlobalParameters(convertMethod);*/
        return convertXMLMethod.convert(sourceURL, convertId);
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.gdem.conversion.ConversionServiceIF#convertDD_XML(java.lang.String)
     */
    public Hashtable convertDD_XML(String sourceURL) throws XMLConvException {

        /*if (!isHTTPRequest()) {*/
        LOGGER.info("ConversionService.convertDD_XML method called through XML-RPC: " + sourceURL);
        ConvertDDXMLMethod convertDDXMLMethod = new ConvertDDXMLMethod();
        /*setGlobalParameters(convertDDXMLMethod);*/
        ConversionResultDto result = convertDDXMLMethod.convertDD_XML(sourceURL);
        return ConvertDDXMLMethod.convertExcelResult(result);
    }

    public ConversionResultDto convertDD_XML(String sourceURL, boolean split, String sheetName) throws XMLConvException {

        ConversionResultDto result = null;
        ConvertDDXMLMethod convertDDXMLMethod = new ConvertDDXMLMethod();
        /*setGlobalParameters(convertDDXMLMethod);*/
        if (split){
            result = convertDDXMLMethod.convertDD_XML_split(sourceURL, sheetName);
        }
        else{
            result = convertDDXMLMethod.convertDD_XML(sourceURL);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.gdem.conversion.ConversionServiceIF#convertDD_XML_split(java.lang.String, java.lang.String)
     */
    public Hashtable convertDD_XML_split(String sourceURL, String sheetParam) throws XMLConvException {

        LOGGER.info("ConversionService.convertDD_XML_split method called through XML-RPC: " + sourceURL);

        ConvertDDXMLMethod convertDDXMLMethod = new ConvertDDXMLMethod();
        /*setGlobalParameters(convertDDXMLMethod);*/
        ConversionResultDto result = convertDDXMLMethod.convertDD_XML_split(sourceURL, sheetParam);
        return ConvertDDXMLMethod.convertExcelResult(result);
    }

    /**
     * Checks if XML Schema exists
     * @param xmlSchema XML Schema
     * @return True if schema exists
     * @throws XMLConvException If an error occurs.
     */
    public boolean existsXMLSchema(String xmlSchema) throws XMLConvException {
        return listConversionsMethod.existsXMLSchema(xmlSchema);
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.gdem.conversion.ConversionServiceIF#convertPush(byte[],java.lang.String,java.lang.String)
     */
    public Hashtable convertPush(byte[] file, String convertId, String filename) throws XMLConvException {

        /*if (!isHTTPRequest() && LOGGER.isDebugEnabled()) {*/
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConversionService.convertPush method called through XML-rpc.");
        }

        InputStream input = null;

        try {
            input = new ByteArrayInputStream(file);
            /*setGlobalParameters(convertMethod);*/
            return convertXMLMethod.convertPush(input, convertId, filename);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.gdem.conversion.ConversionServiceIF#convertPush(java.lang.String,java.lang.String)
     */
    public Hashtable convertPush(InputStream fileInput, String convertId, String fileName) throws XMLConvException {

       /* setGlobalParameters(convertMethod);*/
        return convertXMLMethod.convertPush(fileInput, convertId, fileName);
    }

    public Vector getXMLSchemas() throws XMLConvException {
        return listConversionsMethod.getXMLSchemas();
    }

    /**
     * Converts result
     * @param dto Result transfer object
     * @return Converted result
     */
    private static Vector<Object> convertResult(ConversionResultDto dto) {
        Vector<Object> result = new Vector<Object>();

        result.add(dto.getStatusCode());
        result.add(dto.getStatusDescription());

        if (dto.getConvertedXmls() != null) {
            for (Map.Entry<String, byte[]> entry : dto.getConvertedXmls().entrySet()) {
                result.add(entry.getKey());
                try {
                    result.add(new String(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
