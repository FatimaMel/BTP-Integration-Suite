import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.util.XmlSlurper;

def Message processData(Message message) {
    // Retrieve the body of the message
    def body = message.getBody(java.io.Reader);

    // Parse the XML
    def xml = new XmlSlurper().parse(body);

    def rootAction = message.getProperty('rootAction');

    // Determine the value of APIAction
    def APIAction = xml.Action.text().trim();

    // Set newAPIAction based on the value of APIAction
    def newAPIAction = APIAction ? APIAction : rootAction;

    message.setProperty('newAPIAction', newAPIAction.toLowerCase());

    return message;
}
