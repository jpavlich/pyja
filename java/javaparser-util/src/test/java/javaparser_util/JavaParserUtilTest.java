package javaparser_util;

import java.io.IOException;
import java.util.List;

import org.jpavlich.JavaParserUtil;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class JavaParserUtilTest {

    @Test
    public void test() throws IOException {
        JavaParserUtil p = new JavaParserUtil();
        String home = System.getProperty("user.home");

        String[] cp = new String[] { home + "/.m2/repository/org/objenesis/objenesis/2.6/objenesis-2.6.jar",
                home + "/.m2/repository/org/webjars/webjars-locator-core/0.41/webjars-locator-core-0.41.jar",
                home + "/.m2/repository/org/hibernate/hibernate-core/5.4.10.Final/hibernate-core-5.4.10.Final.jar",
                home + "/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.5.2/junit-jupiter-engine-5.5.2.jar",
                home + "/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.10.2/jackson-annotations-2.10.2.jar",
                home + "/.m2/repository/com/fasterxml/jackson/module/jackson-module-parameter-names/2.10.2/jackson-module-parameter-names-2.10.2.jar",
                home + "/.m2/repository/org/apache/tomcat/embed/tomcat-embed-websocket/9.0.30/tomcat-embed-websocket-9.0.30.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-data-jpa/2.2.4.RELEASE/spring-boot-starter-data-jpa-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/apache/tomcat/embed/tomcat-embed-core/9.0.30/tomcat-embed-core-9.0.30.jar",
                home + "/.m2/repository/mysql/mysql-connector-java/8.0.19/mysql-connector-java-8.0.19.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-jdbc/2.2.4.RELEASE/spring-boot-starter-jdbc-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/hdrhistogram/HdrHistogram/2.1.11/HdrHistogram-2.1.11.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-logging/2.2.4.RELEASE/spring-boot-starter-logging-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-actuator/2.2.4.RELEASE/spring-boot-actuator-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/webjars/npm/angular__http/2.4.10/angular__http-2.4.10.jar",
                home + "/.m2/repository/org/springframework/data/spring-data-commons/2.2.4.RELEASE/spring-data-commons-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-test/2.2.4.RELEASE/spring-boot-starter-test-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/slf4j/jul-to-slf4j/1.7.30/jul-to-slf4j-1.7.30.jar",
                home + "/.m2/repository/org/hibernate/validator/hibernate-validator/6.0.18.Final/hibernate-validator-6.0.18.Final.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter/2.2.4.RELEASE/spring-boot-starter-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/junit/jupiter/junit-jupiter/5.5.2/junit-jupiter-5.5.2.jar",
                home + "/.m2/repository/org/thymeleaf/thymeleaf-spring5/3.0.11.RELEASE/thymeleaf-spring5-3.0.11.RELEASE.jar",
                home + "/git/spring-petclinic/target/spring-petclinic-2.2.0.BUILD-SNAPSHOT.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-test/2.2.4.RELEASE/spring-boot-test-2.2.4.RELEASE.jar",
                home + "/.m2/repository/com/sun/istack/istack-commons-runtime/3.0.8/istack-commons-runtime-3.0.8.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-json/2.2.4.RELEASE/spring-boot-starter-json-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-validation/2.2.4.RELEASE/spring-boot-starter-validation-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/apache/logging/log4j/log4j-api/2.12.1/log4j-api-2.12.1.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot/2.2.4.RELEASE/spring-boot-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/dom4j/dom4j/2.1.1/dom4j-2.1.1.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-autoconfigure/2.2.4.RELEASE/spring-boot-autoconfigure-2.2.4.RELEASE.jar",
                home + "/.m2/repository/net/minidev/accessors-smart/1.2/accessors-smart-1.2.jar",
                home + "/.m2/repository/net/bytebuddy/byte-buddy/1.10.6/byte-buddy-1.10.6.jar",
                home + "/.m2/repository/org/xmlunit/xmlunit-core/2.6.3/xmlunit-core-2.6.3.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-actuator-autoconfigure/2.2.4.RELEASE/spring-boot-actuator-autoconfigure-2.2.4.RELEASE.jar",
                home + "/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.10.2/jackson-databind-2.10.2.jar",
                home + "/.m2/repository/net/bytebuddy/byte-buddy-agent/1.10.6/byte-buddy-agent-1.10.6.jar",
                home + "/.m2/repository/org/unbescape/unbescape/1.1.6.RELEASE/unbescape-1.1.6.RELEASE.jar",
                home + "/.m2/repository/jakarta/persistence/jakarta.persistence-api/2.2.3/jakarta.persistence-api-2.2.3.jar",
                home + "/.m2/repository/org/mockito/mockito-junit-jupiter/3.1.0/mockito-junit-jupiter-3.1.0.jar",
                home + "/.m2/repository/org/aspectj/aspectjweaver/1.9.5/aspectjweaver-1.9.5.jar",
                home + "/.m2/repository/org/junit/jupiter/junit-jupiter-params/5.5.2/junit-jupiter-params-5.5.2.jar",
                home + "/.m2/repository/org/apache/logging/log4j/log4j-to-slf4j/2.12.1/log4j-to-slf4j-2.12.1.jar",
                home + "/.m2/repository/org/thymeleaf/thymeleaf/3.0.11.RELEASE/thymeleaf-3.0.11.RELEASE.jar",
                home + "/.m2/repository/org/springframework/spring-aspects/5.2.3.RELEASE/spring-aspects-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/springframework/spring-expression/5.2.3.RELEASE/spring-expression-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/springframework/spring-tx/5.2.3.RELEASE/spring-tx-5.2.3.RELEASE.jar",
                home + "/.m2/repository/io/github/classgraph/classgraph/4.8.44/classgraph-4.8.44.jar",
                home + "/.m2/repository/org/webjars/bootstrap/3.3.6/bootstrap-3.3.6.jar",
                home + "/.m2/repository/org/jboss/logging/jboss-logging/3.4.1.Final/jboss-logging-3.4.1.Final.jar",
                home + "/.m2/repository/org/slf4j/slf4j-api/1.7.30/slf4j-api-1.7.30.jar",
                home + "/.m2/repository/org/yaml/snakeyaml/1.25/snakeyaml-1.25.jar",
                home + "/.m2/repository/org/springframework/spring-webmvc/5.2.3.RELEASE/spring-webmvc-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-web/2.2.4.RELEASE/spring-boot-starter-web-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/springframework/spring-context/5.2.3.RELEASE/spring-context-5.2.3.RELEASE.jar",
                home + "/.m2/repository/jakarta/xml/bind/jakarta.xml.bind-api/2.3.2/jakarta.xml.bind-api-2.3.2.jar",
                home + "/.m2/repository/javax/cache/cache-api/1.1.1/cache-api-1.1.1.jar",
                home + "/.m2/repository/org/springframework/spring-beans/5.2.3.RELEASE/spring-beans-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-test-autoconfigure/2.2.4.RELEASE/spring-boot-test-autoconfigure-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/ehcache/ehcache/3.8.1/ehcache-3.8.1.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-tomcat/2.2.4.RELEASE/spring-boot-starter-tomcat-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/jboss/jandex/2.1.1.Final/jandex-2.1.1.Final.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-cache/2.2.4.RELEASE/spring-boot-starter-cache-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/glassfish/jaxb/txw2/2.3.2/txw2-2.3.2.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-devtools/2.2.4.RELEASE/spring-boot-devtools-2.2.4.RELEASE.jar",
                home + "/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.10.2/jackson-datatype-jdk8-2.10.2.jar",
                home + "/.m2/repository/org/webjars/jquery-ui/1.11.4/jquery-ui-1.11.4.jar",
                home + "/.m2/repository/org/hibernate/common/hibernate-commons-annotations/5.1.0.Final/hibernate-commons-annotations-5.1.0.Final.jar",
                home + "/.m2/repository/org/springframework/data/spring-data-jpa/2.2.4.RELEASE/spring-data-jpa-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/ow2/asm/asm/5.0.4/asm-5.0.4.jar",
                home + "/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.10.2/jackson-datatype-jsr310-2.10.2.jar",
                home + "/.m2/repository/jakarta/transaction/jakarta.transaction-api/1.3.3/jakarta.transaction-api-1.3.3.jar",
                home + "/.m2/repository/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar",
                home + "/.m2/repository/org/junit/platform/junit-platform-engine/1.5.2/junit-platform-engine-1.5.2.jar",
                home + "/.m2/repository/org/attoparser/attoparser/2.0.5.RELEASE/attoparser-2.0.5.RELEASE.jar",
                home + "/.m2/repository/org/jvnet/staxex/stax-ex/1.8.1/stax-ex-1.8.1.jar",
                home + "/.m2/repository/org/javassist/javassist/3.24.0-GA/javassist-3.24.0-GA.jar",
                home + "/.m2/repository/org/junit/platform/junit-platform-commons/1.5.2/junit-platform-commons-1.5.2.jar",
                home + "/.m2/repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.jar",
                home + "/.m2/repository/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar",
                home + "/.m2/repository/org/springframework/spring-jdbc/5.2.3.RELEASE/spring-jdbc-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-actuator/2.2.4.RELEASE/spring-boot-starter-actuator-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/webjars/jquery/2.2.4/jquery-2.2.4.jar",
                home + "/.m2/repository/org/springframework/spring-orm/5.2.3.RELEASE/spring-orm-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/hamcrest/hamcrest/2.1/hamcrest-2.1.jar",
                home + "/.m2/repository/com/zaxxer/HikariCP/3.4.2/HikariCP-3.4.2.jar",
                home + "/.m2/repository/com/jayway/jsonpath/json-path/2.4.0/json-path-2.4.0.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-thymeleaf/2.2.4.RELEASE/spring-boot-starter-thymeleaf-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/apiguardian/apiguardian-api/1.1.0/apiguardian-api-1.1.0.jar",
                home + "/.m2/repository/com/vaadin/external/google/android-json/0.0.20131108.vaadin1/android-json-0.0.20131108.vaadin1.jar",
                home + "/.m2/repository/org/springframework/spring-core/5.2.3.RELEASE/spring-core-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/mockito/mockito-core/3.1.0/mockito-core-3.1.0.jar",
                home + "/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar",
                home + "/.m2/repository/jakarta/validation/jakarta.validation-api/2.0.2/jakarta.validation-api-2.0.2.jar",
                home + "/.m2/repository/org/hsqldb/hsqldb/2.5.0/hsqldb-2.5.0.jar",
                home + "/.m2/repository/org/springframework/spring-web/5.2.3.RELEASE/spring-web-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/latencyutils/LatencyUtils/2.0.3/LatencyUtils-2.0.3.jar",
                home + "/.m2/repository/org/glassfish/jaxb/jaxb-runtime/2.3.2/jaxb-runtime-2.3.2.jar",
                home + "/.m2/repository/jakarta/annotation/jakarta.annotation-api/1.3.5/jakarta.annotation-api-1.3.5.jar",
                home + "/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.10.2/jackson-core-2.10.2.jar",
                home + "/.m2/repository/io/micrometer/micrometer-core/1.3.2/micrometer-core-1.3.2.jar",
                home + "/.m2/repository/org/springframework/spring-test/5.2.3.RELEASE/spring-test-5.2.3.RELEASE.jar",
                home + "/.m2/repository/com/fasterxml/classmate/1.5.1/classmate-1.5.1.jar",
                home + "/.m2/repository/com/sun/xml/fastinfoset/FastInfoset/1.2.16/FastInfoset-1.2.16.jar",
                home + "/.m2/repository/org/assertj/assertj-core/3.13.2/assertj-core-3.13.2.jar",
                home + "/.m2/repository/org/apache/tomcat/embed/tomcat-embed-el/9.0.30/tomcat-embed-el-9.0.30.jar",
                home + "/.m2/repository/org/springframework/spring-context-support/5.2.3.RELEASE/spring-context-support-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/springframework/spring-aop/5.2.3.RELEASE/spring-aop-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.5.2/junit-jupiter-api-5.5.2.jar",
                home + "/.m2/repository/net/minidev/json-smart/2.3/json-smart-2.3.jar",
                home + "/.m2/repository/org/springframework/boot/spring-boot-starter-aop/2.2.4.RELEASE/spring-boot-starter-aop-2.2.4.RELEASE.jar",
                home + "/.m2/repository/org/skyscreamer/jsonassert/1.5.0/jsonassert-1.5.0.jar",
                home + "/.m2/repository/jakarta/activation/jakarta.activation-api/1.2.1/jakarta.activation-api-1.2.1.jar",
                home + "/.m2/repository/org/springframework/spring-jcl/5.2.3.RELEASE/spring-jcl-5.2.3.RELEASE.jar",
                home + "/.m2/repository/org/thymeleaf/extras/thymeleaf-extras-java8time/3.0.4.RELEASE/thymeleaf-extras-java8time-3.0.4.RELEASE.jar" };

        p.init(new String[] { home + "/git/spring-petclinic/src/main/java" }, cp);
        List<List<String>> deps = p.getDependencies(); // TODO Assert
        System.out.println(deps);
        // System.out.println("Sources");
        // List<String> sources = p.getSourceClasses();
        // System.out.println(sources);
    }
}
