package ch.bedag.eng.various.rcg;

import java.util.Locale;
import java.util.Map;

import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.languages.JavaClientCodegen;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;

/**
 * @author adrian.moos@bedag.ch
 */
public class SimpleJavaClientCodegen extends JavaClientCodegen {

    public SimpleJavaClientCodegen() {
        templateDir = "SimpleJava";
    }

    @Override
    public void processOpts() {
        super.processOpts();
        supportingFiles.clear();
    }

    @Override
    public CodegenOperation fromOperation(String path, String httpMethod, Operation operation,
            Map<String, Model> definitions, Swagger swagger) {
        CodegenOperation op = super.fromOperation(path, httpMethod, operation, definitions, swagger);
        op.httpMethod = op.httpMethod.toLowerCase(Locale.ROOT);
        return op;
    }

    @Override
    public String apiFileFolder() {
        return outputFolder + "/" + apiPackage().replace('.', '/');
    }

    @Override
    public String modelFileFolder() {
        return outputFolder + "/" + modelPackage().replace('.', '/');
    }
}
