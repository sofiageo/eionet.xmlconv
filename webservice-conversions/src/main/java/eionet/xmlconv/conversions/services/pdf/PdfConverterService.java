package eionet.xmlconv.conversions.services.pdf;

import eionet.xmlconv.conversions.exceptions.ServiceException;
import eionet.xmlconv.conversions.services.saxon.XsltErrorListener;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.*;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class PdfConverterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfConverterService.class);

    public void pdfConvert(InputStream in, InputStream xsl, OutputStream out) {
        try {
            FopFactory fopFactory = FopFactory.newInstance(new File("fop.xconf"));
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            XsltErrorListener errors = new XsltErrorListener();
            TransformerFactory transformerFactory = new TransformerFactoryImpl();
            StreamSource transformerSource = new StreamSource(xsl);
            Transformer transformer = transformerFactory.newTransformer(transformerSource);
            transformer.setErrorListener(errors);

            /*if (getXslPath() != null) {
                transformerSource.setSystemId(getXslPath());
            }*/
            Result res = new SAXResult(fop.getDefaultHandler());
            Source src = new StreamSource(in);
            transformer.transform(src, res);
            long l = System.currentTimeMillis();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug((new StringBuilder()).append("generate: transformation needed ").append(System.currentTimeMillis()
                        - l).append(" ms").toString());
            }

        } catch (SAXException | IOException | TransformerException e) {
            LOGGER.error("PDF Transformation failed: ", e);
            throw new ServiceException("PDF Transformation failed: ", e);
        }
    }

}