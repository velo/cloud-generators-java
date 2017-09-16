/*
 * MIT License
 *
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.kenticocloud.generator;

import com.kenticocloud.delivery.DeliveryClient;
import com.kenticocloud.delivery.DeliveryOptions;
import com.squareup.javapoet.JavaFile;
import org.apache.http.HttpHost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.localserver.LocalServerTestBase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CodeGeneratorTest extends LocalServerTestBase {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void test() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "types"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentTypeList.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProductionEndpoint(httpHost.toURI() + "/%s");
        deliveryOptions.setProjectId(projectId);
        DeliveryClient client = new DeliveryClient(deliveryOptions);

        File outputDir = temporaryFolder.newFolder();
        Assert.assertTrue(outputDir.exists() && outputDir.isDirectory());
        CodeGenerator codeGenerator =
                new CodeGenerator(projectId,"com.dancinggoat.models", outputDir);
        List<JavaFile> sources = codeGenerator.generateSources(client);
        codeGenerator.writeSources(sources);

        File[] toplevel = outputDir.listFiles();
        Assert.assertEquals(1, toplevel.length);
        File com = toplevel[0];
        Assert.assertEquals("com", com.getName());
        File[] secondlevel = com.listFiles();
        Assert.assertEquals(1, secondlevel.length);
        File dancinggoat = secondlevel[0];
        Assert.assertEquals("dancinggoat", dancinggoat.getName());
        File[] thirdlevel = dancinggoat.listFiles();
        Assert.assertEquals(1, thirdlevel.length);
        File models = thirdlevel[0];
        Assert.assertEquals("models", models.getName());
        List<String> files = Arrays.asList(models.list());
        Assert.assertEquals(2, files.size());
        Assert.assertTrue(files.contains("Article.java"));
        Assert.assertTrue(files.contains("Brewer.java"));
    }
}
