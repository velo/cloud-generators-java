package com.kenticocloud.generator.maven;

import com.kenticocloud.generator.CodeGenerator;
import com.squareup.javapoet.JavaFile;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Mojo(name = "manifest-validator", defaultPhase = LifecyclePhase.VERIFY)
public class GenerateModelsMojo extends AbstractMojo
{

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(required = true)
    private String projectId;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/kenticocloud")
    private File outputDirectory;

    @Parameter(required = true)
    private String packageName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        outputDirectory.mkdirs();

        try
        {
            final CodeGenerator codeGenerator = new CodeGenerator(projectId, packageName, outputDirectory);
            final List<JavaFile> sources = codeGenerator.generateSources();
            codeGenerator.writeSources(sources);
        }
        catch (final IOException e)
        {
            throw new MojoFailureException("Error while generating sources", e);
        }

        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

}
