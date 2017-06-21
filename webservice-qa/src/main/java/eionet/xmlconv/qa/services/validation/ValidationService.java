package eionet.xmlconv.qa.services.validation;


import eionet.xmlconv.qa.data.ValidateDto;
import eionet.xmlconv.qa.exceptions.DCMException;
import eionet.xmlconv.qa.exceptions.XMLConvException;

import java.io.InputStream;
import java.util.List;

/**
 *
 */
public interface ValidationService {

    public String validate(String xml) throws DCMException;
    public String validateSchema(String xml, String schema) throws DCMException;
    public String validateSchema(String sourceUrl, InputStream srcStream, String schema) throws DCMException, XMLConvException;
    public List<ValidateDto> getErrorList();
    public String getWarningMessage();
    public String getOriginalSchema();
    public String getValidatedSchema();
    public String getValidatedSchemaURL();

}
