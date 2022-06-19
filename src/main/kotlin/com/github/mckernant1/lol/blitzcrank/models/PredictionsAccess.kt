package com.github.mckernant1.lol.blitzcrank.models

import kotlin.streams.asSequence
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class PredictionsAccess(
    ddbClient: DynamoDbClient,
    tableName: String = Prediction.TABLE_NAME,
    private val matchIndexName: String = Prediction.MATCH_ID_INDEX_NAME
) {

    private val enhanced: DynamoDbEnhancedClient = DynamoDbEnhancedClient
        .builder()
        .dynamoDbClient(ddbClient)
        .build()

    private val table = enhanced.table(
        tableName,
        TableSchema.fromImmutableClass(Prediction::class.java)
    )


    fun getItem(userId: String, matchId: String): Prediction? =
        table.getItem(Key.builder().partitionValue(userId).sortValue(matchId).build())

    fun putItem(prediction: Prediction) = table.putItem(prediction)

    fun scan(): Iterable<Prediction> = table.scan().items()

    fun getAllPredictionsForMatch(matchId: String): Sequence<Prediction> =
        table.index(matchIndexName).query { it ->
            it.queryConditional(
                QueryConditional.keyEqualTo(
                    Key.builder().partitionValue(matchId).build()
                )
            )
        }.stream().flatMap { it.items().stream() }.asSequence()


}
