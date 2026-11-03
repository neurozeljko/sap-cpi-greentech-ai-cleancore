import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonSlurper

def Message processData(Message message) {
    def body = message.getBody(String)
    def jsonSlurper = new JsonSlurper()
    def aiResponse = jsonSlurper.parseText(body)

    double anomalyScore = (aiResponse.anomaly_score ?: 0.0) as double
    String fraudReason = aiResponse.reason ?: "Normal Operation"

    message.setProperty("AI_AnomalyScore", anomalyScore)
    message.setProperty("AI_FraudReason", fraudReason)

    def originalPayload = message.getProperty("Original_IoT_Payload")
    message.setBody(originalPayload)
    return message
}
