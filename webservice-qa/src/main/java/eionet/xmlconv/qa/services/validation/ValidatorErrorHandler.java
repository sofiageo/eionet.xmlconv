package eionet.xmlconv.qa.services.validation;

import eionet.xmlconv.qa.data.ValidateDto;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback methods for validation errors. Store errors in the list of ValidateDto objects.
 *
 * @author Enriko Käsper, TripleDev
 */
public class ValidatorErrorHandler extends DefaultHandler {

    /** List of errors. */
    private List<ValidateDto> errContainer = new ArrayList<ValidateDto>();

    @Override
    public void warning(SAXParseException ex) throws SAXException {
        addError(ValidatorErrorType.WARNING, ex);
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        addError(ValidatorErrorType.ERROR, ex);
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        addError(ValidatorErrorType.FATAL_ERROR, ex);
    }

    /**
     * Create ValidateDto object from the SAX error and add it to the error container.
     * @param type ERROR, WARNING or FATAL_ERROR
     * @param ex SAXParseException
     */
    private void addError(ValidatorErrorType type, SAXParseException ex) {
        ValidateDto val = new ValidateDto();
        val.setType(type);
        val.setDescription(ex.getMessage());
        val.setColumn(ex.getColumnNumber());
        val.setLine(ex.getLineNumber());

        errContainer.add(val);
    }

    /**
     * Get the list of errors found by XML Schema validator.
     * @return List of ValidateDto objects
     */
    public List<ValidateDto> getErrors() {
        return errContainer;
    }

}
