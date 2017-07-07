package eionet.gdem.qa.services;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.IQueryDao;
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

    private ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();;
    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();
    private IXQJobDao xqJobDao;

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
