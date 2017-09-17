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

import com.kenticocloud.delivery.DeliveryClient
import com.kenticocloud.generator.CodeGenerator
import com.squareup.javapoet.JavaFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenerateModels extends DefaultTask {

    GenerateModels() {
        setDescription("Generates source files from your Kentico Cloud project.")
        setGroup("KenticoCloud")
    }

    @TaskAction
    void perform() throws IOException {
        String projectId = project.kenticoModel.projectId
        String packageName = project.kenticoModel.packageName
        File outputDir = project.kenticoModel.outputDir
        DeliveryClient deliveryClient = project.kenticoModel.deliveryClient
        CodeGenerator codeGenerator = new CodeGenerator(projectId, packageName, outputDir)
        List<JavaFile> sources =
                deliveryClient == null ? codeGenerator.generateSources() : codeGenerator.generateSources(deliveryClient)
        codeGenerator.writeSources(sources)
    }
}
