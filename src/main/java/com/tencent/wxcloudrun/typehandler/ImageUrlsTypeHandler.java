package com.tencent.wxcloudrun.typehandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 把 relic_info.image_urls 文本读成字符串列表。
 * 空值和空字符串都变成空列表。
 */
public class ImageUrlsTypeHandler implements TypeHandler<List<String>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<List<String>>() {
    };

    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter == null || parameter.isEmpty()) {
            ps.setString(i, "");
            return;
        }
        ps.setString(i, String.join(",", parameter));
    }

    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    static List<String> parse(String raw) {
        List<String> urls = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) {
            return urls;
        }
        String text = raw.trim();
        if (text.startsWith("[")) {
            try {
                List<String> parsed = OBJECT_MAPPER.readValue(text, STRING_LIST);
                if (parsed != null) {
                    for (String item : parsed) {
                        addIfPresent(urls, item);
                    }
                }
                return urls;
            } catch (Exception ignored) {
                urls.clear();
            }
        }
        for (String item : text.split(",")) {
            addIfPresent(urls, item);
        }
        return urls;
    }

    private static void addIfPresent(List<String> urls, String item) {
        if (item != null && !item.trim().isEmpty()) {
            urls.add(item.trim());
        }
    }
}
