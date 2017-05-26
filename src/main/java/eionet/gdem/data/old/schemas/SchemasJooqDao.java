package eionet.gdem.data.old.schemas;

import eionet.gdem.dto.Schema;
import org.jooq.DAO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 *
 */
public class SchemasJooqDao {

    private DSLContext context;

    @Autowired
    public SchemasJooqDao(DSLContext context) {
        this.context = context;
    }
}
