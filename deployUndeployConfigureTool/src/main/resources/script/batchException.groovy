import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonOutput;

def Message processData(Message message) {
    def body = message.getBody(String);
    
    def errorMessages = message.getProperty("cumulativeErrorMessages") as List<String>;
    def artifactId = message.getProperty("ArtifactId");

    // Check if the body contains any HTTP status codes starting with 4 or 5
    if (body =~ /HTTP\/1\.1 [45]\d\d/) {
        def errorMessage = "The batch script for the artifact ${artifactId} processed with errors";
        errorMessages << errorMessage;
    }

    message.setProperty("cumulativeErrorMessages", errorMessages);
    
    // Set the final response body with the cumulative errors
    def response = [
        status: errorMessages.isEmpty() ? "Processed successfully" : "Processed with errors",
        errors: errorMessages as List
    ];
    
    message.setBody(JsonOutput.toJson(response));
    
    return message;
}
