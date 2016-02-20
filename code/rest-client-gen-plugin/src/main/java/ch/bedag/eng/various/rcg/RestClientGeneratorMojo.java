package ch.bedag.eng.various.rcg;

import io.swagger.codegen.ClientOptInput;
import io.swagger.codegen.ClientOpts;
import io.swagger.codegen.DefaultGenerator;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author adrian.moos@bedag.ch (based on https://github.com/garethjevans/swagger-codegen-maven-plugin )
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class RestClientGeneratorMojo extends AbstractMojo {

    /**
     * Location of the swagger spec, as URL or file.
     */
    @Parameter(name = "inputSpec", required = true)
    private File inputSpec;

    /**
     * Generated code is placed into this package.
     */
    @Parameter(name = "packageName", defaultValue = "io.swagger.client")
    private String packageName;

    /**
     * Location of the output directory.
     */
    @Parameter(name = "output", defaultValue = "${project.build.directory}/generated-test-sources/rest-client")
    private File output;

    /**
     * The project being built.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (output.exists()) {
                // make sure no files remain from previous runs
                FileUtils.cleanDirectory(output);
            }

            Swagger swagger = new SwaggerParser().read(inputSpec.getPath());

            SimpleJavaClientCodegen config = new SimpleJavaClientCodegen();
            config.setOutputDir(output.getAbsolutePath());
            config.setApiPackage(packageName + ".api");
            config.setModelPackage(packageName + ".model");

            ClientOptInput input = new ClientOptInput().opts(new ClientOpts()).swagger(swagger);
            input.setConfig(config);

            new DefaultGenerator().opts(input).generate();

            project.addTestCompileSourceRoot(output.toString());
        } catch (Exception e) {
            // as the eclipse maven plugin does not show exceptions to the user, we write it into a file that developers
            // may notice
            output.mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File(output, "error.txt")))) {
                pw.println("Code generation failed:");
                e.printStackTrace(pw);
            } catch (Throwable t) {
                e.addSuppressed(t);
            }

            // Maven logs exceptions thrown by plugins only if invoked with -e
            // I find it annoying to jump through hoops to get basic diagnostic information,
            // so let's log it in any case:
            getLog().error(e);
            throw new MojoExecutionException("Code generation failed. See above for the full exception.");
        }
    }
}
