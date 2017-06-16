package eionet.xmlconv.qa.services.basex;

import org.basex.BaseXServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 */
@Service
public class BaseXService {

    private BaseXServer baseXServer;

    @Autowired
    public BaseXService(BaseXServer basexServer) {
        this.baseXServer = basexServer;
    }

    public String status() {
        return baseXServer.usage();
    }
}
