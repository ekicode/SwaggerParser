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
     *
     * @param fileName name of the schema yaml file
     */
    public SchemaParser(String fileName){
        _openAPI = new OpenAPIV3Parser().read(fileName);
    }

    /**
     * Return the list of parameters given the path item (an endpoint)
     *
     * @param pathItem Endpoint defined as a PathItem object
     * @return set of parameters for the specified endpoint
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
     * parse the entire schema defined in yaml file and return the schema object
     *
     * @return object representing the schema defined in the yaml file
     */
    public Schema parseSchema() {
        Schema parsedSchema = new Schema();
        Paths paths = _openAPI.getPaths();
        for (String endpoint : paths.keySet()) {
            parsedSchema.addEndpoint(endpoint, parseEndpoint(paths.get(endpoint)));
        }
        return parsedSchema;
    }

    private class Schema {
        Map<String, Set<String>> _schema;

        /**
         * Constructor for Schema
         */
        public Schema() {
            _schema = new HashMap<>();
        }

        /**
         * Add endpoint with the set of parameters to the schema
         *
         * @param endpoint name of the endpoint
         * @param parameters set of parameters for the endpoint
         */
        public void addEndpoint(String endpoint, Set<String> parameters) {
            _schema.put(endpoint, parameters);
        }

        /**
         * Get all the endpoints of the schema as a set of string
         */
        public Set<String> getEndpoints() {
            return _schema.keySet();
        }

        /**
         * Get all the parameters of the given endpoint
         *
         * @param endpoint name of the endpoint
         */
        public Set<String> getParameters(String endpoint) {
            return _schema.get(endpoint);
        }

        /**
         * Override toString method for pretty-printing schema
         */
        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (String endpoint : getEndpoints()) {
                stringBuilder.append(String.format("\nEndpoint %s\n", endpoint));
                int parameterIndex = 0;
                for (String parameter : getParameters(endpoint)) {
                    stringBuilder.append(String.format("parameter #%d %s\n", ++parameterIndex, parameter));
                }
            }
            return stringBuilder.toString();
        }
    }

    /**
     * Main method to parse the schema and print the parameters for each endpoint
     */
    public static void main(String[] args) {
        SchemaParser schemaParser = new SchemaParser("./schemas/schema.yaml");
        Schema parsedSchema = schemaParser.parseSchema();
        System.out.println(parsedSchema);
    }
}
