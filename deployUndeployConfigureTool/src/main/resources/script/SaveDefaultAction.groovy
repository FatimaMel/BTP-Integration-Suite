import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper;

def Message processData(Message message) {
    // Parse input JSON
    def body = message.getBody(java.io.Reader);
    def data = new JsonSlurper().parse(body);

    def rootActionValue = data.Action;

    message.setProperty("rootAction", rootActionValue);

    return message;
}