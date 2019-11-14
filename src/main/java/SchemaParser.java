import io.swagger.v3.oas.models.Components;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.oas.models.OpenAPI;

public class SchemaParser
{
    public static void main( String[] args )
    {
        OpenAPI openAPI = new OpenAPIV3Parser().read("./schemas/base.yaml");

        System.out.println("hello world");
    }
}
