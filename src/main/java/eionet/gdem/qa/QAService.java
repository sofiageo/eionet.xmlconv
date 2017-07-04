package eionet.gdem.qa;

import eionet.gdem.qa.engines.FMEQueryEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;

/**
 * Used by QARestService, Remote API, intended to replace XQueryService.class
 * TODO
 */
@Service
public class QAService {

    private FMEQueryEngine fmeQueryEngine;
    private XQueryRestService xQueryRestService;

    @Autowired
    public QAService(FMEQueryEngine fmeQueryEngine, XQueryRestService xQueryRestService) {
        this.fmeQueryEngine = fmeQueryEngine;
        this.xQueryRestService = xQueryRestService;
    }

    public String execute(XQScript script) {
        if (XQScript.SCRIPT_LANG_FME.equals(script.getScriptType())) {
            return fmeQueryEngine.getResult(script);
        }

        return "";
    }

    public void execute(XQScript script, OutputStream out) {
        fmeQueryEngine.getResult(script, out);
    }
}
