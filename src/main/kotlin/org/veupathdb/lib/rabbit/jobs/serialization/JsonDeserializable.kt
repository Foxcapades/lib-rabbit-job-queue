package org.veupathdb.lib.rabbit.jobs.serialization

import com.fasterxml.jackson.databind.JsonNode

/**
 * JSON Deserializable Type
 *
 * Represents a type that may be deserialized from JSON.
 */
interface JsonDeserializable<T : JsonNode, R> {

  /**
   * Converts the given JSON into an instance of the parent type.
   *
   * The return value of this method should be idempotent.
   *
   * @return A new instance of [R].
   */
  fun fromJson(json: T): R
}