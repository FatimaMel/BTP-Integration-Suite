import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper;

def Message processData(Message message) {
    // Parse input JSON
    def body = message.getBody(String);
    def jsonSlurper = new JsonSlurper();
    def jsonData = jsonSlurper.parseText(body);

    // Prepare batch script
    def batchScript = convertToBatchScript(jsonData, message);

    message.setBody(batchScript);

    // Set the Content-Type header
    def batchId = "batch_abc";
    message.setHeader("Content-Type", "multipart/mixed; boundary=${batchId}");

    return message;
}

def convertToBatchScript(def jsonData, Message message) {
    def batchId = "batch_abc";
    def batchScript = new StringBuilder();

    // Start batch
    batchScript.append("--${batchId}\r\n");

    def artifacts = jsonData.Artifacts;
    
    // Ensure Artifacts is a list, even if it's a single object
    if (!(artifacts instanceof List)) {
        artifacts = [artifacts];
    }

    artifacts.eachWithIndex { artifact, index ->
        // Ensure the artifact has an Id property
        if (!artifact.containsKey('Id')) {
            throw new MissingPropertyException("Id", artifact.getClass());
        }
        
        def changesetId = "changeset_${index}";
        def artifactId = artifact.Id;
        
        message.setProperty("ArtifactId", artifactId);

        // Start changeset for the artifact
        batchScript.append("Content-Type: multipart/mixed; boundary=${changesetId}\r\n\r\n");

        def parameters = artifact.Parameters;
        
        // Ensure Parameters is a list, even if it's a single object
        if (!(parameters instanceof List)) {
            parameters = [parameters];
        }

        parameters.each { param ->
            def paramKey = param.ParameterKey;
            def paramValue = param.ParameterValue;
            
            // Add each parameter to the batch script
            batchScript.append("--${changesetId}\r\n");
            batchScript.append("Content-Type: application/http\r\n");
            batchScript.append("Content-Transfer-Encoding: binary\r\n\r\n");
            batchScript.append("PUT IntegrationDesigntimeArtifacts(Id='${artifactId}',Version='active')/\$links/Configurations('${paramKey}') HTTP/1.1\r\n");
            batchScript.append("Accept: application/json\r\n");
            batchScript.append("Content-Type: application/json\r\n\r\n");
            batchScript.append("{\r\n");
            batchScript.append("  \"ParameterKey\": \"${paramKey}\",\r\n");
            batchScript.append("  \"ParameterValue\": \"${paramValue}\",\r\n");
            batchScript.append("  \"DataType\": \"xsd:string\"\r\n");
            batchScript.append("}\r\n\r\n");
        }

        // Close changeset
        batchScript.append("--${changesetId}--\r\n\r\n");

        // Only append boundary if it's not the last artifact
        if (index < artifacts.size() - 1) {
            batchScript.append("--${batchId}\r\n");
        }
    }

    // Close batch
    batchScript.append("--${batchId}--\r\n");

    return batchScript.toString();
}
