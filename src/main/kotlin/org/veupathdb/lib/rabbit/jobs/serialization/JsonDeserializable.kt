package org.veupathdb.lib.rabbit.jobs.serialization

import com.fasterxml.jackson.databind.JsonNode

interface JsonDeserializable<T : JsonNode, R> {
  fun fromJson(json: T): R
}