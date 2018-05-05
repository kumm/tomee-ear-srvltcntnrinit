package org.superbiz;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.application7.ApplicationDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.net.URL;

@RunWith(Arquillian.class)
@RunAsClient
public class ServletContainerInitializerTest {
    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    private URL contextPath;

    public static WebArchive getWarPackage(String warName) {
        return ShrinkWrap
                .create(WebArchive.class, warName + ".war")
                .addAsResource(
                        new StringAsset("org.superbiz.TestContainerInitializer"),
                        "META-INF/services/javax.servlet.ServletContainerInitializer"
                )
                .addPackage(TestContainerInitializer.class.getPackage());
    }

    @Deployment(name = "ear", testable = false)
    public static EnterpriseArchive earDeployment() {
        WebArchive war = getWarPackage("warmodule");
        ApplicationDescriptor app = Descriptors.create(ApplicationDescriptor.class);
        app.version("7");
        app.createModule().getOrCreateWeb().webUri("warmodule.war").contextRoot("warmodule");
        return ShrinkWrap.create(EnterpriseArchive.class)
                .setApplicationXML(new StringAsset(app.exportAsString()))
                .addAsModule(war);
    }

    @Deployment(name = "war", testable = false)
    public static WebArchive warDeployment() {
        return getWarPackage("single-war");
    }

    @Test
    @OperateOnDeployment("ear")
    public void testInEar() {
        open("/warmodule/");
        assertMyClassDisplayed();
    }

    @Test
    @OperateOnDeployment("war")
    public void testInWar() {
        open("");
        assertMyClassDisplayed();
    }

    private void open(String url) {
        webDriver.get(contextPath + url);
    }

    private void assertMyClassDisplayed() {
        String text = webDriver.findElement(By.tagName("body")).getText();
        Assert.assertEquals("classes:[class org.superbiz.MyClass]", text);
    }
}
