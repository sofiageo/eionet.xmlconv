package eionet.xmlconv.qa.services.validation;


import eionet.xmlconv.qa.model.ValidateDto;
import eionet.xmlconv.qa.exceptions.DCMException;
import eionet.xmlconv.qa.exceptions.XMLConvException;
import eionet.xmlconv.qa.model.ValidationResult;

import java.io.InputStream;
import java.util.List;

/**
 *
 */
public interface ValidationService {

    public ValidationResult validate(String xml) throws DCMException;
    public ValidationResult validate(String xml, String schema) throws DCMException;
    public ValidationResult validate(String sourceUrl, InputStream srcStream, String schema) throws DCMException, XMLConvException;
    public List<ValidateDto> getErrorList();
    public String getWarningMessage();
    public String getOriginalSchema();
    public String getValidatedSchema();
    public String getValidatedSchemaURL();

}
