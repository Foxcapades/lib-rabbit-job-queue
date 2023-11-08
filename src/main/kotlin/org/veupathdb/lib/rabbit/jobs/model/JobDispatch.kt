package org.veupathdb.lib.rabbit.jobs.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.rabbit.jobs.serialization.Json
import org.veupathdb.lib.rabbit.jobs.serialization.JsonDeserializable
import org.veupathdb.lib.rabbit.jobs.serialization.JsonKey
import org.veupathdb.lib.rabbit.jobs.serialization.JsonSerializable
import java.time.OffsetDateTime

/**
 * Job Request/Dispatch
 *
 * Immutable struct representing a new job to execute.
 *
 * @constructor Constructs a new JobDispatch instance.
 *
 * @param jobID
 * Hash ID of the job to execute.
 *
 * @param type
 * Arbitrary type of the job payload.  Not used internally, intended to be used
 * by the worker node.
 *
 * If no type value is needed, an empty string may be used.
 *
 * @param body
 * Extra data for the job.  This value should not exceed 8kb in size when
 * serialized to JSON.  If a greater size payload is needed consider using an
 * external means to grant the worker access, such as the filesystem or a
 * database.
 *
 * If no body is needed, this value may be null.
 */
data class JobDispatch(
  val jobID:   HashID,
  val body:    JsonNode? = null,
  val type:    String = "",
  val attemptCount: Int = 0,
  val dispatched: OffsetDateTime = OffsetDateTime.now()
) :  JsonSerializable {
  override fun toJson() =
    Json.new<ObjectNode> {
      put(JsonKey.JobID, jobID.string)
      put(JsonKey.Type, type)
      put(JsonKey.Dispatched, dispatched.toString())
      put(JsonKey.AttemptCount, attemptCount)
      putPOJO(JsonKey.Body, body)
    }

  override fun toString() =
    "Job Dispatch: " + toJson().toPrettyString()

  companion object : JsonDeserializable<ObjectNode, JobDispatch> {
    @JvmStatic
    override fun fromJson(json: ObjectNode): JobDispatch {

      // Fields must be present
      if (!json.has(JsonKey.JobID))
        throw IllegalStateException("Job dispatch has no ${JsonKey.JobID} field!")
      if (!json.has(JsonKey.Type))
        throw IllegalStateException("Job dispatch has no ${JsonKey.Type} field!")
      if (!json.has(JsonKey.Body))
        throw IllegalStateException("Job dispatch has no ${JsonKey.Body} field!")
      if (!json.has(JsonKey.Dispatched))
        throw IllegalStateException("Job dispatch has no ${JsonKey.Dispatched} field!")
      if (!json.has(JsonKey.AttemptCount))
        throw IllegalStateException("Job dispatch has no ${JsonKey.AttemptCount} field!")

      // Only body field may be null
      if (json.get(JsonKey.JobID).isNull)
        throw IllegalStateException("Job dispatch has a null ${JsonKey.JobID} field!")
      if (json.get(JsonKey.Type).isNull)
        throw IllegalStateException("Job dispatch has a null ${JsonKey.Type} field!")
      if (json.get(JsonKey.Dispatched).isNull)
        throw IllegalStateException("Job dispatch has a null ${JsonKey.Dispatched} field!")
      if (json.get(JsonKey.AttemptCount).isNull)
        throw IllegalStateException("Job dispatch has a null ${JsonKey.AttemptCount} field!")

      // Fields must be the correct type
      if (!json.get(JsonKey.JobID).isTextual)
        throw IllegalStateException("Job dispatch has a non-textual ${JsonKey.JobID} field!")
      if (!json.get(JsonKey.Type).isTextual)
        throw IllegalStateException("Job dispatch has a non-textual ${JsonKey.Type} field!")
      if (!json.get(JsonKey.Dispatched).isTextual)
        throw IllegalStateException("Job dispatch has a non-textual ${JsonKey.Dispatched} field!")
      if (!json.get(JsonKey.AttemptCount).isInt)
        throw IllegalStateException("Job dispatch has a non-int ${JsonKey.AttemptCount} field!")

      return JobDispatch(
        jobID = HashID(json.get(JsonKey.JobID).textValue()),
        body = json.get(JsonKey.Body),
        type = json.get(JsonKey.Type).textValue(),
        dispatched = OffsetDateTime.parse(json.get(JsonKey.Dispatched).textValue()),
        attemptCount = json.get(JsonKey.AttemptCount).intValue()
      )
    }
  }
}
