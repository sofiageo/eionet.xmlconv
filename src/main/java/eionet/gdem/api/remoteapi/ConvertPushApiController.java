package eionet.gdem.api.remoteapi;

import eionet.gdem.exceptions.XMLConvException;
import eionet.gdem.deprecated.ConversionService;
import eionet.gdem.exceptions.XMLResult;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.MultipartFileUpload;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 *
 *
 */
@Controller
@RequestMapping("/convertPush")
public class ConvertPushApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertPushApiController.class);
    private MessageService messageService;

    protected static final String CONVERT_ID_PARAM_NAME = "convert_id";
    /** Binary data of the file. */
    public static final String CONVERT_FILE_PARAM_NAME = "convert_file";
    /** File name or URL of the file original location. */
    public static final String FILE_NAME_PARAM_NAME = "file_name";

    @GetMapping
    public ResponseEntity push(HttpServletRequest request, HttpServletResponse response) throws ServletException, XMLConvException {
        InputStream fileInput = null;
        Map params = null;
        String convertId = null;
        String fileName = null;

        // parse multipart form data
        MultipartFileUpload fu = new MultipartFileUpload(false);
        fu.processMultiPartRequest(request);
        params = fu.getRequestParams();

        // get convert_id parameter
        if (params.containsKey(CONVERT_ID_PARAM_NAME)) {
            convertId = (String) params.get(CONVERT_ID_PARAM_NAME);
        }
        if (Utils.isNullStr(convertId)) {
            throw new XMLConvException(CONVERT_ID_PARAM_NAME + " parameter is missing from request.");
        }

        try {
            // get the file as inputstream from request
            fileInput = fu.getFileAsInputStream(CONVERT_FILE_PARAM_NAME);
            // get file name from parameter, if this is not provided then use real file name from multipart content.
            if (params.containsKey(FILE_NAME_PARAM_NAME)) {
                fileName = (String) params.get(FILE_NAME_PARAM_NAME);
            } else {
                fileName = fu.getFileName(CONVERT_FILE_PARAM_NAME);
            }
            // XXX: Convert to Spring ResponseEntity
            // call ConversionService
            ConversionService cs = new ConversionService();
            // set up the servlet outputstream form converter
            /*cs.setHttpResponse(methodResponse);*/
            // execute conversion
            cs.convertPush(fileInput, convertId, fileName);
        } catch (IOException e) {
            LOGGER.error("Could not retrieve file from request");
        } finally {
            IOUtils.closeQuietly(fileInput);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<XMLResult> handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map params = request.getParameterMap();
        XMLResult xml = new XMLResult();
        xml.setParams(params);
        xml.setMessage(ex.getMessage());
        xml.setUrl("/convertPush");
        return ResponseEntity.badRequest().body(xml);
    }
}
