package eionet.xmlconv.qa.services.basex;

import eionet.xmlconv.qa.Properties;
import eionet.xmlconv.qa.exceptions.XMLConvException;
import eionet.xmlconv.qa.model.QAScript;
import eionet.xmlconv.qa.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.basex.core.Context;
import org.basex.core.MainOptions;
import org.basex.core.cmd.Set;
import org.basex.io.out.ArrayOutput;
import org.basex.io.serial.SerializerOptions;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.isNull;

/**
 * @author George Sofianos
 *
 */
@Service
public class BaseXLocalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseXLocalService.class);
    private Context context;

    @Autowired
    public BaseXLocalService(Context context) {
        this.context = context;
    }

    public String execute(QAScript script) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        execute(script, out);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    private void execute(QAScript script, OutputStream result) throws XMLConvException {
        String scriptFileName = script.getFilename();
        String source_url = StringUtils.defaultIfEmpty(script.getSourceFileUrl(), "");
        String outputType = script.getOutputType();

        /*Context context = new Context();*/
        QueryProcessor proc = null;
        try {
            new Set(MainOptions.INTPARSE, true).execute(context);
            new Set(MainOptions.QUERYPATH, Properties.QUERIES_DIR).execute(context);

            String scriptSource = null;
            if (!Utils.isNullStr(scriptFileName)) {
                try (Reader queryReader = new FileReader(scriptFileName)) {
                    scriptSource = new String(IOUtils.toByteArray(queryReader, "UTF-8"));
                } catch (IOException e) {
                    LOGGER.error("Error while reading XQuery file: " + e);
                    throw new XMLConvException("Error while reading XQuery file: " + scriptFileName + " : " + ExceptionUtils.getStackTrace(e), e);
                }
            }
            proc = new QueryProcessor( scriptSource, context);
            proc.bind("source_url", source_url, "xs:string");

            // same serialization options with saxon
            /*SerializerOptions opts = new SerializerOptions();
            opts.set(SerializerOptions.INDENT, "no");*/
/*
            depends on script

            opts.set(SerializerOptions.ENCODING, DEFAULT_ENCODING);
            if (getOutputType().equals(HTML_CONTENT_TYPE)) {
                opts.set(SerializerOptions.METHOD, HTML_CONTENT_TYPE);
            } else {
                opts.set(SerializerOptions.METHOD, getOutputType());
            }

            if (getOutputType().equals(XML_CONTENT_TYPE)) {
                opts.set(SerializerOptions.OMIT_XML_DECLARATION, "no");
            } else {
                opts.set(SerializerOptions.OMIT_XML_DECLARATION, "yes");
            }*/

            Value res = proc.value();

            ArrayOutput A = res.serialize();
            result.write(A.toArray());
        } catch (QueryException | IOException e) {
            LOGGER.error("Error executing BaseX xquery script : " + e.getMessage(), e);
            throw new XMLConvException(e);
        } finally {
            if (!isNull(proc))  {
                proc.close();
            }
            /*context.close();*/
        }
    }
}
