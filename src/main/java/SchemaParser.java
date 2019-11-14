import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.*;

public class SchemaParser {
    private OpenAPI _openAPI;

    /**
     * Constructor for SchemaParser
     */
    public SchemaParser(String fileName){
        _openAPI = new OpenAPIV3Parser().read(fileName);
    }

    /**
     * return the list of parameters given the path item (an endpoint)
     */
    public static Set<String> parseEndpoint(PathItem pathItem) throws IllegalArgumentException {
        List<Parameter> parameterList;
        Set<String> parameterSet = new HashSet<>();
        if (pathItem.getGet() != null) {
            parameterList = pathItem.getGet().getParameters();
        } else if (pathItem.getPost() != null) {
            parameterList = pathItem.getPost().getParameters();
        } else {
            throw new IllegalArgumentException("Schema Parser does not support HTTP methods other than GET/POST");
        }

        for (Parameter parameter : parameterList) {
            parameterSet.add(parameter.getName());
        }
        return parameterSet;
    }

    /**
     * parse the entire schema defined in yaml file and return map of endpoints and parameters
     */
    public Map<String, Set<String>> parseSchema() {
        // map to be returned; key: endpoint name, value: set of parameters
        Map<String, Set<String>> parsedSchema = new HashMap<>();

        Paths paths = _openAPI.getPaths();
        for (String endpoint : paths.keySet()) {
            System.out.println("Parsing endpoint " + endpoint);
            parsedSchema.put(endpoint, parseEndpoint(paths.get(endpoint)));
        }
        return parsedSchema;
    }

    /**
     * Main method to parse the schema and print the parameters for each endpoint
     */
    public static void main( String[] args ) {
        SchemaParser schemaParser = new SchemaParser("./schemas/schema.yaml");
        Map<String, Set<String>> parsedSchema = schemaParser.parseSchema();
        for (String endpoint : parsedSchema.keySet()) {
            System.out.println("\nEndpoint " + endpoint);
            int parameterIndex = 0;
            for (String parameter : parsedSchema.get(endpoint)) {
                System.out.println(String.format("parameter #%d %s", ++parameterIndex, parameter));
            }
        }
    }
}
