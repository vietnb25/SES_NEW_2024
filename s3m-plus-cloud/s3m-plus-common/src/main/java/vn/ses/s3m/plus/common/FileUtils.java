package vn.ses.s3m.plus.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * FileUtils class, which contains static methods helper methods like loading the given file etc.
 */
public final class FileUtils {

    /** Logging */
    private static Log log = LogFactory.getLog(FileUtils.class);
    private ClassLoader classLoader = null;

    public FileUtils() {
        classLoader = getClass().getClassLoader();
    }

    /**
     * Load property file.
     */
    public static Properties getProperties(String propertyFileName) throws java.io.FileNotFoundException {

        InputStream is = null;

        try {
            // String configPath = System.getProperty(Constants.CONFIG_PATH);
            String configPath = Constants.CONFIG_PATH;
            File file = new File("C:/Works/git/s3m_plus_cloud/s3m-plus-cloud/s3m-plus-common"
                + Constants.SLASH_CHARACTER + configPath + Constants.SLASH_CHARACTER + propertyFileName);
            is = new FileInputStream(file);

            // Load properties
            Properties props = new Properties();
            props.load(is);
            return props;

        } catch (Exception ignore) {
            ignore.printStackTrace();
            throw new FileNotFoundException(propertyFileName + " not found");

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Load XML file.
     */
    public static Document getXML(String xmlFileName) {

        Document doc = null;

        try {
            String configPath = System.getProperty(Constants.CONFIG_PATH);
            File xmlFile = new File(configPath + Constants.SLASH_CHARACTER + xmlFileName);

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(xmlFile);

        } catch (SAXParseException saxPE) {
            log.error("Error occured during parsing XML, line " + saxPE.getLineNumber() + ", uri " + saxPE
                .getSystemId());
            log.error(saxPE.getMessage());

        } catch (SAXException saxE) {
            log.error("SAX error ocurred.");
            log.error(saxE.getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return doc;
    }

    /**
     * Load property file from resource.
     *
     * @param propertyFileName Property file name
     */
    public Properties getSourceProperties(String propertyFileName) throws java.io.FileNotFoundException {

        FileInputStream is = null;

        try {
            is = new FileInputStream(classLoader.getResource(propertyFileName)
                .getFile());

            // Load properties
            Properties props = new Properties();
            props.load(is);
            return props;

        } catch (Exception ignore) {
            ignore.printStackTrace();
            throw new FileNotFoundException(propertyFileName + " not found");

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
