package com.github.mckernant1.lol.blitzcrank.models

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbImmutable(builder = Prediction.Builder::class)
class Prediction(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("discordId")
    @get:DynamoDbSecondarySortKey(indexNames = [MATCH_ID_INDEX_NAME])
    val userId: String,
    @get:DynamoDbAttribute("matchId")
    @get:DynamoDbSortKey
    @get:DynamoDbSecondaryPartitionKey(indexNames = [MATCH_ID_INDEX_NAME])
    val matchId: String,
    val prediction: String,
) {
    override fun toString(): String = "[userId=$userId, matchId=$matchId, prediction=$prediction]"

    companion object {
        const val MATCH_ID_INDEX_NAME = "games-by-match-id-index"
        const val TABLE_NAME = "discord-predictions"
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private var matchId: String? = null
        fun matchId(value: String) = apply { matchId = value }

        private var discordId: String? = null
        fun userId(value: String) = apply { discordId = value }

        private var prediction: String? = null
        fun prediction(value: String) = apply { prediction = value }

        fun build() = Prediction(
            matchId = matchId!!,
            userId = discordId!!,
            prediction = prediction!!
        )
    }
}
