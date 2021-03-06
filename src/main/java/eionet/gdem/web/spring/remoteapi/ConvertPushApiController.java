package eionet.gdem.web.spring.remoteapi;

import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

    @RequestMapping
    public ResponseEntity push(@RequestParam(CONVERT_FILE_PARAM_NAME) MultipartFile multipartFile, @RequestParam(value = FILE_NAME_PARAM_NAME, required = false) String fileName,
                               @RequestParam(CONVERT_ID_PARAM_NAME) String convertId, Model model,
                               HttpServletRequest request, HttpServletResponse response) throws ServletException, XMLConvException {
        InputStream fileInput = null;
        Map params = null;

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);

        if (Utils.isNullStr(convertId)) {
            throw new XMLConvException(CONVERT_ID_PARAM_NAME + " parameter is missing from request.");
        }

        try {
            fileInput = multipartFile.getInputStream();

            if (fileName == null) {
                fileName = multipartFile.getOriginalFilename();
            }

            // XXX: Convert to Spring ResponseEntity
            // call ConversionService
            ConversionServiceIF cs = new ConversionService();
            // set up the servlet outputstream form converter
            cs.setHttpResponse(methodResponse);
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
    public void handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        Map params = request.getParameterMap();
        methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage(), "/convertPush", params);
    }
}
