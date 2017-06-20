package eionet.xmlconv.conversions.services.converters;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 *
 *
 */
public interface Converter {
    String convert(Map params);
    String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt);
}
