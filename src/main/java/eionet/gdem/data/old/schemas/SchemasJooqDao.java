package eionet.gdem.data.old.schemas;

import eionet.gdem.data.schemata.SchemaDaoImpl;
import eionet.gdem.dto.Schema;
import eionet.gdem.services.db.dao.mysql.SchemaMySqlDao;
import org.jooq.*;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;

import static org.jooq.impl.DSL.*;

/**
 *
 *
 */
public class SchemasJooqDao implements SchemasDao {

    private DSLContext create;

    @Autowired
    public SchemasJooqDao(DSLContext context) {
        this.create = context;
    }


    @Override
    public String schemaUrl(String schemaId) {
        String sql = create
                .select(field(SchemaMySqlDao.UPL_SCHEMA_FLD))
                .from(table(SchemaMySqlDao.SCHEMA_TABLE))
                .where(field(SchemaMySqlDao.SCHEMA_ID_FLD).eq(schemaId))
                .getSQL();
    return "test";
    }
}
