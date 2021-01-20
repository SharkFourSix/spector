package lib.gintec_rdl.spector;


import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectorTest {

    Logger logger;

    @Before
    public void setupLogging() {
        logger = LoggerFactory.getLogger(SpectorTest.class);

        // load from resource
        Spector.addProviders(new ResourceFileSignatureProvider("spector-test-file-signature.json"));
        Spector.addProviders(new ResourceFileSignatureProvider("spector-wildcard-test-file-signature.json"));
    }

    @Test
    public void testInspection() {
        TypeInfo typeInfo;

        logger.info("Image file inspection test");

        typeInfo = Spector.inspect("spector.test");
        assert typeInfo != null : "Unknown file type";
        assert typeInfo.getExtension().equalsIgnoreCase("test") : "Not a TEST file";
    }

    @Test
    public void testWildcard() {
        TypeInfo typeInfo;

        logger.info("Image file inspection test");

        typeInfo = Spector.inspect("spector.test");
        assert typeInfo != null : "Unknown file type";
        assert typeInfo.getExtension().equalsIgnoreCase("test") : "Not a TEST file";
    }
}
