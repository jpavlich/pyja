package javaparser_util;

import java.io.IOException;
import java.util.List;

import org.jpavlich.JavaParserUtil;
// import org.jpavlich.JavaParserUtil.ClassInfo;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class JavaParserUtilGradleTest {

    JavaParserUtil parser;

    @Before
    public void setup() {
        parser = new JavaParserUtil();

        String home = System.getProperty("user.home");

        String[] cp = new String[] { home
                + "/.gradle/caches/modules-2/files-2.1/org.apache.tomcat.embed/tomcat-embed-core/8.5.35/9c459829e1aa72669203dbbf6648dc3b6314644c/tomcat-embed-core-8.5.35.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-data-redis/1.5.18.RELEASE/1294c4cd42485a0f2028b0824ed74d474afb6f20/spring-boot-starter-data-redis-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-suggest/4.10.4/9a1b5e1974fe61e16d78e8a5b26f802cb2fb9863/lucene-suggest-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-core/4.10.4/cbdb5f686a85e391d9b88f0bae9e018f4d9472ff/lucene-core-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.elasticsearch/elasticsearch/1.7.1/df9e70a4705f71dc6e24f8cf381a1ed82b1b7c31/elasticsearch-1.7.1.jar",
                home + "/.gradle/caches/modules-2/files-2.1/javax.inject/javax.inject/1/6975da39a7040257bd51d21a231b76c915872d38/javax.inject-1.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-expression/4.3.21.RELEASE/e69cc3105026235d4b91cb1c51c25ce125f0b7/spring-expression-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.thymeleaf.extras/thymeleaf-extras-springsecurity4/2.1.3.RELEASE/b01f09fa045944629db1cdcf556bc5ba0bd0a673/thymeleaf-extras-springsecurity4-2.1.3.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.slf4j/log4j-over-slf4j/1.7.25/a87bb47468f47ee7aabbd54f93e133d4215769c3/log4j-over-slf4j-1.7.25.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.thymeleaf.extras/thymeleaf-extras-java8time/2.1.0.RELEASE/e04e378cad4db60acca478954da6a48388201cc1/thymeleaf-extras-java8time-2.1.0.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-data-jpa/1.5.18.RELEASE/e36bc851981a58fb60d24a9c7bd890b1172e40ca/spring-boot-starter-data-jpa-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot/1.5.18.RELEASE/7914ca08dafc83e7013ef1f14f03357114f0e813/spring-boot-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.cloud/spring-cloud-cloudfoundry-connector/1.2.7.RELEASE/a72e45232223998db030061e0eb32b3edb0c576b/spring-cloud-cloudfoundry-connector-1.2.7.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.javassist/javassist/3.21.0-GA/598244f595db5c5fb713731eddbb1c91a58d959b/javassist-3.21.0-GA.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.module/jackson-module-jaxb-annotations/2.8.11/7b52a95f0fed65cb8526d6aa2be090502226c449/jackson-module-jaxb-annotations-2.8.11.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-acl/4.2.10.RELEASE/f9a30005c41373e18d9e0204ecb124c9686ea9d1/spring-security-acl-4.2.10.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/javax.validation/validation-api/1.1.0.Final/8613ae82954779d518631e05daa73a6a954817d5/validation-api-1.1.0.Final.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.xmlbeam/xmlprojector/1.4.8/2654ab54350d5005c6b076c6b464957ba1d145b/xmlprojector-1.4.8.jar",
                home + "/.gradle/caches/modules-2/files-2.1/ch.qos.logback/logback-classic/1.1.11/ccedfbacef4a6515d2983e3f89ed753d5d4fb665/logback-classic-1.1.11.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-tx/4.3.21.RELEASE/9920a4b7629ee949f6ead56a214b532863c02919/spring-tx-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/io.searchbox/jest-common/2.0.4/77626bae6a2b9062d8cbc0dcbb2fc7c297f4739e/jest-common-2.0.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.github.mangstadt/vinnie/2.0.2/c5ac6b631bd5d0fb0a34a14cb51e83283aaff63e/vinnie-2.0.2.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.httpcomponents/httpcore/4.4.10/acc54d9b28bdffe4bbde89ed2e4a1e86b5285e2b/httpcore-4.4.10.jar",
                home + "/.gradle/caches/modules-2/files-2.1/antlr/antlr/2.7.7/83cd2cd674a217ade95a4bb83a8a14f351f48bd0/antlr-2.7.7.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-grouping/4.10.4/bb60a4aa452d2783584c0b2e832564741b88f8f5/lucene-grouping-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/net.minidev/json-smart/2.2.1/5b9e5df7a62d1279b70dc882b041d249c4f0b002/json-smart-2.2.1.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-oxm/4.3.21.RELEASE/b14930c6b3c0b76ede7cad70e9cceb533076090c/spring-oxm-4.3.21.RELEASE.jar",
                home + "/git/sagan/sagan-site/build/libs/sagan-site-1.0.0.BUILD-SNAPSHOT.jar",
                home + "/git/sagan/sagan-site/build/libs/sagan-common-1.0.0.BUILD-SNAPSHOT.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.rometools/rome-utils/1.12.0/c714531d168f733f1ebed7ee8f6ebd255bac52dc/rome-utils-1.12.0.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.thymeleaf/thymeleaf/2.1.6.RELEASE/c362b4ecbeddc86a0cb7e767a88ce27e9c8147e6/thymeleaf-2.1.6.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.thymeleaf/thymeleaf-spring4/2.1.6.RELEASE/a18784dab5e9337a53ce3213b205f927217849f2/thymeleaf-spring4-2.1.6.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.8.5/f645ed69d595b24d4cf8b3fbb64cc505bede8829/gson-2.8.5.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-join/4.10.4/abc55f0284b01a23cf41422f3596bc1fba06701b/lucene-join-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.httpcomponents/httpcore-nio/4.4.4/16badfc2d99db264c486ba8c57ae577301a58bd9/httpcore-nio-4.4.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-thymeleaf/1.5.18.RELEASE/940cd03ce1bb856ca1cf284e0cb7c23cd0c3356a/spring-boot-starter-thymeleaf-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-webmvc/4.3.21.RELEASE/697b5d133fc9293a25caec960fd19c7bd518f56a/spring-webmvc-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.google.guava/guava/18.0/cce0823396aa693798f8882e64213b1772032b09/guava-18.0.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.social/spring-social-config/1.1.6.RELEASE/b28c82bba6a67e2850cbd06360692b11f4e396ed/spring-social-config-1.1.6.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-devtools/1.5.18.RELEASE/5049af66444b18553bc034d7ff13e2767090f5c0/spring-boot-devtools-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.8.0/45b426f7796b741035581a176744d91090e2e6fb/jackson-annotations-2.8.0.jar",
                home + "/.gradle/caches/modules-2/files-2.1/ch.qos.logback/logback-core/1.1.11/88b8df40340eed549fb07e2613879bf6b006704d/logback-core-1.1.11.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.hibernate/hibernate-validator/5.3.6.Final/4c730be8bf55a8e9a61e79a2f2c079abf48b718/hibernate-validator-5.3.6.Final.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-core/4.3.21.RELEASE/baca8fcf544214f08fe594ba092d6fa88900a74e/spring-core-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.tomcat.embed/tomcat-embed-websocket/8.5.35/e87da30db31e2d9e70598a12b40e9c2a23215a40/tomcat-embed-websocket-8.5.35.jar",
                home + "/.gradle/caches/modules-2/files-2.1/redis.clients/jedis/2.9.0/292bc9cc26553acd3cccc26f2f95620bf88a04c2/jedis-2.9.0.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-highlighter/4.10.4/8ceadd928c9d8efef7b069fd850f25db42fbec8b/lucene-highlighter-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/net.sf.biweekly/biweekly/0.6.3/fdb5ff2bed9f75360d897ae5e230354569911a44/biweekly-0.6.3.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.jsoup/jsoup/1.9.2/5e3bda828a80c7a21dfbe2308d1755759c2fd7b4/jsoup-1.9.2.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.data/spring-data-redis/1.8.17.RELEASE/6c74e33bdcd57774eeb1380919c76612d8d0d331/spring-data-redis-1.8.17.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-lang3/3.4/5fe28b9518e58819180a43a850fbc0dd24b7c050/commons-lang3-3.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-web/4.2.10.RELEASE/5a6341dd31d4059342e074543308f81353d7c4f6/spring-security-web-4.2.10.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-orm/4.3.21.RELEASE/b2a0c304b91c64a6bc1890061aa61a56acde8d70/spring-orm-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-queryparser/4.10.4/432d1e1b0aa99695bcaadf49c380c87b17a6f01b/lucene-queryparser-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-databind/2.8.11.3/844df5aba5a1a56e00905b165b12bb34116ee858/jackson-databind-2.8.11.3.jar",
                home + "/.gradle/caches/modules-2/files-2.1/xml-apis/xml-apis/1.4.01/3789d9fada2d3d458c4ba2de349d48780f381ee3/xml-apis-1.4.01.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.ow2.asm/asm/5.0.3/dcc2193db20e19e1feca8b1240dbbc4e190824fa/asm-5.0.3.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-spatial/4.10.4/79ac88a4f91125f47a1a8e28fffae9860e7b3ca6/lucene-spatial-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/javax.xml.stream/stax-api/1.0-2/d6337b0de8b25e53e81b922352fbea9f9f57ba0b/stax-api-1.0-2.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.jboss/jandex/2.0.0.Final/3e899258936f94649c777193e1be846387ed54b3/jandex-2.0.0.Final.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.httpcomponents/httpclient/4.5.6/1afe5621985efe90a92d0fbc9be86271efbe796f/httpclient-4.5.6.jar",
                home + "/.gradle/caches/modules-2/files-2.1/javax.transaction/javax.transaction-api/1.2/d81aff979d603edd90dcd8db2abc1f4ce6479e3e/javax.transaction-api-1.2.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.antlr/antlr-runtime/3.5/baa82bff19059401e90e1b90020beb9c96305d7/antlr-runtime-3.5.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.fasterxml/classmate/1.3.4/3d5f48f10bbe4eb7bd862f10c0583be2e0053c6/classmate-1.3.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/aopalliance/aopalliance/1.0/235ba8b489512805ac13a8f9ea77a1ca5ebe3e8/aopalliance-1.0.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-context-support/4.3.21.RELEASE/1a95108c75e45a92f7e37891d07e432bc7a7265e/spring-context-support-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/dom4j/dom4j/1.6.1/5d3ccc056b6f056dbf0dddfdf43894b9065a8f94/dom4j-1.6.1.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.25/da76ca59f6a57ee3102f8f9bd9cee742973efa8a/slf4j-api-1.7.25.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.slf4j/jul-to-slf4j/1.7.25/af5364cd6679bfffb114f0dec8a157aaa283b76/jul-to-slf4j-1.7.25.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.flywaydb/flyway-core/3.2.1/88347e9a484152e9b80fbad7648d1b552a8cff78/flyway-core-3.2.1.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-jdbc/1.5.18.RELEASE/1c5b51be934704efa1260b4cf27dd6ad5df7205/spring-boot-starter-jdbc-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.data/spring-data-jpa/1.11.17.RELEASE/2b14bc15a9cd4586b1140f40fb75ccb7ee35b082/spring-data-jpa-1.11.17.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-config/4.2.10.RELEASE/459cb4a42a93f393b41b01c87bf76d219fe42b13/spring-security-config-4.2.10.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.codehaus.woodstox/stax2-api/3.1.4/ac19014b1e6a7c08aad07fe114af792676b685b7/stax2-api-3.1.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.hateoas/spring-hateoas/0.23.0.RELEASE/678ffa0798f417a794fea592dc8066e325611919/spring-hateoas-0.23.0.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.httpcomponents/httpasyncclient/4.1.4/f3a3240681faae3fa46b573a4c7e50cec9db0d86/httpasyncclient-4.1.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-aop/4.3.21.RELEASE/e29ad56774c2e8d685b2628a1cd7df217e8e76b9/spring-aop-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.data/spring-data-keyvalue/1.2.17.RELEASE/93e80134d04b106e02b406c6715961ed9c592ee/spring-data-keyvalue-1.2.17.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.hibernate.javax.persistence/hibernate-jpa-2.1-api/1.0.0.Final/5e731d961297e5a07290bfaf3db1fbc8bbbf405a/hibernate-jpa-2.1-api-1.0.0.Final.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.ow2.asm/asm-commons/4.1/f8b86f4ee6e02082f63a658e00eb5506821253c6/asm-commons-4.1.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.dataformat/jackson-dataformat-xml/2.8.11/5e06e2f7ea929b9a7037dc0121e673d95e8b075d/jackson-dataformat-xml-2.8.11.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.social/spring-social-github/1.0.0.M4/a184a059357cbb82ecc66c27795470c4175a1248/spring-social-github-1.0.0.M4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.jboss.logging/jboss-logging/3.3.2.Final/3789d00e859632e6c6206adc0c71625559e6e3b0/jboss-logging-3.3.2.Final.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.hibernate.common/hibernate-commons-annotations/5.0.1.Final/71e1cff3fcb20d3b3af4f3363c3ddb24d33c6879/hibernate-commons-annotations-5.0.1.Final.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.yaml/snakeyaml/1.17/7a27ea250c5130b2922b86dea63cbb1cc10a660c/snakeyaml-1.17.jar",
                home + "/git/sagan/sagan-site/build/libs/sagan-site-1.0.0.BUILD-SNAPSHOT-stubs.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.tuckey/urlrewritefilter/4.0.4/b22c2658a325688bb87903033ae9f041f668aad2/urlrewritefilter-4.0.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-actuator/1.5.18.RELEASE/c4858eedec690e61e547646ce7ae77e6bdc58c1a/spring-boot-starter-actuator-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.rometools/rome/1.12.0/a7057d89b443792277662380cc9f020f1707927b/rome-1.12.0.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.h2database/h2/1.4.197/bb391050048ca8ae3e32451b5a3714ecd3596a46/h2-1.4.197.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.spatial4j/spatial4j/0.4.1/4234d12b1ba4d4b539fb3e29edd948a99539d9eb/spatial4j-0.4.1.jar",
                home + "/.gradle/caches/modules-2/files-2.1/commons-codec/commons-codec/1.10/4b95f4897fa13f2cd904aee711aeafc0c5295cd8/commons-codec-1.10.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.github.mxab.thymeleaf.extras/thymeleaf-extras-data-attribute/1.3/54773015479dfbfbf5050f35af14fbea78771ebf/thymeleaf-extras-data-attribute-1.3.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.jdom/jdom2/2.0.6/6f14738ec2e9dd0011e343717fa624a10f8aab64/jdom2-2.0.6.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.hibernate/hibernate-entitymanager/5.0.12.Final/302a526f5058290e9cbd719a5caf9f248d344719/hibernate-entitymanager-5.0.12.Final.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-core/4.2.10.RELEASE/ad1f0de3a4b61dabc618998df06aa09f9ee82617/spring-security-core-4.2.10.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-security/1.5.18.RELEASE/bb114616a5b74ed4cc8bfe33b4dba58475987b2c/spring-boot-starter-security-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.hibernate/hibernate-core/5.0.12.Final/e58bf1c660e6706d8e2cbb53bae110f574366102/hibernate-core-5.0.12.Final.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.unbescape/unbescape/1.1.0.RELEASE/ab0db4fe0a6fa89fb8da2a40008a4e63a7f3f5b9/unbescape-1.1.0.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.jayway.jsonpath/json-path/2.2.0/22290d17944bd239fabf5ac69005a60a7ecbbbcb/json-path-2.2.0.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.tomcat/tomcat-jdbc/8.5.35/b4b27053b96b70b6e2f3b8de9f5338b0d386386b/tomcat-jdbc-8.5.35.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.social/spring-social-web/1.1.6.RELEASE/4892f8cecac012af6cfe4d5d220667417a7c1b8e/spring-social-web-1.1.6.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.cloud/spring-cloud-core/1.2.7.RELEASE/bc17f69e66731682f15b368b1bbcba1157f7ab43/spring-cloud-core-1.2.7.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-aspects/4.3.21.RELEASE/66207caba507580ee1fef4f8855001074389e62f/spring-aspects-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.codehaus.woodstox/woodstox-core-asl/4.4.1/84fee5eb1a4a1cefe65b6883c73b3fa83be3c1a1/woodstox-core-asl-4.4.1.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-misc/4.10.4/1dea0f279c158875b2374bc03fb7b502b559ea84/lucene-misc-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.slf4j/jcl-over-slf4j/1.7.25/f8c32b13ff142a513eeb5b6330b1588dcb2c0461/jcl-over-slf4j-1.7.25.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-aop/1.5.18.RELEASE/40777b0ebeccaf8afaa7d63b1fab2b6b15622e94/spring-boot-starter-aop-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.cloud/spring-cloud-spring-service-connector/1.2.7.RELEASE/c7dc6db37e5d4f52f0e1eaf0613dd5fcef02938d/spring-cloud-spring-service-connector-1.2.7.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-jdbc/4.3.21.RELEASE/3691a7af0372d73723222c4e0c63db2e58637e8e/spring-jdbc-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/joda-time/joda-time/2.9.9/f7b520c458572890807d143670c9b24f4de90897/joda-time-2.9.9.jar",
                home + "/.gradle/caches/modules-2/files-2.1/nz.net.ultraq.thymeleaf/thymeleaf-layout-dialect/1.4.0/8d7810c069ed1534b9631fb1e85c35973546086/thymeleaf-layout-dialect-1.4.0.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.tomcat/tomcat-annotations-api/8.5.35/5e03d5b26a8cdf7368831d35baa323aaae3213b4/tomcat-annotations-api-8.5.35.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-analyzers-common/4.10.4/438dca3789b04735fe6b223d4aded3561fc5c039/lucene-analyzers-common-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-web/4.3.21.RELEASE/64e4d7c10ce844ea093bee12f0bc9afc563ffa33/spring-web-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.aspectj/aspectjweaver/1.8.13/ad94df2a28d658a40dc27bbaff6a1ce5fbf04e9b/aspectjweaver-1.8.13.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.tomcat/tomcat-juli/8.5.35/69d0606072b31b57ba706d1ffc102064ad8f694b/tomcat-juli-8.5.35.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-beans/4.3.21.RELEASE/b189b059b054acd7ea3d07dc028800d471bb72b6/spring-beans-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-tomcat/1.5.18.RELEASE/a9698317aa43178ae5f09ae015a828f9919a878c/spring-boot-starter-tomcat-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-core/2.8.11/876ead1db19f0c9e79c9789273a3ef8c6fd6c29b/jackson-core-2.8.11.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-queries/4.10.4/f3b55bc9553d622760b585550ffa6dbd082f32f3/lucene-queries-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework/spring-context/4.3.21.RELEASE/5264ce9d8039d48684ce3fc25eb5d214fa46ab5b/spring-context-4.3.21.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.codehaus.groovy/groovy/2.4.15/74b7e0b99526c569e3a59cb84dbcc6204d601ee6/groovy-2.4.15.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.postgresql/postgresql/9.4.1212.jre7/7101612950488be0ff6882bcc27aa0f0a4c202dd/postgresql-9.4.1212.jre7.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-actuator/1.5.18.RELEASE/a970e02f8a2fec07a4ba752065f3e4c653cc4838/spring-boot-actuator-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.tomcat.embed/tomcat-embed-el/8.5.35/da63e21798ee42a4b4019c7345b159d3b63bc3de/tomcat-embed-el-8.5.35.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-pool2/2.4.3/e7ab2a5143cb4e0b21d8ca81c265095e4567dd22/commons-pool2-2.4.3.jar",
                home + "/.gradle/caches/modules-2/files-2.1/io.searchbox/jest/2.0.4/c44c10b78bb133588a4c64a21f5aca5a7d960827/jest-2.0.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.social/spring-social-core/1.1.6.RELEASE/c53ad41ecf1f2ce305aaac43151a272afe7cc509/spring-social-core-1.1.6.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-autoconfigure/1.5.18.RELEASE/7951dd1fe36dea4a64db6c2f7e14f0b1e0adb3ea/spring-boot-autoconfigure-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.data/spring-data-commons/1.13.17.RELEASE/58cf6873526629151d06963a5f9b1c2c6c3ae321/spring-data-commons-1.13.17.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-memory/4.10.4/d80858a3954435b5c731a24945505b99bd5c159c/lucene-memory-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/com.fasterxml.woodstox/woodstox-core/5.0.3/10aa199207fda142eff01cd61c69244877d71770/woodstox-core-5.0.3.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-logging/1.5.18.RELEASE/f60df51b2b14ee57db5340cf230ccd8514407017/spring-boot-starter-logging-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter/1.5.18.RELEASE/f7676e6488e22fcf6c0119120d7364e14fa11dca/spring-boot-starter-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-validation/1.5.18.RELEASE/eb82d99dcad53c4434ac5389918e47a122fa24d/spring-boot-starter-validation-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.plugin/spring-plugin-core/1.2.0.RELEASE/f380e7760032e7d929184f8ad8a33716b75c0657/spring-plugin-core-1.2.0.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.apache.lucene/lucene-sandbox/4.10.4/1600fe003fd6fef68cebca0802c96227501feb99/lucene-sandbox-4.10.4.jar",
                home + "/.gradle/caches/modules-2/files-2.1/ognl/ognl/3.0.8/37e1aebfde7eb7baebc9ad4f85116ef9009c5fc5/ognl-3.0.8.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-hateoas/1.5.18.RELEASE/3e0dfde584f27c9a840a05f0c8d808fbc0052829/spring-boot-starter-hateoas-1.5.18.RELEASE.jar",
                home + "/.gradle/caches/modules-2/files-2.1/net.minidev/accessors-smart/1.1/a527213f2fea112a04c9bdf0ec0264e34104cd08/accessors-smart-1.1.jar",
                home + "/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-web/1.5.18.RELEASE/ac69c498381c10bb5add3c19b517f25c51341886/spring-boot-starter-web-1.5.18.RELEASE.jar", };

        // parser.init(cp);
    }

    // @Test
    // public void getDependencies() throws IOException {
    //     try {
    //         List<List<String>> deps = parser.getDependencies();
    //     } catch (RuntimeException e) {
    //         e.printStackTrace();
    //     }
    //     // System.out.println(deps);
    // }

    // @Test
    // public void getSourceClasses() throws IOException {

    //     try {
    //         List<ClassInfo> deps = parser.getSourceClasses(); // TODO Assert
    //         System.out.println(deps);
    //     } catch (RuntimeException e) {
    //         e.printStackTrace();
    //     }
    // }
}
