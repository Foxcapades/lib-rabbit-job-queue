package org.veupathdb.lib.rabbit.jobs.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.rabbit.jobs.serialization.Json
import org.veupathdb.lib.rabbit.jobs.serialization.JsonDeserializable
import org.veupathdb.lib.rabbit.jobs.serialization.JsonKey
import org.veupathdb.lib.rabbit.jobs.serialization.JsonSerializable

/**
 * Job Error Notification
 *
 * @constructor Constructs a new ErrorNotification instance.
 *
 * @param jobID
 * Hash ID of the job that failed.
 *
 * @param code
 * Error/response code of the job failure.
 *
 * If the error has no associated code, set this value to `0`.
 *
 * @param message
 * Optional error message for the job failure.
 */
data class ErrorNotification @JvmOverloads constructor(
  val jobID:         HashID,
  val code:          Int,
  val attemptCount:  Int? = 0,
  val message:       String? = null,
  val body:          JsonNode? = null,
) : JsonSerializable {
  override fun toJson(): JsonNode =
    Json.new<ObjectNode> {
      put(JsonKey.JobID, jobID.string)
      put(JsonKey.Code, code)
      put(JsonKey.Message, message)
      put(JsonKey.AttemptCount, attemptCount)
      putPOJO(JsonKey.Body, body)
    }

  override fun toString() =
    "ErrorNotification: " + toJson().toPrettyString()

  companion object : JsonDeserializable<ObjectNode, ErrorNotification> {
    @JvmStatic
    override fun fromJson(json: ObjectNode): ErrorNotification {
      if (!json.has(JsonKey.JobID))
        throw IllegalStateException("Error notification has no ${JsonKey.JobID} field!")
      if (!json.has(JsonKey.Code))
        throw IllegalStateException("Error notification has no ${JsonKey.Code} field!")
      if (!json.has(JsonKey.Body))
        throw IllegalStateException("Error notification has no ${JsonKey.Body} field!")

      if (json.get(JsonKey.JobID).isNull)
        throw IllegalStateException("Error notification has a null ${JsonKey.JobID} field!")
      if (json.get(JsonKey.Code).isNull)
        throw IllegalStateException("Error notification has a null ${JsonKey.Code} field!")
      if (json.get(JsonKey.Body).isNull)
        throw IllegalStateException("Error notification has a null ${JsonKey.Body} field!")

      if (!json.get(JsonKey.JobID).isTextual)
        throw IllegalStateException("Error notification has a non-textual ${JsonKey.JobID} field!")
      if (!json.get(JsonKey.Code).isIntegralNumber)
        throw IllegalStateException("Error notification has a non-integral ${JsonKey.Code} field!")

      return ErrorNotification(
        jobID = HashID(json.get(JsonKey.JobID).textValue()),
        code = json.get(JsonKey.Code).intValue(),
        message = json.get(JsonKey.Message).textValue(),
        attemptCount = json.get(JsonKey.AttemptCount).intValue(),
        body = json.get(JsonKey.Body),
      )
    }
  }
}
