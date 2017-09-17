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

package com.kenticocloud.generator.gradle

import org.apache.http.HttpHost
import org.apache.http.entity.InputStreamEntity
import org.apache.http.impl.bootstrap.ServerBootstrap
import org.apache.http.localserver.LocalServerTestBase
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class GenerateModelsFuncTest extends Specification {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()
    File buildFile
    File propertiesFile
    def localServer = new LocalServer()

    def setup() {
        buildFile = temporaryFolder.newFile('build.gradle')
        propertiesFile = temporaryFolder.newFile('gradle.properties')
        localServer.setUp()
    }

    def cleanup() {
        localServer.shutDown()
    }

    def "test that when the plugin is applied to a project and the generateModels task is executed, the sources are generated successfully"() {
        setup:
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c"

        localServer.getProtectedServerBootstrap().registerHandler(
                String.format("/%s/%s", projectId, "types"),
                { request, response, context ->
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentTypeList.json")
                            )
                    )
                })

        HttpHost httpHost = localServer.start()

        buildFile << '''
            plugins {
                id 'com.kenticocloud.generator'
            }
        '''

        buildFile << "\n"
        buildFile << "//This is sort of a hack, but ensuring test hits our mocked server\n"
        buildFile << "import com.kenticocloud.delivery.*\n"
        buildFile << "DeliveryOptions deliveryOptions = new DeliveryOptions()\n"
        buildFile << "deliveryOptions.setProductionEndpoint(\"${httpHost.toURI()}/%s\")\n"
        buildFile << "deliveryOptions.setProjectId(\"${projectId}\")\n"
        buildFile << "DeliveryClient client = new DeliveryClient(deliveryOptions)\n"

        buildFile << '''
            kenticoModel {
                projectId = '02a70003-e864-464e-b62c-e0ede97deb8c'
                classpath = 'com.dancinggoat.models'
                outputDir = file('generated-sources')
                deliveryClient = client
            }
        '''

        when:
        GradleRunner runner = GradleRunner.create()
                .withProjectDir(temporaryFolder.getRoot())
                .withArguments('generateModels', '--stacktrace', '--refresh-dependencies')
                .withPluginClasspath()
        BuildResult result =  runner.build()
        then:
        result.task(':generateModels').getOutcome() == TaskOutcome.SUCCESS
        new File(temporaryFolder.getRoot(), "generated-sources/com/dancinggoat/models/Article.java").exists()
        new File(temporaryFolder.getRoot(), "generated-sources/com/dancinggoat/models/Brewer.java").exists()
        new File(temporaryFolder.getRoot(), "generated-sources/com/dancinggoat/models").listFiles().length == 2
    }

    class LocalServer extends LocalServerTestBase {
        ServerBootstrap getProtectedServerBootstrap() {
            return this.serverBootstrap
        }
    }
}
