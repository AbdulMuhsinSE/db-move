package com.grumpyarab.db.migration.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

@Slf4j
public class HashmapRowMapper implements RowMapper<HashMap<String, Object>> {
    @Override
    public HashMap<String, Object> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        HashMap<String, Object> result = new HashMap<>();

        List<String> columnNames = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columnNames.add(rsmd.getColumnLabel(i));
        }

        List<Object> rowData = new ArrayList<>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            rowData.add(rs.getObject(i));
        }

        for (int i = 0; i < columnNames.size(); i++) {
            if (rowData.get(i) != null ) {
                result.put(columnNames.get(i), rowData.get(i));
            }
        }

        return result;
    }
}
