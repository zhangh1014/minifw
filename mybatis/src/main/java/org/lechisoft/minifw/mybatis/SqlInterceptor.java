package org.lechisoft.minifw.mybatis;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//@Intercepts({
//        @Signature(method = "query", type = Executor.class, args = { MappedStatement.class, Object.class,
//                RowBounds.class, ResultHandler.class }),
//        @Signature(method = "query", type = Executor.class, args = { MappedStatement.class, Object.class,
//                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }),
//        @Signature(method = "update", type = Executor.class, args = { MappedStatement.class, Object.class }) })
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SqlInterceptor implements Interceptor {

    private String pagingSql = "";

    public SqlInterceptor(String path) {
        MiniLogger.debug("正在创建SQL拦截器...");
        MiniLogger.debug("正在读取属性文件：" + path);
        Properties props = new Properties();
        try {
            props.load(SqlInterceptor.class.getResourceAsStream(path));
            pagingSql = props.getProperty("pagingSql");
            MiniLogger.debug("pagingSql=" + pagingSql);
            MiniLogger.debug("读取属性文件成功");
            MiniLogger.debug("创建SQL拦截器成功");
        } catch (IOException e) {
            props = null;
            MiniLogger.error("读取属性文件发生异常");
        }
    }
    public SqlInterceptor(){
        this("/paging.properties");
    }

    public Object intercept(Invocation invocation) throws Throwable {

        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler routingStatementHandler
                    = (RoutingStatementHandler) invocation.getTarget();
            StatementHandler statementHandler
                    = (StatementHandler) ReflectUtil.getFieldValue(routingStatementHandler, "delegate");

            if (null != statementHandler) {
                // 获取Paging对象
                BoundSql boundSql = statementHandler.getBoundSql();
                if (null != boundSql) {
                    Paging paging = this.findPaging(statementHandler.getBoundSql());

                    if (null != paging && paging.getEnabled()) {

                        // 获取并设置总记录数、总页数
                        Connection connection = (Connection) invocation.getArgs()[0];
                        int totalRecord = this.getTotalRecord(statementHandler, connection, paging);
                        paging.setTotalRecord(totalRecord);

                        int pageSize = paging.getPageSize();
                        int totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1;
                        paging.setTotalPage(totalPage);

                        int gotoPage = paging.getGotoPage();
                        gotoPage = gotoPage < 1 ? 1 : gotoPage;
                        gotoPage = gotoPage > totalPage ? totalPage : gotoPage;
                        paging.setGotoPage(gotoPage);

                        // 分页处理
                        this.pagingHandler(statementHandler, paging);
                    }

                    // 记录SQL
                    MiniLogger.debug("sql:\n" + this.getSql(statementHandler.getBoundSql()));
                }
            }
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties arg0) {
    }


    /**
     * 从查询参数中查找Paging对象
     *
     * @param boundSql boundSql对象
     * @return Paging对象
     */
    private Paging findPaging(BoundSql boundSql) {
        Paging paging = null;
        Object paramObject = boundSql.getParameterObject();
        if (null != paramObject && !this.isBaseType(paramObject)) {
            if (paramObject instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> paramObjectMap = (Map<String, Object>) paramObject;
                for (Map.Entry<String, Object> entry : paramObjectMap.entrySet()) {
                    if (entry.getValue() instanceof Paging) {
                        paging = (Paging) entry.getValue();
                        break;
                    }
                }
            } else {
                Field field = ReflectUtil.getField(paramObject, Paging.class);
                if (null != field) {
                    try {
                        paging = (Paging) field.get(paramObject);
                    } catch (IllegalArgumentException | IllegalAccessException ignored) {
                    }
                }
            }
        }
        return paging;
    }

    private int getTotalRecord(StatementHandler statementHandler, Connection connection, Paging paging)
            throws Throwable {
        int num = 0;

        BoundSql boundSql = statementHandler.getBoundSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().toLowerCase();

        int fromIdx = sql.indexOf("\nfrom\n");
        if (-1 == fromIdx) {
            fromIdx = sql.indexOf(" from ");
        }
        if (-1 == fromIdx) {
            fromIdx = sql.indexOf("\nfrom ");
        }
        if (-1 == fromIdx) {
            fromIdx = sql.indexOf(" from\n");
        }
        if (-1 == fromIdx) {
            fromIdx = sql.indexOf("\tfrom\t");
        }
        if (-1 == fromIdx) {
            fromIdx = sql.indexOf("\tfrom ");
        }
        if (-1 == fromIdx) {
            fromIdx = sql.indexOf(" from\t");
        }
        if (-1 == fromIdx) {
            fromIdx = sql.indexOf("\tfrom\n");
        }
        if (-1 == fromIdx) {
            fromIdx = sql.indexOf("\nfrom\t");
        }
        if (-1 != fromIdx) {
            sql = "SELECT COUNT(1) " + sql.substring(fromIdx);

            PreparedStatement pstmt = connection.prepareStatement(sql);

            // 通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
            MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(statementHandler,
                    "mappedStatement");
            if (null != mappedStatement) {
                Configuration configuration = mappedStatement.getConfiguration();
                Object paramObject = boundSql.getParameterObject();
                BoundSql countBoundSql = new BoundSql(configuration, sql, parameterMappings,
                        paramObject);
                ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, paramObject,
                        countBoundSql);
                parameterHandler.setParameters(pstmt);

                // 记录SQL
                MiniLogger.debug("total record sql:\n" + this.getSql(countBoundSql));

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    num = rs.getInt(1);
                    rs.close();
                }
            }
        }
        return num;
    }

    private void pagingHandler(StatementHandler statementHandler, Paging paging) throws Throwable {

        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();

        int gotoPage = paging.getGotoPage();
        int pageSize = paging.getPageSize();

        int beginRow = (gotoPage - 1) * pageSize + 1;
        int endRow = gotoPage * pageSize;

        int beginIndex = (gotoPage - 1) * pageSize;
        int endIndex = gotoPage * pageSize - 1;
        endIndex = endIndex < 0 ? 0 : endIndex;

        pagingSql = pagingSql.replaceAll("\\$\\{\\n*sql\\n*\\}", sql);
        pagingSql = pagingSql.replaceAll("\\$\\{\\n*beginRow\\n*\\}", String.valueOf(beginRow));
        pagingSql = pagingSql.replaceAll("\\$\\{\\n*endRow\\n*\\}", String.valueOf(endRow));
        pagingSql = pagingSql.replaceAll("\\$\\{\\n*beginIndex\\n*\\}", String.valueOf(beginIndex));
        pagingSql = pagingSql.replaceAll("\\$\\{\\n*endIndex\\n*\\}", String.valueOf(endIndex));
        pagingSql = pagingSql.replaceAll("\\$\\{\\n*pageSize\\n*\\}", String.valueOf(pageSize));

        Field sqlField = boundSql.getClass().getDeclaredField("sql");
        sqlField.setAccessible(true);
        sqlField.set(boundSql, pagingSql);
    }


    /**
     * 获取带参数值的SQL字符串
     *
     * @param boundSql boundSql对象
     * @return SQL字符串
     */
    private String getSql(BoundSql boundSql) {
        String sql = boundSql.getSql();
        Object paramObject = boundSql.getParameterObject();

        // 遍历SQL参数
        List<ParameterMapping> pms = boundSql.getParameterMappings();
        for (ParameterMapping pm : pms) {

            String paramName = pm.getProperty(); // SQL参数名称
            Class<?> paramJavaType = pm.getJavaType(); // SQL参数Java类型

            // 从参ParameterObject中获取参数的值
            Object paramValue = null;
            if (null != paramObject) {
                if (this.isBaseType(paramObject)) {
                    paramValue = paramObject;
                } else if (paramObject instanceof Map) {
                    paramValue = ((Map) paramObject).get(paramName);
                } else {
                    paramValue = ReflectUtil.getFieldValue(paramObject, paramName);
                }
            }

            // 替换SQL中参数的占位符
            paramValue = null == paramValue ? "" : paramValue;
            paramValue = paramJavaType.equals(String.class) ? "'" + paramValue + "'" : paramValue;
            sql = sql.replaceFirst("\\?", paramValue.toString());
        }
        return sql;
    }

    /**
     * 判断是否基本类型
     *
     * @param val 对象
     * @return 是否基本类型
     */
    private boolean isBaseType(Object val) {
        return val instanceof String || val instanceof Integer || val instanceof Byte || val instanceof Long
                || val instanceof Double || val instanceof Float || val instanceof Character || val instanceof Short
                || val instanceof BigDecimal || val instanceof BigInteger || val instanceof Boolean
                || val instanceof Date;
    }
}
