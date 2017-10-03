package eionet.xmlconv.conversions.converters;


import eionet.xmlconv.conversions.ApplicationTestContext;
import eionet.xmlconv.conversions.services.converters.HTMLConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class HTMLConverterIT {

    @Test
    public void conversionTest() throws Exception {
        InputStream xml = this.getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_QA_XML);
        InputStream xsl = this.getClass().getClassLoader().getResourceAsStream("xsl/dummy.xsl");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HTMLConverter converter = new HTMLConverter();
        converter.convert(xml, xsl, out, ".html");
        assertEquals("Expected output: ", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy>1</dummy>", new String(out.toByteArray()));
        xml.close();
        xsl.close();
        out.close();
    }
}