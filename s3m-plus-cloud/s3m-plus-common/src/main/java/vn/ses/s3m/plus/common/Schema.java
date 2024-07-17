package vn.ses.s3m.plus.common;

public class Schema {

    private static final String DEFAULT_SCHEMA = "s3m_plus_customer";

    public static String getSchemas(final Integer customerId) {

        String schemas = DEFAULT_SCHEMA + Constants.ES.UNDERSCORE_CHARACTER + customerId + Constants.ES.DOT_CHARACTER;

        return schemas;
    }

}