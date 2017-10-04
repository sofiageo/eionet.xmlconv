package eionet.gdem.qa.services;

import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.services.db.dao.IXQJobDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Transactional
@Service
public class DatabaseService {

    private final ISchemaDao schemaDao;
    private final IQueryDao queryDao;
    private final IConvTypeDao convTypeDao;
    private final IXQJobDao xqJobDao;

    @Autowired
    public DatabaseService(ISchemaDao schemaDao, IQueryDao queryDao, IConvTypeDao convTypeDao, IXQJobDao xqJobDao) {
        this.schemaDao = schemaDao;
        this.queryDao = queryDao;
        this.convTypeDao = convTypeDao;
        this.xqJobDao = xqJobDao;
    }

    public List<String> listQueries(String schema) {
        return Arrays.asList("");
    }

    public List<String> listQueries() {
        return Arrays.asList("");
    }
}
